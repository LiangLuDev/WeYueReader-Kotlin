package com.aku.weyue.ui.read;

import androidx.annotation.Nullable;
import com.aku.weyue.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Liang_Lu on 2017/11/22.
 * 选择阅读背景
 */

public class ReadBgAdapter extends BaseQuickAdapter<ReadBgBean, BaseViewHolder> {

    public ReadBgAdapter(@Nullable List<ReadBgBean> data) {
        super(R.layout.item_read_bg, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ReadBgBean item) {
        helper.setBackgroundRes(R.id.read_bg_view, item.getBgColor());
        helper.setVisible(R.id.read_bg_iv_checked, item.isSelect());
    }
}
