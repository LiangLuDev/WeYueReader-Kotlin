package com.aku.weyue.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author Zsc
 * @date   2019/4/29
 * @desc
 */
@Entity(tableName = "book_type")
class BookType {
    @PrimaryKey
    @ColumnInfo
    var name: String = ""
    @ColumnInfo
    var bookCount: String? = null
    @ColumnInfo
    var monthlyCount: String? = null
    @ColumnInfo
    var icon: String? = null
    /**
     * 自定义字段
     * 0->男生
     * 1->女生
     * 2->出版
     */
    @ColumnInfo
    var type: Int = 0
}