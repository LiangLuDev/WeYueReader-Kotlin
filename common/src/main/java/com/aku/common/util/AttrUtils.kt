package com.aku.common.util

import android.util.TypedValue
import com.blankj.utilcode.util.ActivityUtils

/**
 * @author Zsc
 * @date   2019/6/1
 * @desc
 */
object AttrUtils {

    /**
     * 获取attr的id
     */
    fun getAttrResourceId(attr: Int): Int {
        return getAttrTypeValue(attr).resourceId
    }

    private fun getAttrTypeValue(attr: Int): TypedValue {
        return TypedValue().apply {
            ActivityUtils.getTopActivity()
                .theme.resolveAttribute(attr, this, true)
        }
    }


}