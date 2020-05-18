package com.aku.weyue.test

import android.os.Environment
import com.page.view.data.CollBookBean

/**
 * @author Zsc
 * @date   2019/5/4
 * @desc
 */
object LocalBook {

    val bookDig = CollBookBean().apply {
        isLocal = true
        bookId = Environment.getExternalStorageDirectory().path!! + "/test.txt"

    }

}