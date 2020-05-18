package com.aku.weyue.ui.shelf

import android.view.View
import android.widget.ImageView
import com.aku.weyue.R
import com.aku.weyue.api.BookApi
import com.aku.weyue.data.BookBean
import com.blankj.utilcode.util.FileUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @author Zsc
 * @date   2019/5/18
 * @desc
 */
class BookShelfAdapter : BaseQuickAdapter<BookBean, BaseViewHolder>(
    R.layout.item_book_shelf
) {
    override fun convert(helper: BaseViewHolder, item: BookBean) {
        Glide.with(mContext)
            .load(BookApi.BOOK_IMG_URL + item.cover)
            .apply(RequestOptions().placeholder(R.mipmap.ic_book_loading))
            .into(helper.getView<View>(R.id.coll_book_iv_cover) as ImageView)
        if (item.isLocal) {
            helper.setText(R.id.coll_book_tv_name, FileUtils.getFileNameNoExtension(item._id))
                .setText(R.id.coll_book_tv_chapter, "")
        } else {
            helper.setText(R.id.coll_book_tv_name, item.title)
                .setText(R.id.coll_book_tv_chapter, item.lastChapter)
        }
        helper.setVisible(R.id.coll_book_iv_red_rot, true)
    }
}