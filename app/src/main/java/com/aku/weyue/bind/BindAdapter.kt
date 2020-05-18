package com.aku.weyue.bind

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.aku.common.widget.StateLayout
import com.aku.weyue.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 * @author Zsc
 * @date   2019/5/2
 * @desc
 */

@BindingAdapter("isVisible")
fun bindIsVisible(view: View, isVisible: Boolean?) {
    view.visibility = if (isVisible == true) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

/**
 * 绑定图片和url
 */
@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("imageCircleFromUrl")
fun bindImageCircleFromUrl(view: ImageView, imageUrl: String?) {
    Glide.with(view.context)
        .run {
            if (imageUrl.isNullOrEmpty()) {
                load(R.mipmap.avatar)
            } else {
                load("http://www.luliangdev.cn$imageUrl")
            }
        }
        .transition(DrawableTransitionOptions.withCrossFade())
        .transform(CircleCrop())
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .into(view)
}


/**
 * 绑定[StateLayout]的状态
 */
@BindingAdapter("loadingStatus")
fun bindLoadStatus(view: StateLayout, status: Int) {
    view.status = status
}