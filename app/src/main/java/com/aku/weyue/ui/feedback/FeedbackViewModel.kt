package com.aku.weyue.ui.feedback

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aku.aac.kchttp.data.BaseResult
import com.aku.aac.kchttp.data.ResultApi
import com.aku.aac.kchttp.ext.awaitWithNetCheck
import com.aku.weyue.api.BookApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * @author Zsc
 * @date   2019/6/1
 * @desc
 */
class FeedbackViewModel : ViewModel(), KoinComponent {

    private val bookApi: BookApi by inject()

    val userQQ = MutableLiveData("")

    val userContent = MutableLiveData("")

    val resultApi = MutableLiveData<BaseResult<String>>()


    fun feedBack() {
        viewModelScope.launch {
            bookApi.userFeedBackAsync(
                userQQ.value ?: "",
                userContent.value ?: ""
            ).awaitWithNetCheck()
                .let {
                    resultApi.postValue(it)
                }

        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}