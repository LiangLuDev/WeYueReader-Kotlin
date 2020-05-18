package com.dct.aac.toolbarsample

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

/**
 * @author Zsc
 * @date   2019/5/29
 * @desc
 */
class ToolbarApplication:Application() {


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}