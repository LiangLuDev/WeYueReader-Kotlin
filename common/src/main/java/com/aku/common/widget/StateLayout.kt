package com.aku.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.aku.common.R

/**
 * 带状态的view
 * 对LoadingLayout做了优化，共用了空页面、错误页面和无网的页面
 * @author Zsc
 */
class StateLayout : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.StateLayout)
        isFirstVisible = a.getBoolean(R.styleable.StateLayout_isFirstVisible, false)
        a.recycle()
    }

    private lateinit var loadProgressBar: ProgressBar

    lateinit var globalLoadingPage: View
        private set
    private lateinit var errorImg: ImageView
    private lateinit var errorText: TextView
    private lateinit var errorReloadBtn: TextView
    private lateinit var loadingLinear: View

    private lateinit var contentView: View

    private var listener: (() -> Unit)? = null
    /**
     * 是否一开始显示contentView，默认不显示
     */
    private var isFirstVisible: Boolean = false
    /**
     * 返回当前状态{Success, Empty, Error, No_Network, Loading}
     *
     * @return
     */
    var status: Int = 0
        set(@Flavour status) {
            field = status
            when (status) {
                Success -> {
                    globalLoadingPage.visibility = View.GONE
                    contentView.visibility = View.VISIBLE
                }

                Loading -> {
                    globalLoadingPage.visibility = View.VISIBLE
                    loadingLinear.visibility = View.GONE
                    loadProgressBar.visibility = View.VISIBLE
                    contentView.visibility = View.GONE
                }

                Empty -> {
                    errorText.text = emptyStr
                    errorImg.setImageResource(emptyImgId)
                    globalLoadingPage.visibility = View.VISIBLE
                    loadingLinear.visibility = View.VISIBLE
                    loadProgressBar.visibility = View.GONE
                    contentView.visibility = View.GONE
                }

                Error -> {
                    errorText.text = errorStr
                    errorImg.setImageResource(errorImgId)
                    globalLoadingPage.visibility = View.VISIBLE
                    loadingLinear.visibility = View.VISIBLE
                    loadProgressBar.visibility = View.GONE
                    contentView.visibility = View.GONE
                }

                No_Network -> {
                    errorText.text = networkStr
                    errorImg.setImageResource(networkImgId)
                    loadingLinear.visibility = View.VISIBLE
                    globalLoadingPage.visibility = View.VISIBLE
                    loadProgressBar.visibility = View.GONE
                    contentView.visibility = View.GONE
                }

                else -> {
                }
            }

        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 1) {
            throw IllegalStateException("StateLayout can host only one direct child")
        }
        contentView = this.getChildAt(0)
        if (!isFirstVisible) {
            contentView.visibility = View.GONE
        }
        build()
    }

    private fun build() {
        globalLoadingPage = LayoutInflater.from(context).inflate(loadingLayoutId, null)
        globalLoadingPage.setBackgroundColor(getColor(context, backgroundColor))
        errorText = globalLoadingPage.findViewById(R.id.error_text)
        errorImg = globalLoadingPage.findViewById(R.id.error_img)
        loadingLinear = globalLoadingPage.findViewById(R.id.loading_ll_msg)
        errorReloadBtn = globalLoadingPage.findViewById(R.id.error_reload_btn)
        loadProgressBar = globalLoadingPage.findViewById(R.id.loading_progress)
        errorReloadBtn.setOnClickListener {
            listener?.invoke()
        }
        errorText.textSize = tipTextSize.toFloat()
        errorText.setTextColor(getColor(context, tipTextColor))
        errorReloadBtn.setBackgroundResource(reloadBtnId)
        errorReloadBtn.text = reloadBtnStr
        errorReloadBtn.textSize = buttonTextSize.toFloat()
        errorReloadBtn.setTextColor(getColor(context, buttonTextColor))

        if (buttonHeight != -1) {
            errorReloadBtn.height = dp2px(context, buttonHeight)
        }
        if (buttonWidth != -1) {
            errorReloadBtn.width = dp2px(context, buttonWidth)
        }

        this.addView(globalLoadingPage)
    }

    /**
     * 设置Error状态提示文本，仅对当前所在的地方有效
     *
     * @param text
     * @return
     */
    fun setErrorText(text: String): StateLayout {
        errorText.text = text
        return this
    }


    /**
     * 设置Error状态显示图片，仅对当前所在的地方有效
     *
     * @param id
     * @return
     */
    fun setErrorImage(@DrawableRes id: Int): StateLayout {
        errorImg.setImageResource(id)
        return this
    }


    /**
     * 设置Error状态提示文本的字体大小，仅对当前所在的地方有效
     *
     * @param sp
     * @return
     */
    fun setErrorTextSize(sp: Int): StateLayout {
        errorText.textSize = sp.toFloat()
        return this
    }


    /**
     * 设置Error状态图片的显示与否，仅对当前所在的地方有效
     *
     * @param bool
     * @return
     */
    fun setErrorImageVisible(bool: Boolean): StateLayout {
        errorImg.visibility = if (bool) {
            View.VISIBLE
        } else {
            View.GONE
        }
        return this
    }

    /**
     * 设置ReloadButton的文本，仅对当前所在的地方有效
     *
     * @param text
     * @return
     */
    fun setReloadButtonText(text: String): StateLayout {
        errorReloadBtn.text = text
        return this
    }

    /**
     * 设置ReloadButton的文本字体大小，仅对当前所在的地方有效
     *
     * @param sp
     * @return
     */
    fun setReloadButtonTextSize(sp: Int): StateLayout {
        errorReloadBtn.textSize = sp.toFloat()
        return this
    }

    /**
     * 设置ReloadButton的文本颜色，仅对当前所在的地方有效
     *
     * @param id
     * @return
     */
    fun setReloadButtonTextColor(@ColorRes id: Int): StateLayout {
        errorReloadBtn.setTextColor(getColor(context, id))
        return this
    }

    /**
     * 设置ReloadButton的背景，仅对当前所在的地方有效
     *
     * @param id
     * @return
     */
    fun setReloadButtonBackgroundResource(@DrawableRes id: Int): StateLayout {
        errorReloadBtn.setBackgroundResource(id)
        return this
    }

    /**
     * 设置ReloadButton的监听器
     *
     * @param listener
     * @return
     */
    fun setOnReloadListener(listener: () -> Unit): StateLayout {
        this.listener = listener
        return this
    }

    /**
     * 自定义加载页面，仅对当前所在的Activity有效
     *
     * @param view
     * @return
     */
    fun setLoadingPage(view: View): StateLayout {
        this.removeView(globalLoadingPage)
        this.addView(view)
        return this
    }

    /**
     * 自定义加载页面，仅对当前所在的地方有效
     *
     * @param id
     * @return
     */
    fun setLoadingPage(@LayoutRes id: Int): StateLayout {

        this.removeView(globalLoadingPage)
        val view = LayoutInflater.from(context).inflate(id, null)
        this.addView(view)
        return this
    }

    @IntDef(Success, Empty, Error, No_Network, Loading)
    annotation class Flavour

    /**
     * 全局配置的Class，对所有使用到的地方有效
     */
    class Config {

        fun setErrorText(text: String): Config {
            errorStr = text
            return config
        }

        fun setEmptyText(text: String): Config {
            emptyStr = text
            return config
        }

        fun setNoNetworkText(text: String): Config {
            networkStr = text
            return config
        }

        fun setReloadButtonText(text: String): Config {
            reloadBtnStr = text
            return config
        }

        /**
         * 设置所有提示文本的字体大小
         *
         * @param sp
         * @return
         */
        fun setAllTipTextSize(sp: Int): Config {
            tipTextSize = sp
            return config
        }

        /**
         * 设置所有提示文本的字体颜色
         *
         * @param color
         * @return
         */
        fun setAllTipTextColor(@ColorRes color: Int): Config {
            tipTextColor = color
            return config
        }

        fun setReloadButtonTextSize(sp: Int): Config {
            buttonTextSize = sp
            return config
        }

        fun setReloadButtonTextColor(@ColorRes color: Int): Config {
            buttonTextColor = color
            return config
        }

        fun setReloadButtonBackgroundResource(@DrawableRes id: Int): Config {
            reloadBtnId = id
            return config
        }

        fun setReloadButtonWidthAndHeight(width_dp: Int, height_dp: Int): Config {
            buttonWidth = width_dp
            buttonHeight = height_dp
            return config
        }

        fun setErrorImage(@DrawableRes id: Int): Config {
            errorImgId = id
            return config
        }

        fun setEmptyImage(@DrawableRes id: Int): Config {
            emptyImgId = id
            return this
        }

        fun setNoNetworkImage(@DrawableRes id: Int): Config {
            networkImgId = id
            return config
        }

        fun setLoadingPageLayout(@LayoutRes id: Int): Config {
            loadingLayoutId = id
            return config
        }

        fun setAllPageBackgroundColor(@ColorRes color: Int): Config {
            backgroundColor = color
            return config
        }
    }

    companion object {

        const val Success = 0
        const val Empty = 1
        const val Error = 2
        const val No_Network = 3
        const val Loading = 4

        /**
         * 配置
         */
        /**
         * 获取全局配置的class
         *
         * @return
         */
        val config = Config()
        private var emptyStr = "暂无数据"
        private var errorStr = "加载失败，请稍后重试···"
        private var networkStr = "无网络连接，请检查网络···"
        private var reloadBtnStr = "点击重试"
        private var emptyImgId = R.mipmap.empty
        private var errorImgId = R.mipmap.error
        private var networkImgId = R.mipmap.no_network
        private var reloadBtnId = R.drawable.selector_btn_back_gray
        private var tipTextSize = 14
        private var buttonTextSize = 14
        private var tipTextColor = R.color.base_text_color_light
        private var buttonTextColor = R.color.base_text_color_light
        private var buttonWidth = -1
        private var buttonHeight = -1
        private var loadingLayoutId = R.layout.widget_loading_page
        private var backgroundColor = R.color.base_loading_background

        private fun getColor(context: Context, @ColorRes id: Int): Int {
            return ContextCompat.getColor(context, id)
        }

        private fun getString(context: Context, @StringRes id: Int): String {
            return context.resources.getString(id)
        }

        private fun sp2px(context: Context, spValue: Float): Int {
            val fontScale = context.resources.displayMetrics
                .scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        private fun dp2px(context: Context, dip: Int): Int {
            val scale = context.resources.displayMetrics.density
            return (dip * scale + 0.5f).toInt()
        }
    }


}
