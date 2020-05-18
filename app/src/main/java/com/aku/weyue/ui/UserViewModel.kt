package com.aku.weyue.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aku.weyue.data.UserBean
import com.aku.weyue.data.source.SpSource

/**
 * @author Zsc
 * @date   2019/5/2
 * @desc
 */
class UserViewModel : ViewModel() {

    val user = MutableLiveData<UserBean>(SpSource.user)

}