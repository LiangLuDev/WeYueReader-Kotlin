package com.aku.weyue.data.repository

import com.aku.aac.kchttp.data.BaseResult
import com.aku.aac.kchttp.config.ApiErrorCode
import com.aku.aac.kchttp.ext.*
import com.aku.weyue.api.BookApi
import com.aku.weyue.data.BookTotal
import com.aku.weyue.data.BookType
import com.aku.weyue.data.local.BookTypeDao
import com.blankj.utilcode.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * @author Zsc
 * @date   2019/5/18
 * @desc
 */
object BookTypeRepository : KoinComponent {

    private val bookTypeDao: BookTypeDao by inject()
    private val bookApi: BookApi by inject()

    /**
     * 获取书籍分类
     */
    private fun getAllBookType(): List<BookType> {
        return bookTypeDao.getAll()
    }

    /**
     * 储存数据
     */
    private fun insertAll(bookTypes: List<BookType>) {
        bookTypeDao.insertAll(bookTypes)
    }

    /**
     * 异步加载获取并缓存数据
     */
    suspend fun loadBookTotal(): BaseResult<BookTotal> {
        if (NetworkUtils.isConnected()) {
            return bookApi.bookClassifyAsync()
                .awaitResult()
                .apply {
                    //异步缓存数据
                    withContext(Dispatchers.IO) {
                        doSuccess {
                            val list = mutableListOf<BookType>()
                            list.addAll(it.male.apply {
                                forEach { it1 -> it1.type = 0 }
                            })
                            list.addAll(it.female.apply {
                                forEach { it1 -> it1.type = 1 }
                            })
                            list.addAll(it.press.apply {
                                forEach { it1 -> it1.type = 2 }
                            })
                            insertAll(list)
                        }
                    }
                }
        } else {
            //异步读取数据，room必须要io线程
            return withContext(Dispatchers.IO) {
                val list = getAllBookType()
                if (list.isEmpty()) {
                    createErrorResult(ApiErrorCode.NO_NET_WORK, "")
                } else {
                    BookTotal()
                        .run {
                            male.addAll(list.filter { it.type == 0 })
                            female.addAll(list.filter { it.type == 1 })
                            press.addAll(list.filter { it.type == 2 })
                            createResult(this)
                        }
                }
            }

        }

    }

}
