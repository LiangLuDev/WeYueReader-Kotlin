package com.aku.weyue.ui.read

import com.blankj.utilcode.util.FileUtils
import com.page.view.utils.Constant
import com.page.view.utils.IOUtils
import java.io.*

/**
 * @author Zsc
 * @date   2019/5/4
 * @desc
 */
object BookCacheUtils {

    fun getBookFile(folderName: String, fileName: String): File {
        return FileUtils.getFileByPath(
            Constant.BOOK_CACHE_PATH + folderName
                    + File.separator + fileName + Constant.SUFFIX_WY
        )
    }

    /**
     * 存储章节
     *
     * @param folderName
     * @param fileName
     * @param content
     */
    fun saveChapterInfo(folderName: String, fileName: String, content: String) {
        val file = getBookFile(folderName, fileName)
        FileUtils.createOrExistsDir(file.parent)
        //获取流并存储
        var writer: Writer? = null
        try {
            writer = BufferedWriter(FileWriter(file))
            writer.write(content)
            writer.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            IOUtils.close(writer)
        }

    }
}