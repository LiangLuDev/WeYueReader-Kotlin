package com.aku.weyue.ui.scan

import android.annotation.SuppressLint
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aku.weyue.data.BookBean
import com.aku.weyue.data.BookFile
import com.aku.weyue.data.repository.BookRepository
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.Utils
import kotlinx.coroutines.*
import java.io.File

/**
 * @author Zsc
 * @date   2019/6/2
 * @desc
 */
class LocalBookViewModel : ViewModel() {
    //本地txt列表
    val bookFilesLiveData = MutableLiveData<List<BookFile>>()
    //sd卡目录
    val bookLayerFilesLiveData = MutableLiveData<List<BookFile>>()
    //本地已收藏的txt
    val localBookIdsLiveData = MutableLiveData<List<String>>()

    /**
     * 直接从文件库读取文件，效率很高
     */
    @SuppressLint("Recycle")
    fun loadBooksFromCursor(): Job {
        val time00 = System.currentTimeMillis()
        return viewModelScope.launch(context = Dispatchers.Default) {
            val uri = MediaStore.Files.getContentUri("external")
            val selection = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.txt' and " +
                    MediaStore.Files.FileColumns.SIZE + " > 0)"
            val txtProjection = arrayOf(
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_MODIFIED
            )
            val cursor = Utils.getApp().contentResolver.query(
                uri,
                txtProjection,
                selection,
                null,
                MediaStore.Files.FileColumns.SIZE + " DESC"
            )!!
            LogUtils.d("查询耗时:${System.currentTimeMillis() - time00}")
            val count = cursor.count
            val listPath = mutableListOf<String>()
            withContext(Dispatchers.IO) {
                while (cursor.moveToNext()) {
                    val time0 = System.currentTimeMillis()
                    val title = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE))!!
                    val time = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED))
                    val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE))
                    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                    LogUtils.d(
                        """path:$path
                    title:$title
                    time:${TimeUtils.millis2String(time)}
                    size:$size
                    单条耗时:${System.currentTimeMillis() - time0}"""
                    )
                    listPath.add(path)
                }
            }
            cursor.close()
            val bookFiles = listPath.map {
                BookFile(File(it))
            }
            bookFilesLiveData.postValue(bookFiles)
            LogUtils.d("结束条数：$count")
            LogUtils.d("结束耗时：${System.currentTimeMillis() - time00}")
        }
    }

    fun refreshLocalBooks() {
        viewModelScope.launch {
            localBookIdsLiveData.postValue(BookRepository.getAllLocalPath())
        }

    }

    fun loadBooks(fileDir: File) {
        viewModelScope.launch {
            val t0 = System.currentTimeMillis()
            val list = FileUtils
                .listFilesInDir(fileDir)
                .map { BookFile(it) }
                .toMutableList()
            val t1 = System.currentTimeMillis()
            //协程并行，结束时间为最长的协程执行时间
            list.forEach {
                async(Dispatchers.Default) {
                    it.checkCount()
                    (System.currentTimeMillis() - t1).let {
                        LogUtils.d("内部耗时:$it")
                    }
                }.start()
            }
            LogUtils.d("外部耗时:${t1 - t0}")
            bookLayerFilesLiveData.postValue(sortList(list))
        }
    }

    private suspend fun sortList(list: List<BookFile>): List<BookFile> {
        return withContext(Dispatchers.IO) {
            list.sortedWith(compareBy({ it.file.isDirectory.not() }
                , { it.name }))
        }
    }


    fun addShelf(bookFiles: List<BookFile>): Job {
        val books = bookFiles.map {
            BookBean().apply {
                _id = it.file.path
                isLocal = true
            }
        }
        return viewModelScope.launch {
            BookRepository.insertAll(books)
        }

    }


    @Deprecated("使用 loadBooksFromCursor")
    fun loadBooks1() {
        val time00 = System.currentTimeMillis()
        viewModelScope.launch {
            listTxtAsync(this, Environment.getExternalStorageDirectory())
        }.invokeOnCompletion {
            if (it is CancellationException) {
                LogUtils.d("取消耗时：${System.currentTimeMillis() - time00}")
            } else {
                LogUtils.d("结束耗时：${System.currentTimeMillis() - time00}")
                //            val list = mutableListOf<File>()
                try {
                    val time0 = System.currentTimeMillis()
//                listTxtAsync(Environment.getExternalStorageDirectory())
                    LogUtils.d("总耗时：${System.currentTimeMillis() - time0}")
                } catch (e: Throwable) {
                    LogUtils.d(e.message)
                }
            }
        }
    }

    private suspend fun listTxtAsync(
        scope: CoroutineScope,
        fileDir: File
    ): Job {
        //使用withContext可以在协程取消的时候关闭子协程
        return scope.async(Dispatchers.Default) {
            val time0 = System.currentTimeMillis()
            val listAll = fileDir.listFiles() ?: arrayOf()
            val listTxt = listAll.filter {
                it.extension.equals("txt", true)
            }.map { BookFile(it) }
            if (listTxt.isNotEmpty()) {
                bookFilesLiveData.postValue(listTxt)
            }
            listAll.filter {
                it.isDirectory
            }.forEach {
                //使用递归获取所有的txt文件
                listTxtAsync(scope, it).start()
            }
            LogUtils.d("内部耗时${fileDir.path}：${System.currentTimeMillis() - time0}")
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

}