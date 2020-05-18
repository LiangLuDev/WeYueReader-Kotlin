package com.page.view.data

/**
 * 某一章的书本页
 */
class TxtPage {
    var position: Int = 0
    //标题
    var title: String? = null
    //当前 lines 中为 title 的行数。
    var titleLines: Int = 0
    //行数
    var lines: MutableList<String>? = null
}
