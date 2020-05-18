package com.aku.weyue.ui.read

import com.aku.aac.kchttp.ext.awaitResult
import com.aku.aac.kchttp.ext.doError
import com.aku.aac.kchttp.ext.doSuccess
import com.aku.weyue.api.BookApi
import com.aku.weyue.data.ChapterContentBean
import com.blankj.utilcode.util.ToastUtils
import com.page.view.data.TxtChapter
import kotlinx.coroutines.*

/**
 * @author Zsc
 * @date   2019/5/4
 * @desc
 */
object ReadHelper {


    fun loadChapters(bookApi: BookApi, bookId: String, iBookChapters: IBookChapters) {
        val a = bookApi.bookChaptersAsync(bookId)
        GlobalScope.launch(Dispatchers.IO) {
            val result = a.awaitResult()
            withContext(Dispatchers.Main) {
                result.doSuccess {
                    iBookChapters.bookChapters(it)
                }.doError {
                    ToastUtils.showShort("加载失败")
                }
            }
        }
    }


    fun loadContent(
        bookApi: BookApi, bookId: String,
        bookChapterList: List<TxtChapter>
        , iBookChapters: IBookChapters
    ) {
        //取消上次的任务，防止多次加载
        //首先判断是否Chapter已经存在
        val listDeferred = mutableListOf<Deferred<ChapterContentBean>>()
        listDeferred.addAll(
            bookChapterList.map {
                bookApi.bookContentAsync(it.link ?: "")
            }
        )
        GlobalScope.launch(Dispatchers.IO) {
            val list = listDeferred.awaitAll()
            withContext(Dispatchers.Main) {
                if (list.any {
                        it.chapter.cpContent.isNullOrEmpty()
                    }) {
                    iBookChapters.errorChapters()
                } else {
                    list.forEach {
                        BookCacheUtils.saveChapterInfo(bookId, it.chapter.title, it.chapter.cpContent ?: "")
                    }
                    iBookChapters.finishChapters()
                }
            }
        }
    }


}