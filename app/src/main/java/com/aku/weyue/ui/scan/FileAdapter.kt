package com.aku.weyue.ui.scan

import androidx.databinding.DataBindingUtil
import com.aku.weyue.R
import com.aku.weyue.data.BookFile
import com.aku.weyue.databinding.ItemBookDirBinding
import com.aku.weyue.databinding.ItemBookFileBinding
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @author Zsc
 * @date   2019/6/2
 * @desc
 */
class FileAdapter : BaseMultiItemQuickAdapter<BookFile, BaseViewHolder>(
    null
) {

    val allShelfBookIds = mutableListOf<String>()

    init {
        addItemType(BookFile.FILE, R.layout.item_book_file)
        addItemType(BookFile.DIR, R.layout.item_book_dir)
    }


    override fun convert(helper: BaseViewHolder, item: BookFile) {
        if (item.itemType == BookFile.FILE) {
            helper.setVisible(
                R.id.file_cb_select,
                item.isTxt &&
                        !allShelfBookIds.contains(item.path)
            )
            DataBindingUtil.bind<ItemBookFileBinding>(helper.itemView)
                ?.run {
                    f = item
                    executePendingBindings()
                }
        } else {
            DataBindingUtil.bind<ItemBookDirBinding>(helper.itemView)
                ?.run {
                    f = item
                    executePendingBindings()
                }
        }
    }
}