package com.aku.aac.kchttp

import android.annotation.SuppressLint
import com.aku.aac.kchttp.config.ApiHandler
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * kotlin Coroutine for http
 */
@SuppressLint("StaticFieldLeak")
object KcHttp {

    var apiHandler = object : ApiHandler {}

    var globalBaseUrl: String = "http://www.aku.com/"
    /**
     * 全局的retrofitBuilder
     */
    val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
    }
    /**
     * 全局的 okHttpClientBuilder
     */
    val okHttpClientBuilder: OkHttpClient.Builder by lazy {
        OkHttpClient.Builder()
    }

    val retrofits: MutableMap<String, Retrofit> = mutableMapOf()

    init {
        retrofitBuilder
            //使用Gson
            .addConverterFactory(GsonConverterFactory.create())
            //支持协程
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
    }

    /**
     * 直接创建Api
     */
    inline fun <reified T> createApi(
        url: String = globalBaseUrl
    ): T {
        return when (val retrofit = retrofits[T::class.java.simpleName]) {
            null -> create(url)
                .apply {
                    retrofits[T::class.java.simpleName] = this
                }
            else -> retrofit
        }.create(T::class.java)

    }

    /**
     *
     */
    inline fun <reified T> createSingleApi(
        url: String = globalBaseUrl,
        retrofit: Retrofit = create(url)
    ): T {
        return retrofit
            .create(T::class.java)
    }

    fun create(url: String = globalBaseUrl): Retrofit {
        return retrofitBuilder
            .baseUrl(url)
            .client(
                okHttpClientBuilder.build()
            )
            .build()
    }


}