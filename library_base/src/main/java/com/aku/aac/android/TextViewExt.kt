package com.aku.aac.android

import android.widget.TextView

/**
 * @author Zsc
 * @date   2019/6/1
 * @desc
 */
val TextView.string: String
    get() = text.toString()

val TextView.isEmpty: Boolean
    get() = string.isEmpty()
