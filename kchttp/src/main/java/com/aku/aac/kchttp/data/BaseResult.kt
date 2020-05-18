package com.aku.aac.kchttp.data

/**
 * @author Zsc
 * @date   2019/5/19
 * @desc 基础返回结果
 */
interface BaseResult<T> : ResultError {
    var state: Int
    var data: T?
}