package com.aku.weyue.ui.type

import androidx.databinding.DataBindingUtil
import com.aku.weyue.R
import com.aku.weyue.data.BookType
import com.aku.weyue.databinding.ItemMainBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @author Zsc
 * @date   2019/4/29
 * @desc
 */
class BookTypeAdapter : BaseQuickAdapter<BookType, BaseViewHolder>(
    R.layout.item_main
) {

    override fun convert(helper: BaseViewHolder, item: BookType) {
        val binding = DataBindingUtil.bind<ItemMainBinding>(helper.itemView)
        binding?.t = BookTypeViewModel(item)

    }
}