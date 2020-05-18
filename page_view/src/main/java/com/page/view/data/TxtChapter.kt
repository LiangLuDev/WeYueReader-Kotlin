package com.page.view.data

/**
 * 章节内容
 */
class TxtChapter {
    //章节所属的小说(网络)
    var bookId: String? = null
    //章节的链接(网络)
    var link: String? = null
    //章节名(共用)
    var title: String? = null
    //章节内容在文章中的起始位置(本地)
    var start: Long = 0
    //章节内容在文章中的终止位置(本地)
    var end: Long = 0
    //选中目录
    var isSelect: Boolean = false

}
