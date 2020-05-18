package com.aku.weyue.ui.author

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.aku.aac.core.BaseFragment
import com.aku.weyue.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.about_author_frag.*
import kotlinx.android.synthetic.main.include_toolbar.*

/**
 * @author Zsc
 * @date   2019/6/1
 * @desc
 */
class AboutAuthorFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.about_author_frag

    override fun initData(savedInstanceState: Bundle?) {
        toolbar.setupWithNavController(findNavController())
        Glide.with(this)
            .load(R.mipmap.avatar)
            .apply(
                RequestOptions()
                    .transform(CircleCrop())
            )
            .into(iv_avatar)
    }
}