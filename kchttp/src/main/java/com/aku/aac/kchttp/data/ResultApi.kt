package com.aku.aac.kchttp.data

import com.aku.aac.kchttp.config.RState

/**
 * @author Zsc
 * @date   2019/4/29
 * @desc 数据请求结果，state为自添加字段
 */
open class ResultApi<T> : BaseResult<T> {

    override var code: Int = 0
    override var msg: String = ""
    override var data: T? = null
    /**
     * 请求返回的状态[RState]
     */
    override var state: Int = RState.SUCCESS
}