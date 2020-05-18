package com.aku.common.util

import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

/**
 * Json解析基类，所有的Json解析
 *
 * @author Zsc
 * @date 2017/9/15
 */
object GsonUtils {

    private const val TAG = "GsonUtils"
    private val GSON by lazy { Gson() }

    inline fun <reified T> toList(json: String): MutableList<T> {
        return toList(json, Array<T>::class.java)
    }

    inline fun <reified T> toObject(jsonString: String): T? {
        return toObject(jsonString, T::class.java)
    }

    /**
     * Json解析对象，解析出错返回空数据
     *
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
    </T> */
    fun <T> toObject(jsonString: String, clazz: Class<T>): T? {
        try {
            GsonUtils
            return GSON.fromJson(jsonString, clazz)
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, Log.getStackTraceString(e))
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        return null
    }


    /**
     * 对象转Json
     * @param obj
     * @return
     */
    fun toJson(obj: Any?): String {
        return GSON.toJson(obj)
    }


    /**
     * Json解析集合，出错则返回空集合
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
    </T> */
    fun <T> toList(json: String, clazz: Class<Array<T>>): MutableList<T> {
        try {
            val array = GSON.fromJson(json, clazz)
            return array.toMutableList()
        } catch (e: JsonSyntaxException) {
            LogUtils.eTag(TAG, Log.getStackTraceString(e))
        } catch (e: Exception) {
            LogUtils.eTag(TAG, Log.getStackTraceString(e))
        }
        return mutableListOf()
    }

}
