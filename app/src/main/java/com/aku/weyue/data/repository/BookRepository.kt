package com.aku.weyue.data.repository

import com.aku.weyue.api.BookApi
import com.aku.weyue.data.BookBean
import com.aku.weyue.data.BookRecordBean
import com.aku.weyue.data.local.BookDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * @author Zsc
 * @date   2019/5/18
 * @desc
 */
object BookRepository : KoinComponent {

    private val bookDao: BookDao by inject()

    private val bookApi: BookApi by inject()

    suspend fun insert(bookBean: BookBean) {
        return withContext(Dispatchers.IO) {
            bookDao.insert(bookBean)
        }
    }

    suspend fun delete(bookBean: BookBean) {
        return withContext(Dispatchers.IO) {
            bookDao.delete(bookBean)
        }
    }

    suspend fun getAll(): List<BookBean> {
        return withContext(Dispatchers.IO) {
            bookDao.findAll()
        }
    }

    suspend fun getAllLocalPath():List<String>{
        return withContext(Dispatchers.IO){
            bookDao.getAllLocalPath()
        }
    }

    suspend fun insertAll(list: List<BookBean>) {
        withContext(Dispatchers.IO) {
            bookDao.insertAll(list)
        }
    }

    suspend fun isCollect(bookId: String): Boolean {
        return withContext(Dispatchers.IO) {
            bookDao.findOne(bookId) != null
        }

    }


}