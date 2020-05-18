package com.aku.weyue.data

import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.File

/**
 * @author Zsc
 * @date   2019/6/2
 * @desc
 */
data class BookFile(
    val file: File,
    var isSelect: Boolean = false
) : MultiItemEntity {

    val sizeString: String
        get() = FileUtils.getFileSize(file)

    val time: String
        get() = TimeUtils.millis2String(file.lastModified())

    val name: String
        get() = file.name

    val path: String
        get() = file.path

    val countString: String
        get() = "文件夹:${countDir}个   txt:${countTxt}个"

    val isTxt:Boolean
        get() = file.extension.equals("txt", true)

    private var countTxt: Int = 1

    private var countDir: Int = 0

    /**
     * 文件夹遍历所有文件，得到子文件夹和txt个数
     */
    fun checkCount(): BookFile {
        if (file.isDirectory) {
            FileUtils.listFilesInDir(file)
                .run {
                    countTxt = filter {
                        it.extension.equals("txt", true)
                    }.size
                    countDir = filter {
                        it.isDirectory
                    }.size
                }
        }
        return this
    }

    override fun getItemType(): Int {
        return if (file.isDirectory) DIR else FILE
    }


    companion object {
        const val FILE = 0
        const val DIR = 1
    }


}