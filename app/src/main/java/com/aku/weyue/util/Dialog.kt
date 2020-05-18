package com.aku.weyue.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.aku.weyue.R
import com.blankj.utilcode.util.ActivityUtils


/**
 * @author Zsc
 * @date   2019/6/4
 * @desc
 */
val sProgress: AlertDialog
    get() = showProgress()

/**
 * 返回可取消，点击外部不取消的dialog
 */
private fun showProgress(
    context: Context =
        ActivityUtils.getTopActivity()
): AlertDialog {

    return AlertDialog.Builder(context, R.style.DialogLoading)
        .setView(R.layout.dialog_loading)
        .show()
        .apply {
            setCanceledOnTouchOutside(false)
        }
}