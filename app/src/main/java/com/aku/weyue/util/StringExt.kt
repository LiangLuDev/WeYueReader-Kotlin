package com.aku.weyue.util

import com.aku.weyue.api.BookApi

/**
 * @author Zsc
 * @date   2019/5/3
 * @desc
 */
fun String?.toBookPic(): String? {
    return this?.let {
        BookApi.BOOK_IMG_URL + it
    }
}

