package com.aku.weyue.util

/**
 * @author Zsc
 * @date   2019/5/3
 * @desc
 */
/**
 * 只保留一位小数的Double
 */
fun Double?.toRatString():String?{
    return this?.let {
        String.format("%.1f",it)
    }
}