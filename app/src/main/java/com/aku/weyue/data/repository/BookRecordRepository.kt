package com.aku.weyue.data.repository

import com.aku.weyue.data.BookRecordBean
import com.aku.weyue.data.local.BookRecordDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * @author Zsc
 * @date   2019/5/18
 * @desc
 */
object BookRecordRepository : KoinComponent {

    private val bookRecordDao: BookRecordDao by inject()

    suspend fun getAll(): List<BookRecordBean> {
        return withContext(Dispatchers.IO) {
            bookRecordDao.getAll()
        }
    }

    suspend fun insert(bookRecord: BookRecordBean) {
        withContext(Dispatchers.IO) {
            bookRecordDao.insert(bookRecord)
        }
    }

    suspend fun insertAll(list: List<BookRecordBean>) {
        withContext(Dispatchers.IO) {
            bookRecordDao.insertAll(list)
        }
    }

}