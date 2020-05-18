package com.aku.aac.kchttp.config

import com.aku.aac.kchttp.*
import com.aku.aac.kchttp.data.BaseResult
import com.aku.aac.kchttp.util.KcUtils
import com.aku.aac.kchttp.ext.createErrorResult
import com.aku.aac.kchttp.ext.msgOrDefault
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.text.ParseException

/**
 * @author Zsc
 * @date   2019/5/19
 * @desc
 */
interface ApiHandler {

    /**
     * 检测返回的数据
     */
    fun <T> handleResult(resultApi: BaseResult<T>): BaseResult<T> {
        return resultApi.apply {
            if ((resultApi.code != 200 &&
                        resultApi.code != 10000
                        ) || resultApi.data == null
            ) {
                resultApi.state = RState.ERROR
                resultApi.msg = resultApi.msgOrDefault
            }
        }
    }

    /**
     * 异常拦截处理
     * @param e
     * @return
     */
    fun <T> handleError(e: Throwable): BaseResult<T> {
        return when (e) {
            is HttpException -> {
                when(val code=e.code()) {
                    504 -> createErrorResult(
                        code,
                        KcUtils.getString(R.string.kchttp_net_504)
                    )
                    in 400 until 500-> createErrorResult(
                        code,
                        KcUtils.getString(R.string.kchttp_net_4xx)
                    )
                    in 500 until 600 -> createErrorResult(
                        code,
                        KcUtils.getString(R.string.kchttp_net_5xx)
                    )
                    else -> createErrorResult(
                        ApiErrorCode.ERROR,
                        KcUtils.getString(R.string.kchttp_net_unknown_error)
                    )
                }
            }
            is CancellationException -> createErrorResult(
                ApiErrorCode.CANCEL,
                KcUtils.getString(R.string.kchttp_net_cancel),
                RState.CANCEL
            )
            is SocketTimeoutException -> createErrorResult(
                ApiErrorCode.TIME_OUT,
                KcUtils.getString(R.string.kchttp_net_504)
            )
            is JsonParseException,
            is JSONException,
            is ParseException -> createErrorResult(
                ApiErrorCode.PARSE_ERROR,
                KcUtils.getString(R.string.kchttp_net_504)
            )
            is ConnectException -> createErrorResult(
                ApiErrorCode.CONNECT_ERROR,
                KcUtils.getString(R.string.kchttp_net_504)
            )
            else -> createErrorResult(
                ApiErrorCode.UNKNOWN,
                KcUtils.getString(R.string.kchttp_net_504)
            )
        }
    }
}