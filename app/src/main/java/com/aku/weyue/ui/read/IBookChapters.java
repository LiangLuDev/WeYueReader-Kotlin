package com.aku.weyue.ui.read;


import com.aku.weyue.data.BookChaptersBean;

/**
 * Created by Liang_Lu on 2017/12/11.
 */

public interface IBookChapters {
    void bookChapters(BookChaptersBean bookChaptersBean);

    void finishChapters();

    void errorChapters();

}
