package com.aku.weyue.ui.type

import androidx.lifecycle.MutableLiveData
import com.aku.weyue.data.BookType

/**
 * @author Zsc
 * @date   2019/5/2
 * @desc
 */
class BookTypeViewModel(
    bookType: BookType
) {
    val book = MutableLiveData(bookType)
//    val name: String? = bookType.name
//    val bookCount: String? = bookType.bookCount + "本"
//    val monthlyCount: String? = bookType.monthlyCount
//    val icon: String? = bookType.icon

}