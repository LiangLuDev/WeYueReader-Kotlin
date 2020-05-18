package com.aku.weyue.ui.read

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PowerManager
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.aku.aac.core.BaseFragment
import com.aku.weyue.R
import com.aku.weyue.api.BookApi
import com.aku.weyue.data.BookChaptersBean
import com.aku.weyue.data.BookRecordBean
import com.aku.weyue.test.LocalBook
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.BrightnessUtils
import com.page.view.DataConfig
import com.page.view.PageLoader
import com.page.view.PageView
import com.page.view.ReadSettingManager
import com.page.view.data.BaseRecord
import com.page.view.data.BookChapterBean
import com.page.view.data.CollBookBean
import com.page.view.data.TxtChapter
import kotlinx.android.synthetic.main.read_frag.*
import org.koin.android.ext.android.inject

/**
 * @author Zsc
 * @date   2019/5/3
 * @desc
 */
class ReadFragment : BaseFragment(), IBookChapters {

    override val layout = R.layout.read_frag

    private val args: ReadFragmentArgs by navArgs()

    private val bookApi: BookApi by inject()

    private lateinit var mPageLoader: PageLoader
    private lateinit var mCollBook: CollBookBean

    private var isNightMode = false
    internal var mTxtChapters: MutableList<TxtChapter> = mutableListOf()
    lateinit var mReadCategoryAdapter: ReadCategoryAdapter
    private var bookChapterList: MutableList<BookChapterBean> = mutableListOf()
    private lateinit var mWakeLock: PowerManager.WakeLock

    private val mSettingDialog: ReadSettingDialog by lazy {
        ReadSettingDialog(requireActivity(), mPageLoader)
    }

    private val mTopInAnim: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_top_in)
    }
    private val mTopOutAnim: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_top_out)
            .apply { duration = 200 }
    }
    private val mBottomInAnim: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_bottom_in)
    }
    private val mBottomOutAnim: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_bottom_out)
            .apply { duration = 200 }
    }
    // 接收电池信息和时间更新的广播
    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra("level", 0)
                mPageLoader.updateBattery(level)
            } else if (intent.action == Intent.ACTION_TIME_TICK) {
                mPageLoader.updateTime()
            }//监听分钟的变化
        }
    }

    @SuppressLint("InvalidWakeLockTag", "CheckResult")
    override fun initData(savedInstanceState: Bundle?) {

        mCollBook = args.coolBook ?: LocalBook.bookDig
        mPageLoader = pv_read_page.getPageLoader(mCollBook.isLocal)
        read_dl_slide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        showSystemBar(false)
        setCategory()
        initListener()
        if (mCollBook.isLocal) {
            mPageLoader.openBook(mCollBook)
        } else {
            ReadHelper.loadChapters(bookApi, mCollBook.bookId, this)
        }
        toggleNightMode()
        initBackListener()
        //注册广播
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        intentFilter.addAction(Intent.ACTION_TIME_TICK)
        requireActivity().registerReceiver(mReceiver, intentFilter)

        //设置当前Activity的Brightness
        if (ReadSettingManager.isBrightnessAuto) {
            BrightnessUtils.setWindowBrightness(
                requireActivity().window,
                BrightnessUtils.getBrightness()
            )
        } else {
            BrightnessUtils.setWindowBrightness(
                requireActivity().window,
                ReadSettingManager.brightness
            )
//                    }
//                }
        }

        //初始化屏幕常亮类
        val pm = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "keep bright")

    }


    /**
     * 拦截返回键
     */
    private fun initBackListener() {
        requireActivity().onBackPressedDispatcher
            .addCallback(this) {
                if (read_abl_top_menu.isVisible) {
                    toggleMenu()
                    return@addCallback
                }
                if (read_dl_slide.isDrawerOpen(Gravity.LEFT)) {
                    read_dl_slide.closeDrawer(Gravity.LEFT)
                    return@addCallback
                }
                findNavController().popBackStack()
            }
    }

    @SuppressLint("WrongConstant")
    private fun initListener() {
        mPageLoader.dataConfig = object : DataConfig {
            override fun saveRecord(bookRecord: BaseRecord) {

            }

            override fun getRecordById(bookId: String): BaseRecord {
                return BookRecordBean()
            }

        }

        mPageLoader.setOnPageChangeListener(object : PageLoader.OnPageChangeListener {
            override fun onChapterChange(pos: Int) {
                setCategorySelect(pos)

            }

            override fun onLoadChapter(chapters: List<TxtChapter>, pos: Int) {
                ReadHelper.loadContent(bookApi, mCollBook.bookId, chapters, this@ReadFragment)
                setCategorySelect(mPageLoader.chapterPos)

                if (mPageLoader.pageStatus == PageLoader.STATUS_LOADING
                    || mPageLoader.pageStatus == PageLoader.STATUS_ERROR
                ) {
                    //冻结使用
                    read_sb_chapter_progress.isEnabled = false
                }

                //隐藏提示
                read_tv_page_tip.isVisible = false
                read_sb_chapter_progress.progress = 0
            }

            override fun onCategoryFinish(chapters: List<TxtChapter>) {
                mTxtChapters.clear()
                mTxtChapters.addAll(chapters)
                mReadCategoryAdapter.notifyDataSetChanged()
            }

            override fun onPageCountChange(count: Int) {
                read_sb_chapter_progress.isEnabled = true
                read_sb_chapter_progress.max = count - 1
                read_sb_chapter_progress.progress = 0
            }

            override fun onPageChange(pos: Int) {
                read_sb_chapter_progress.post {
                    read_sb_chapter_progress.progress = pos
                }
            }
        })


        read_sb_chapter_progress.setOnSeekBarChangeListener(object
            : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (read_ll_bottom_menu.visibility == View.VISIBLE) {
                    //显示标题
                    read_tv_page_tip.text = (progress + 1).toString() + "/" + (read_sb_chapter_progress.max + 1)
                    read_tv_page_tip.visibility = View.VISIBLE
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //进行切换
                val pagePos = read_sb_chapter_progress.progress
                if (pagePos != mPageLoader.pagePos) {
                    mPageLoader.skipToPage(pagePos)
                }
                //隐藏提示
                read_tv_page_tip.visibility = View.GONE
            }
        })

        pv_read_page.setTouchListener(object : PageView.TouchListener {
            override fun center() {
                toggleMenu()
            }

            override fun onTouch(): Boolean {
                return !hideReadMenu()
            }

            override fun prePage(): Boolean {
                return true
            }

            override fun nextPage(): Boolean {
                return true
            }

            override fun cancel() {}
        })
        read_tv_setting.setOnClickListener {
            toggleMenu()
            mSettingDialog.show()
        }
        tv_toolbar_title.setOnClickListener {
            findNavController().navigateUp()
        }
        read_tv_category.setOnClickListener {
            setCategorySelect(mPageLoader.chapterPos)
            //切换菜单
            toggleMenu()
            //打开侧滑动栏
            read_dl_slide.openDrawer(Gravity.START)
        }
        read_tv_pre_chapter.setOnClickListener {
            setCategorySelect(mPageLoader.skipPreChapter())
        }
        read_tv_next_chapter.setOnClickListener {
            setCategorySelect(mPageLoader.skipNextChapter())
        }
        read_tv_night_mode.setOnClickListener {
            isNightMode = !isNightMode
            mPageLoader.setNightMode(isNightMode)
            toggleNightMode()
        }
    }


    private fun setCategory() {
        rv_read_category.layoutManager = LinearLayoutManager(context)
        mReadCategoryAdapter = ReadCategoryAdapter(mTxtChapters)
        rv_read_category.adapter = mReadCategoryAdapter

        if (mTxtChapters.size > 0) {
            setCategorySelect(0)
        }

        mReadCategoryAdapter.setOnItemClickListener { _, _, position ->
            setCategorySelect(position)
            read_dl_slide.closeDrawer(Gravity.LEFT)
            mPageLoader.skipToChapter(position)
        }

    }

    /**
     * 设置选中目录
     *
     * @param selectPos
     */
    private fun setCategorySelect(selectPos: Int) {
        for (i in mTxtChapters.indices) {
            val chapter = mTxtChapters[i]
            chapter.isSelect = i == selectPos
        }
        mReadCategoryAdapter.notifyDataSetChanged()
    }

    override fun bookChapters(bookChaptersBean: BookChaptersBean) {
        bookChapterList.clear()
        for (bean in bookChaptersBean.chapters) {
            val chapterBean = BookChapterBean()
            chapterBean.bookId = bookChaptersBean.book
            chapterBean.link = bean.link
            chapterBean.title = bean.title
            //chapterBean.setTaskName("下载");
            chapterBean.unreadble = bean.isRead
            bookChapterList.add(chapterBean)
        }
        mCollBook.bookChapters = bookChapterList
        //如果是更新加载，那么重置PageLoader的Chapter
        /*if (mCollBook.isUpdate() && isCollected) {
            mPageLoader.setChapterList(bookChapterList)
            //异步下载更新的内容存到数据库
            //fixme db
//            BookChapterHelper.getsInstance()
//                .saveBookChaptersWithAsync(bookChapterList)

        } else {*/
        mPageLoader.openBook(mCollBook)
//        }


    }

    override fun finishChapters() {
        if (mPageLoader.pageStatus == PageLoader.STATUS_LOADING) {
            pv_read_page.post {
                mPageLoader.openChapter()
                //当完成章节的时候，刷新列表
                mReadCategoryAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun toggleNightMode() {
        if (isNightMode) {
            read_tv_night_mode.text = getString(R.string.wy_mode_morning)
            val drawable = ContextCompat.getDrawable(requireContext(), R.mipmap.read_menu_morning)
            read_tv_night_mode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        } else {
            read_tv_night_mode.text = getString(R.string.wy_mode_night)
            val drawable = ContextCompat.getDrawable(requireContext(), R.mipmap.read_menu_night)
            read_tv_night_mode.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        }
    }


    override fun errorChapters() {
        if (mPageLoader.pageStatus == PageLoader.STATUS_LOADING) {
            mPageLoader.chapterError()
        }
    }

    override fun onDestroy() {
        BarUtils.setStatusBarVisibility(requireActivity(), true)
        BrightnessUtils.setWindowBrightness(
            requireActivity().window,
            BrightnessUtils.getBrightness()
        )
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mWakeLock.acquire(10 * 60 * 1000L)
    }

    override fun onPause() {
        super.onPause()
        mWakeLock.release()
//        if (isCollected) {
//            mPageLoader.saveRecord()
//        }
    }

    /**
     * 切换菜单栏的可视状态
     * 默认是隐藏的
     */
    private fun toggleMenu() {
        if (read_abl_top_menu.visibility == View.VISIBLE) {
            //关闭
            read_abl_top_menu.startAnimation(mTopOutAnim)
            read_abl_top_menu.isVisible = false
            read_tv_page_tip.isVisible = false
            read_ll_bottom_menu.startAnimation(mBottomOutAnim)
            read_ll_bottom_menu.visibility = View.GONE
            showSystemBar(false)
        } else {
            read_abl_top_menu.isVisible = true
            read_abl_top_menu.startAnimation(mTopInAnim)

            read_ll_bottom_menu.visibility = View.VISIBLE
            read_ll_bottom_menu.startAnimation(mBottomInAnim)
            showSystemBar(true)
        }
    }

    private fun showSystemBar(show: Boolean) {
        BarUtils.setStatusBarVisibility(requireActivity(), show)
    }


    private fun hideReadMenu(): Boolean {
        showSystemBar(false)
        if (read_abl_top_menu.isVisible) {
            toggleMenu()
            return true
        } else if (mSettingDialog.isShowing) {
            mSettingDialog.dismiss()
            return true
        }
        return false
    }


}