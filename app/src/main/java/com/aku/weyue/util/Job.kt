package com.aku.weyue.util

import android.app.Dialog
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.Job

/**
 * @author Zsc
 * @date   2019/6/4
 * @desc
 */
fun Job.bindDialog(dialog: Dialog = sProgress) {
    val t0 = System.currentTimeMillis()
    dialog.setOnCancelListener {
        cancel()
    }
    invokeOnCompletion {
        LogUtils.d("显示时间：${System.currentTimeMillis() - t0}")
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }


}