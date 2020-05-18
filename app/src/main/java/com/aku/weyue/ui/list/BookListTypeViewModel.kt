package com.aku.weyue.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aku.aac.kchttp.ext.awaitResult
import com.aku.aac.kchttp.ext.doError
import com.aku.aac.kchttp.ext.doSuccess
import com.aku.weyue.api.BookApi
import com.aku.weyue.data.BookBean
import kotlinx.coroutines.launch

/**
 * @author Zsc
 * @date   2019/5/3
 * @desc
 */
class BookListTypeViewModel(
    private val bookApi: BookApi
) : ViewModel() {

    var curPage = 1

    val books = MutableLiveData<List<BookBean>>()

    val status = MutableLiveData(false)

    fun load(
        type: String,
        major: String,
        page: Int
    ) {
        viewModelScope.launch {
            bookApi.booksAsync(type, major, page)
                .awaitResult()
                .doSuccess {
                    curPage = page
                    status.postValue(true)
                    books.postValue(it)
                }.doError {
                    curPage = page
                    status.postValue(false)
                }

        }
    }
}