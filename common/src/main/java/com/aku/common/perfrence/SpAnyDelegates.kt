package com.aku.common.perfrence

import com.google.gson.Gson
import kotlin.reflect.KProperty

class SpAnyDelegates<T>(
    private val key: String,
    private val clazz: Class<T>
) {

    companion object {
        private val gson by lazy { Gson() }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return gson.fromJson(SpDelegates.SP.getString(key), clazz)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        SpDelegates.SP.put(key, gson.toJson(value))
    }
}
