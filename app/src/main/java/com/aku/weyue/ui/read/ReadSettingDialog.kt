package com.aku.weyue.ui.read

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aku.weyue.R
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BrightnessUtils
import com.page.view.PageLoader
import com.page.view.PageView
import com.page.view.ReadSettingManager
import com.page.view.utils.ScreenUtils
import java.util.*

/**
 * Created by newbiechen on 17-5-18.
 */

class ReadSettingDialog(private val mActivity: Activity, private val mPageLoader: PageLoader) :
    Dialog(mActivity, R.style.ReadSettingDialog) {

    private var colorBg = intArrayOf(
        R.color.color_cec29c,
        R.color.color_ccebcc,
        R.color.color_aaa,
        R.color.color_d1cec5,
        R.color.color_001c27
    )

    lateinit var mIvBrightnessMinus: ImageView
    lateinit var mSbBrightness: SeekBar
    lateinit var mIvBrightnessPlus: ImageView
    lateinit var mCbBrightnessAuto: CheckBox
    lateinit var mTvFontMinus: TextView
    lateinit var mTvFont: TextView
    lateinit var mTvFontPlus: TextView
    lateinit var mCbFontDefault: CheckBox
    lateinit var mRgPageMode: RadioGroup

    lateinit var mRbSimulation: RadioButton
    lateinit var mRbCover: RadioButton
    lateinit var mRbSlide: RadioButton
    lateinit var mRbScroll: RadioButton
    lateinit var mRbNone: RadioButton
    lateinit var mRvBg: RecyclerView
    /** */
    private var mReadBgAdapter: ReadBgAdapter? = null

    private var mBrightness: Int = 0
    private var isBrightnessAuto: Boolean = false
    private var mTextSize: Int = 0
    private var isTextDefault: Boolean = false
    private var mPageMode: Int = 0
    private var mReadBgTheme: Int = 0
    private val mReadBgBeans = ArrayList<ReadBgBean>()

    val isBrightFollowSystem: Boolean
        get() = mCbBrightnessAuto.isChecked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_read_setting)
        initView()
        setUpWindow()
        initData()
        initWidget()
        initClick()
    }

    private fun initView() {
        mIvBrightnessMinus = findViewById(R.id.read_setting_iv_brightness_minus)
        mSbBrightness = findViewById(R.id.read_setting_sb_brightness)
        mIvBrightnessPlus = findViewById(R.id.read_setting_iv_brightness_plus)
        mCbBrightnessAuto = findViewById(R.id.read_setting_cb_brightness_auto)
        mTvFontMinus = findViewById(R.id.read_setting_tv_font_minus)
        mTvFont = findViewById(R.id.read_setting_tv_font)
        mTvFontPlus = findViewById(R.id.read_setting_tv_font_plus)
        mCbFontDefault = findViewById(R.id.read_setting_cb_font_default)
        mRgPageMode = findViewById(R.id.read_setting_rg_page_mode)
        mRbSimulation = findViewById(R.id.read_setting_rb_simulation)
        mRbCover = findViewById(R.id.read_setting_rb_cover)
        mRbSlide = findViewById(R.id.read_setting_rb_slide)
        mRbScroll = findViewById(R.id.read_setting_rb_scroll)
        mRbNone = findViewById(R.id.read_setting_rb_none)
        mRvBg = findViewById(R.id.read_setting_rv_bg)


    }


    //设置Dialog显示的位置
    private fun setUpWindow() {
        val window = window
        val lp = window!!.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.BOTTOM
        window.attributes = lp
    }

    private fun initData() {

        isBrightnessAuto = ReadSettingManager.isBrightnessAuto
        mBrightness = ReadSettingManager.brightness
        mTextSize = ReadSettingManager.textSize
        isTextDefault = ReadSettingManager.isDefaultTextSize
        mPageMode = ReadSettingManager.pageMode
        mReadBgTheme = ReadSettingManager.readBgTheme
    }

    @SuppressLint("SetTextI18n")
    private fun initWidget() {
        mSbBrightness.progress = mBrightness
        mTvFont.text = mTextSize.toString()
        mCbBrightnessAuto.isChecked = isBrightnessAuto
        mCbFontDefault.isChecked = isTextDefault
        initPageMode()
        //RecyclerView
        setUpAdapter()
    }

    private fun setUpAdapter() {
        setReadBg(0)
        mReadBgAdapter = ReadBgAdapter(mReadBgBeans)
        //横向列表
        //        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //        mRvBg.setLayoutManager(linearLayoutManager);
        mRvBg.layoutManager = GridLayoutManager(context, 5)
        mRvBg.adapter = mReadBgAdapter

    }

    /**
     * 设置选择背景数据
     *
     * @param selectPos 选中下标
     */
    private fun setReadBg(selectPos: Int) {
        mReadBgBeans.clear()
        for (i in colorBg.indices) {
            val readBgBean = ReadBgBean()
            readBgBean.bgColor = colorBg[i]
            readBgBean.isSelect = i == selectPos
            mReadBgBeans.add(readBgBean)
        }
    }

    private fun initPageMode() {
        when (mPageMode) {
            PageView.PAGE_MODE_SIMULATION -> mRbSimulation.isChecked = true
            PageView.PAGE_MODE_COVER -> mRbCover.isChecked = true
            PageView.PAGE_MODE_SLIDE -> mRbSlide.isChecked = true
            PageView.PAGE_MODE_NONE -> mRbNone.isChecked = true
            PageView.PAGE_MODE_SCROLL -> mRbScroll.isChecked = true
            else -> {
            }
        }
    }

    private fun getDrawable(drawRes: Int): Drawable? {
        return ContextCompat.getDrawable(context, drawRes)
    }

    @SuppressLint("SetTextI18n")
    private fun initClick() {
        //亮度调节
        mIvBrightnessMinus.setOnClickListener { v ->
            if (mCbBrightnessAuto.isChecked) {
                mCbBrightnessAuto.isChecked = false
            }
            val progress = mSbBrightness.progress - 1
            if (progress < 0) {
                return@setOnClickListener
            }
            mSbBrightness.progress = progress
            BrightnessUtils.setWindowBrightness(
                ActivityUtils.getTopActivity().window,
                progress
            )
        }
        mIvBrightnessPlus.setOnClickListener {
            if (mCbBrightnessAuto.isChecked) {
                mCbBrightnessAuto.isChecked = false
            }
            val progress = mSbBrightness.progress + 1
            if (progress > mSbBrightness.max) {
                return@setOnClickListener
            }
            mSbBrightness.progress = progress
            BrightnessUtils.setWindowBrightness(
                ActivityUtils.getTopActivity().window,
                progress
            )
            //设置进度
            ReadSettingManager.brightness = progress
        }

        mSbBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            @SuppressLint("CheckResult")
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val progress = seekBar.progress
                if (mCbBrightnessAuto.isChecked) {
                    mCbBrightnessAuto.isChecked = false
                }
                //设置当前 Activity 的亮度
                BrightnessUtils.setWindowBrightness(
                    ActivityUtils.getTopActivity().window,
                    progress
                )
                //存储亮度的进度条
                ReadSettingManager.brightness = progress
            }
        })

        mCbBrightnessAuto.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //获取屏幕的亮度
                BrightnessUtils.setWindowBrightness(
                    ActivityUtils.getTopActivity().window,
                    BrightnessUtils.getBrightness()
                )
            } else {
                //获取进度条的亮度
                BrightnessUtils.setWindowBrightness(
                    ActivityUtils.getTopActivity().window,
                    BrightnessUtils.getBrightness()
                )
            }
            ReadSettingManager.setAutoBrightness(isChecked)

        }

        //字体大小调节
        mTvFontMinus.setOnClickListener { _ ->
            if (mCbFontDefault.isChecked) {
                mCbFontDefault.isChecked = false
            }
            val fontSize = Integer.valueOf(mTvFont.text.toString()) - 1
            if (fontSize < 0) {
                return@setOnClickListener
            }
            mTvFont.text = fontSize.toString()
            mPageLoader.setTextSize(fontSize)
        }

        mTvFontPlus.setOnClickListener { v ->
            if (mCbFontDefault.isChecked) {
                mCbFontDefault.isChecked = false
            }
            val fontSize = Integer.valueOf(mTvFont.text.toString()) + 1
            mTvFont.text = fontSize.toString()
            mPageLoader.setTextSize(fontSize)
        }

        mCbFontDefault.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val fontSize = ScreenUtils.dpToPx(DEFAULT_TEXT_SIZE)
                mTvFont.text = fontSize.toString()
                mPageLoader.setTextSize(fontSize)
            }
        }

        //Page Mode 切换
        mRgPageMode.setOnCheckedChangeListener { group, checkedId ->
            var pageMode = 0
            when (checkedId) {
                R.id.read_setting_rb_simulation -> pageMode = PageView.PAGE_MODE_SIMULATION
                R.id.read_setting_rb_cover -> pageMode = PageView.PAGE_MODE_COVER
                R.id.read_setting_rb_slide -> pageMode = PageView.PAGE_MODE_SLIDE
                R.id.read_setting_rb_scroll -> pageMode = PageView.PAGE_MODE_SCROLL
                R.id.read_setting_rb_none -> pageMode = PageView.PAGE_MODE_NONE
                else -> {
                }
            }
            mPageLoader.setPageMode(pageMode)
        }

        //背景的点击事件
        mReadBgAdapter!!.setOnItemClickListener { adapter, _, position ->
            mPageLoader.setBgColor(position)
            setReadBg(position)
            adapter.notifyDataSetChanged()
        }

    }

    companion object {
        private val TAG = "ReadSettingDialog"
        private val DEFAULT_TEXT_SIZE = 16
    }
}
