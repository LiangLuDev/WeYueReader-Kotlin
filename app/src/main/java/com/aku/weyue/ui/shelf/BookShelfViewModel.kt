package com.aku.weyue.ui.shelf

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aku.weyue.data.BookBean
import com.aku.weyue.data.repository.BookRepository
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.Utils
import com.page.view.utils.Constant
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File


/**
 * @author Zsc
 * @date   2019/5/18
 * @desc
 */
class BookShelfViewModel : ViewModel() {
    //当前加载的书籍
    val books = MutableLiveData<List<BookBean>>()
    //当前状态是否加载成功

    fun loadBookShelf() {
        viewModelScope.launch {
            books.postValue(BookRepository.getAll())
        }
    }


    fun removeBookShelf(bookBean: BookBean, clearLocal: Boolean)
            : Job {

        return viewModelScope.launch {
            BookRepository.delete(bookBean)
            if (clearLocal) {
                val filePath = if (bookBean.isLocal) {
                    bookBean._id
                } else {
                    Constant.BOOK_CACHE_PATH + bookBean._id
                }
                FileUtils.delete(filePath)
                notifySystemToScan(filePath)
            }

        }

    }

    private fun notifySystemToScan(filePath: String) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val file = File(filePath)
        val uri = Uri.fromFile(file)
        intent.data = uri
        Utils.getApp().sendBroadcast(intent)
    }

}