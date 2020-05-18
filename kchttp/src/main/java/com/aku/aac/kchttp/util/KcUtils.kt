package com.aku.aac.kchttp.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * @author Zsc
 * @date   2019/6/9
 * @desc
 */
object KcUtils {

    private lateinit var sApplication: Application

    fun init(context: Context) {
        init(context.applicationContext as Application)
    }

    fun init(application: Application) {
        sApplication = application
    }


    fun isConnected(): Boolean {
        return when (val net = getActiveNetworkInfo()) {
            null -> false
            else -> net.isConnected
        }

    }

    @SuppressLint("MissingPermission")
    private fun getActiveNetworkInfo(): NetworkInfo? {
        return (sApplication.getSystemService(Context.CONNECTIVITY_SERVICE)
                as? ConnectivityManager?)?.activeNetworkInfo
    }

    /**
     * 使用资源文件
     */
    fun getString(id: Int): String {
        return sApplication.getString(id)
    }

}