package com.aku.weyue.util

import com.blankj.utilcode.util.ActivityUtils
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * @author Zsc
 * @date   2019/6/1
 * @desc
 */

val rxPermission: RxPermissions
    get() = RxPermissions(ActivityUtils.getTopActivity())