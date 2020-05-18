package com.aku.weyue.ui.book

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aku.aac.kchttp.ext.awaitResult
import com.aku.aac.kchttp.ext.doError
import com.aku.aac.kchttp.ext.doSuccess
import com.aku.common.widget.StateLayout
import com.aku.weyue.api.BookApi
import com.aku.weyue.data.BookBean
import com.aku.weyue.data.repository.BookRepository
import com.blankj.utilcode.util.TimeUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

/**
 * @author Zsc
 * @date   2019/5/3
 * @desc
 */
class BookDetailViewModel(
    private val bookId: String,
    private val bookApi: BookApi
) : ViewModel() {
    val status = MutableLiveData<Int>()
    val book = MutableLiveData<BookBean>()
    val collect = MutableLiveData<Boolean>(false)

    init {
        viewModelScope.launch {
            collect.postValue(BookRepository.isCollect(bookId))
        }
    }

    fun loadData() {
        status.postValue(StateLayout.Loading)
        viewModelScope.launch {
            val bookResult = bookApi.bookInfoAsync(bookId).awaitResult()
            bookResult.doSuccess {
                status.postValue(StateLayout.Success)
                book.postValue(it)
            }.doError {
                status.postValue(StateLayout.Error)
            }
        }
    }

    fun changeCollect() {
        viewModelScope.launch {
            val isCollect = collect.value!!
            if (isCollect) {
                BookRepository.delete(book.value!!)
            } else {
                BookRepository.insert(book.value!!)
            }
            collect.postValue(!isCollect)
        }

    }


    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    companion object {

        @SuppressLint("SimpleDateFormat")
        private var SDF = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        @JvmStatic
        fun loadCountTime(count: Int, time: String?): String {
            val countString = when {
                count / 10000 > 0 -> "${count / 10000}万字"
                else -> "${count}字"
            }
            val timeString = TimeUtils.getFriendlyTimeSpanByNow(time ?: "", SDF)
            return "$countString   |   $timeString"

        }


    }


}