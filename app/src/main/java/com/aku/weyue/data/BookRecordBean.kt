package com.aku.weyue.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.page.view.data.BaseRecord

/**
 * Created by LiangLu on 17-11-22.
 * 阅读记录 fixme 不属于控件必须要的
 */
@Entity(tableName = "book_record")
class BookRecordBean : BaseRecord {
    //所属的书的id
    @PrimaryKey
    override var bookId: String = ""
    //阅读到了第几章
    override var chapter: Int = 0
    //当前的页码
    override var pagePos: Int = 0

}
