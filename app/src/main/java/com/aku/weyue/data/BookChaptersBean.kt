package com.aku.weyue.data

/**
 * Created by Liang_Lu on 2017/12/11.
 */

class BookChaptersBean {

    var _id: String? = null
    var source: String? = null
    var book: String? = null
    var chapters: MutableList<ChatpterBean> = mutableListOf()

    class ChatpterBean {
        var _id: String? = null
        var isVip: Boolean = false
        var link: String? = null
        var title: String? = null
        var isRead: Boolean = false
    }

}
