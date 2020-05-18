package com.aku.weyue.data.local

import androidx.room.Dao
import androidx.room.Query
import com.aku.weyue.data.BookBean

/**
 * @author Zsc
 * @date   2019/5/18
 * @desc
 */
@Dao
interface BookDao : BaseDao<BookBean> {

    @Query("SELECT * FROM book WHERE _id=:bookId")
    fun findOne(bookId: String): BookBean?

    @Query("SELECT * FROM book")
    fun findAll(): List<BookBean>

    @Query("SELECT _id FROM book WHERE isLocal=:isLocal")
    fun getAllLocalPath(isLocal: Boolean = true): List<String>

}