package com.page.view.utils

import java.io.File

/**
 * @author Zsc
 * @date   2019/5/3
 * @desc
 */
object Constant {

    const val FORMAT_TIME = "HH:mm"

    //采用自己的格式去设置文件，防止文件被系统文件查询到
    const val SUFFIX_WY = ".wy"

    const val FORMAT_BOOK_DATE = "yyyy-MM-dd'T'HH:mm:ss"

    //BookCachePath (因为getCachePath引用了Context，所以必须是静态变量，不能够是静态常量)
    @JvmStatic
    var BOOK_CACHE_PATH = (FileUtils.getCachePath() + File.separator
            + "book_cache" + File.separator)

}