package com.aku.weyue.api

import com.aku.weyue.data.source.SpSource
import okhttp3.Interceptor
import okhttp3.Request

/**
 * @author Zsc
 * @date   2019/5/19
 * @desc
 */
object HttpConfig {

    /**
     * 创建： 馥溪凝
     * 日期： 2018/9/11 17:35
     * 描述： 设置token
     */
    fun tokenInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = configRequestBuilder(chain)
            return@Interceptor chain.proceed(request.build())
        }
    }

    /**
     * 配置请求信息
     */
    fun configRequestBuilder(chain: Interceptor.Chain): Request.Builder {
        return chain.request()
            .newBuilder()
            .addHeader("app-type","Android")
            .addHeader("access-token", SpSource.token)

    }


}