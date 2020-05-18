package com.aku.aac.kchttp.config

/**
 * @author Zsc
 * @date   2019/5/2
 * @desc Fixme 更多列举
 */
object ApiErrorCode {
    //没网
    const val NO_NET_WORK = 0
    //网络请求出现错误
    const val ERROR = 1
    //网络请求取消
    const val CANCEL = -1
    //网络请求 未知错误
    const val UNKNOWN = 1000
    //网络请求超时
    const val TIME_OUT = 1001
    //解析出错
    const val PARSE_ERROR = 1002
    //连接异常
    const val CONNECT_ERROR = 1003

}