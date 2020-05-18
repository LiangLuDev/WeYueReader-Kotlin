package com.page.view.utils

import android.content.Context

/**
 * @author Zsc
 * @date   2019/5/3
 * @desc
 */
object BookUtils {

    @JvmStatic
    lateinit var mAppContext: Context


    fun init(appContext: Context) {
        mAppContext = appContext
    }

}