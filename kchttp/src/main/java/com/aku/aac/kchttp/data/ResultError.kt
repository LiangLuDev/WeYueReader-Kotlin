package com.aku.aac.kchttp.data

/**
 * @author Zsc
 * @date   2019/5/19
 * @desc 返回失败的请求结果
 */
interface ResultError {
    var code: Int
    var msg: String
}