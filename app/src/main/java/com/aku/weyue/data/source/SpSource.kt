package com.aku.weyue.data.source

import com.aku.common.perfrence.SpAnyDelegates
import com.aku.common.perfrence.SpDelegates
import com.aku.weyue.data.UserBean

/**
 * @author Zsc
 * @date   2019/5/2
 * @desc
 */
object SpSource {

    private const val SP_USER = "SP_USER"
    var user by SpAnyDelegates(SP_USER, UserBean::class.java)

    private const val SP_TOKEN = "SP_TOKEN"
    var token by SpDelegates(SP_TOKEN, "")

    private const val SP_THEME="SP_THEME"
    var appTheme by SpDelegates(SP_THEME, 0)

}