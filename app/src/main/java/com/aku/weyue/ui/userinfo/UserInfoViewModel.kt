package com.aku.weyue.ui.userinfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aku.aac.kchttp.data.BaseResult
import com.aku.aac.kchttp.data.ResultApi
import com.aku.aac.kchttp.ext.awaitResult
import com.aku.aac.kchttp.ext.doError
import com.aku.aac.kchttp.ext.doSuccess
import com.aku.weyue.api.BookApi
import com.aku.weyue.data.UserDetail
import com.aku.weyue.data.source.SpSource
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

/**
 * @author Zsc
 * @date   2019/6/8
 * @desc
 */
class UserInfoViewModel : ViewModel(), KoinComponent {

    private val bookApi: BookApi by inject()

    val userEdit = MutableLiveData<UserDetail>(UserDetail(SpSource.user))

    val isEdit: MutableLiveData<Boolean> = MutableLiveData(false)

    val userIcon: MutableLiveData<BaseResult<String>> = MutableLiveData()

    val userPassword: MutableLiveData<BaseResult<String>> = MutableLiveData()

    fun updateUserInfo(nickname: String, brief: String): Job {
        return viewModelScope.launch(Dispatchers.Default) {
            bookApi.updateUserInfoAsync(nickname, brief)
                .awaitResult()
                .doSuccess {
                    userEdit.postValue(userEdit.value)
                }.doError {
                    ToastUtils.showShort(it.msg)
                }
        }
    }

    fun loadUserDetail(): Job {
        return viewModelScope.launch(Dispatchers.Default) {
            bookApi.getUserInfoAsync()
                .awaitResult()
                .doSuccess {
                    userEdit.postValue(it)
                }.doError {
                    ToastUtils.showShort("获取用户详情失败")
                }
        }
    }

    fun updatePassword(password: String): Job {
        return viewModelScope.launch(Dispatchers.Default) {
            bookApi.updatePasswordAsync(password)
                .awaitResult()
                .apply {
                    userPassword.postValue(this)
                }
        }
    }

    fun uploadAvatar(imagePath: String): Job {
        val file = File(imagePath)
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("avatar", file.name, requestBody)
        return viewModelScope.launch(Dispatchers.Default) {
            bookApi.avatarAsync(body)
                .awaitResult()
                .let {
                    userIcon.postValue(it)
                }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }
}