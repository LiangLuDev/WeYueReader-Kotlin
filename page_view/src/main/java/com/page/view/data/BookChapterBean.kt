package com.page.view.data


import java.io.Serializable

/**
 * 书本章节列表信息
 */
class BookChapterBean : Serializable {
    /**
     * title : 第一章 他叫白小纯
     * link : http://read.qidian.com/chapter/rJgN8tJ_cVdRGoWu-UQg7Q2/6jr-buLIUJSaGfXRMrUjdw2
     * unreadble : false
     */
    //所属的书籍
    var bookId: String? = null
    //链接是唯一的
    var link: String? = null

    var title: String? = null

    //所属的下载任务
    var taskName: String? = null

    var unreadble: Boolean = false




}
