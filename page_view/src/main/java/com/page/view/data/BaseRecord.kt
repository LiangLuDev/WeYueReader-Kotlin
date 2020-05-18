package com.page.view.data

/**
 * @author Zsc
 * @date   2019/5/25
 * @desc
 */
interface BaseRecord {
    //所属的书的id
    var bookId: String
    //阅读到了第几章
    var chapter: Int
    //当前的页码
    var pagePos: Int
}