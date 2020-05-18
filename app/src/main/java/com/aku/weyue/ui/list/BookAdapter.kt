package com.aku.weyue.ui.list

import androidx.databinding.DataBindingUtil
import com.aku.weyue.R
import com.aku.weyue.data.BookBean
import com.aku.weyue.databinding.ItemBookBinding
import com.aku.weyue.databinding.ItemMainBinding
import com.aku.weyue.ui.type.BookTypeViewModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @author Zsc
 * @date   2019/5/3
 * @desc
 */
class BookAdapter:BaseQuickAdapter<BookBean,BaseViewHolder>(
    R.layout.item_book
) {
    override fun convert(helper: BaseViewHolder, item: BookBean) {
        val binding = DataBindingUtil.bind<ItemBookBinding>(helper.itemView)
        binding?.t = BookInfoViewModel(item)
        binding?.executePendingBindings()
    }
}