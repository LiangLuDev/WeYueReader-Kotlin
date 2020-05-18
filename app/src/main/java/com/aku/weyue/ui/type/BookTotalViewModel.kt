package com.aku.weyue.ui.type

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aku.aac.kchttp.ext.doError
import com.aku.aac.kchttp.ext.doSuccess
import com.aku.common.widget.StateLayout
import com.aku.weyue.data.BookTotal
import com.aku.weyue.data.repository.BookTypeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * @author Zsc
 * @date   2019/4/29
 * @desc
 */
class BookTotalViewModel : ViewModel() {

    val status: MutableLiveData<Int> = MutableLiveData()

    val bookTotalLiveData: MutableLiveData<BookTotal> = MutableLiveData()

    fun load() {
        //666 避免切换主题重新加载数据
        if (bookTotalLiveData.value != null) {
            return
        }
        viewModelScope.launch {
            //status当前虽然是同步的，但也要放在viewModelScope.launch内，不然退出后进入还是loading状态
            status.postValue(StateLayout.Loading)
            BookTypeRepository
                .loadBookTotal()
                .doSuccess {
                    status.postValue(StateLayout.Success)
                    bookTotalLiveData.postValue(it)
                }.doError {
                    when (it.code) {
                        1 -> status.postValue(StateLayout.No_Network)
                        else -> status.postValue(StateLayout.Error)
                    }
                }
        }
    }


    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}