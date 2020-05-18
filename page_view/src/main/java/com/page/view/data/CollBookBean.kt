package com.page.view.data

import java.io.Serializable

/**
 * 书本信息
 */
class CollBookBean : Serializable {
    var bookId: String = ""//如果是本地文件，那么id为所在的地址
    //最后阅读时间
    var lastRead: String? = null
    var lastChapter: String? = null
    //是否更新或未阅读(已阅读为false)
    var isUpdate = true
    //是否是本地文件
    var isLocal = false
    //书籍章节
    var bookChapters: MutableList<BookChapterBean>? = null
}