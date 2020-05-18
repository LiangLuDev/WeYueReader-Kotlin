package com.aku.weyue.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aku.weyue.data.BookRecordBean

/**
 * @author Zsc
 * @date   2019/5/18
 * @desc
 */
@Dao
interface BookRecordDao:BaseDao<BookRecordBean> {

    @Query("SELECT * FROM book_record")
    fun getAll(): List<BookRecordBean>

}