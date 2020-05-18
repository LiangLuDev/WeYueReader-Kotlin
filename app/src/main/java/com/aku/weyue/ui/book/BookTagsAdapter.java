package com.aku.weyue.ui.book;

import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.aku.weyue.R;
import com.aku.weyue.api.BookApi;
import com.aku.weyue.data.BookBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.List;

/**
 * Created by Liang_Lu on 2017/12/20.
 */

public class BookTagsAdapter extends BaseQuickAdapter<BookBean, BaseViewHolder> {


    public BookTagsAdapter() {
        super(R.layout.item_book_tag, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookBean item) {
        Glide.with(mContext).load(BookApi.BOOK_IMG_URL + item.getCover())
                .apply(new RequestOptions().placeholder(R.mipmap.ic_book_loading))
                .into((ImageView) helper.getView(R.id.iv_image));
        helper.setText(R.id.tv_book_title, item.getTitle())
                .setText(R.id.tv_book_brief, item.getLongIntro());

        String tags = "";
        for (String tag : item.getTags()) {
            tags += tag + " | ";
        }
        if (item.getTags().size() > 0) {
            tags=tags.substring(0, tags.length()- 2);
        }

        helper.setText(R.id.tv_book_tags, tags);

    }
}
