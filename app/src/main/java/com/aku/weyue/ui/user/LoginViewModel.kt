package com.aku.weyue.ui.user

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aku.aac.kchttp.ext.awaitResult
import com.aku.aac.kchttp.ext.doError
import com.aku.aac.kchttp.ext.doSuccess
import com.aku.weyue.api.BookApi
import com.aku.weyue.data.UserBean
import com.aku.weyue.data.source.SpSource
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * @author Zsc
 * @date   2019/5/2
 * @desc
 */
class LoginViewModel
    : ViewModel(),KoinComponent {

    private val bookApi:BookApi by inject()

    val name: ObservableField<String> = ObservableField()
    val password: ObservableField<String> = ObservableField()

    val userLogin: MutableLiveData<UserBean> = MutableLiveData()

    fun loginIn() {
        when {
            name.get().isNullOrEmpty() -> ToastUtils.showShort("请输入用户名")
            password.get().isNullOrEmpty() -> ToastUtils.showShort("请输入密码")
            else -> {
                viewModelScope.launch {
                    val result = bookApi.loginAsync(
                        name.get()!!,
                        password.get()!!
                    ).awaitResult()
                    result.doSuccess {
                        SpSource.token = it.token
                        SpSource.user = it
                        userLogin.postValue(it)
                    }.doError {
                        ToastUtils.showShort(it.msg)
                    }
                }
            }

        }
    }

}