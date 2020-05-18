package com.aku.weyue.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aku.weyue.data.BookType

/**
 * @author Zsc
 * @date   2019/5/18
 * @desc
 */
@Dao
interface BookTypeDao:BaseDao<BookType> {

    @Query("SELECT * FROM book_type")
    fun getAll(): List<BookType>

}