package com.aku.weyue.ui.list

import com.aku.weyue.api.BookApi
import com.aku.weyue.data.BookBean

/**
 * @author Zsc
 * @date   2019/5/3
 * @desc
 */
class BookInfoViewModel(
   book: BookBean
) {
    var title: String? = book.title
    var author: String? = book.author
    var longIntro: String? = book.longIntro
    var cover: String? = book.cover?.run {
        BookApi.BOOK_IMG_URL + this
    }
    var wordCountStr: String = book.wordCount.toString()

    var retentionRatio: String? = book.retentionRatio

}