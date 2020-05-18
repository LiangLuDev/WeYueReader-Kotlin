package com.aku.aac.kchttp.util

import androidx.core.content.FileProvider

/**
 * @author Zsc
 * @date   2019/6/9
 * @desc
 */
internal class KcFileProvider : FileProvider(){
    override fun onCreate(): Boolean {
        KcUtils.init(context)
        return true
    }
}