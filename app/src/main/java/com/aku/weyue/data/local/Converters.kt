package com.aku.weyue.data.local

import androidx.room.TypeConverter
import com.aku.weyue.data.BookBean
import com.aku.common.util.GsonUtils

/**
 * @author Zsc
 * @date   2019/5/18
 * @desc
 */
class Converters {

    @TypeConverter
    fun list2String(list: List<String>?)
            : String = GsonUtils.toJson(list)

    @TypeConverter
    fun string2List(value: String?): List<String> {
        return GsonUtils.toList(value ?: "")
    }

    @TypeConverter
    fun string2RatingBean(value: String?): BookBean.RatingBean? {
        return GsonUtils.toObject(value ?: "")
    }

    @TypeConverter
    fun ratingBeanString(value: BookBean.RatingBean?): String? {
        return GsonUtils.toJson(value)
    }
}