package com.page.view

import com.page.view.data.BaseRecord

/**
 * @author Zsc
 * @date   2019/5/25
 * @desc 数据配置类 fixme 还有保存（销毁）阅读记录
 */
interface DataConfig {
    /**
     * 获取阅读记录
     */
    fun getRecordById(bookId: String): BaseRecord

    fun saveRecord(bookRecord: BaseRecord)

}