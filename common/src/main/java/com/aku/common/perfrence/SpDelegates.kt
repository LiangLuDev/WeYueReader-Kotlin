package com.aku.common.perfrence

import android.util.Log
import com.blankj.utilcode.util.SPUtils
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
class SpDelegates<T>(private val key: String, private val default: T) {

    companion object {
        private const val TAG = "SpDelegates"

        internal val SP by lazy { SPUtils.getInstance() }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return when (default) {
            is Int -> SP.getInt(key, default)
            is Boolean -> SP.getBoolean(key, default)
            is String -> SP.getString(key, default)
            is Float -> SP.getFloat(key, default)
            is Long -> SP.getLong(key, default)
            else -> default
        } as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        when (value) {
            is Int -> SP.put(key, value)
            is Boolean -> SP.put(key, value)
            is String -> SP.put(key, value)
            is Float -> SP.put(key, value)
            is Long -> SP.put(key, value)
            else -> Log.e(TAG, "请配置相关类型")
        }
    }

}


