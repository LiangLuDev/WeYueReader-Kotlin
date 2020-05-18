package com.page.view

import android.graphics.*
import android.text.TextPaint
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ToastUtils
import com.page.view.data.*
import com.page.view.utils.*
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

abstract class PageLoader(//页面显示类
    private val mPageView: PageView
) {
    lateinit var dataConfig: DataConfig

    //当前章节列表
    protected var mChapterList: List<TxtChapter>? = null
    //书本对象
    protected lateinit var mCollBook: CollBookBean
    //监听器
    protected var mPageChangeListener: OnPageChangeListener? = null
    //当前显示的页
    private var mCurPage: TxtPage? = null
    //上一章的页面列表缓存
    private var mWeakPrePageList: WeakReference<List<TxtPage>>? = null
    //当前章节的页面列表
    private var mCurPageList: List<TxtPage>? = null
    //下一章的页面列表缓存
    private var mNextPageList: List<TxtPage>? = null

    //下一页绘制缓冲区，用户缓解卡顿问题。
    private var mNextBitmap: Bitmap? = null

    //绘制电池的画笔
    private val mBatteryPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            isDither = true
            color = if (isNightMode) {
                Color.WHITE
            } else {
                Color.BLACK
            }
        }

    }
    //绘制提示的画笔
    private val mTipPaint: Paint by lazy {
        Paint().apply {
            color = mTextColor
            textAlign = Paint.Align.LEFT//绘制的起始点
            textSize = ScreenUtils.spToPx(DEFAULT_TIP_SIZE).toFloat()//Tip默认的字体大小
            isAntiAlias = true
            isSubpixelText = true
        }
    }
    //绘制标题的画笔
    private val mTitlePaint: Paint by lazy {
        TextPaint().apply {
            color = mTextColor
            textSize = mTitleSize.toFloat()
            style = Paint.Style.FILL_AND_STROKE
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
    }
    //绘制背景颜色的画笔(用来擦除需要重绘的部分)
    private val mBgPaint: Paint by lazy {
        Paint().apply {
            color = mPageBg
        }
    }
    //绘制小说内容的画笔
    private val mTextPaint: TextPaint by lazy {
        TextPaint().apply {
            color = mTextColor
            textSize = mTextSize.toFloat()
            isAntiAlias = true
        }
    }
    //被遮盖的页，或者认为被取消显示的页
    private var mCancelPage: TxtPage? = null
    //存储阅读记录类
    private var mBookRecord: BaseRecord? = null
    /*****************params */
    //当前的状态
    //获取当前页的状态
    var pageStatus = STATUS_LOADING
        protected set
    //当前章
    //获取当前章节的章节位置
    var chapterPos = 0
        protected set
    //书本是否打开
    protected var isBookOpen = false

    private var mPreLoadDisp: Disposable? = null
    //上一章的记录
    private var mLastChapter = 0
    //书籍绘制区域的宽高
    private var mVisibleWidth: Int = 0
    private var mVisibleHeight: Int = 0
    //应用的宽高
    private var mDisplayWidth: Int = 0
    private var mDisplayHeight: Int = 0
    //间距
    private var mMarginWidth: Int = 0
    private var mMarginHeight: Int = 0
    //字体的颜色
    private var mTextColor: Int = 0
    //标题的大小
    private var mTitleSize: Int = 0
    //字体的大小
    private var mTextSize: Int = 0
    //行间距
    private var mTextInterval: Int = 0
    //标题的行间距
    private var mTitleInterval: Int = 0
    //段落距离(基于行间距的额外距离)
    private var mTextPara: Int = 0
    private var mTitlePara: Int = 0
    //电池的百分比
    private var mBatteryLevel: Int = 0
    //页面的翻页效果模式
    private var mPageMode: Int = 0
    //加载器的颜色主题
    private var mBgTheme: Int = 0
    //当前页面的背景
    private var mPageBg: Int = 0
    //当前是否是夜间模式
    private var isNightMode: Boolean = false

    //获取当前页的页码
    val pagePos: Int
        get() = mCurPage!!.position


    /**************************************private method */

    /**
     * @return:获取上一个页面
     */
    private val prevPage: TxtPage?
        get() {
            val pos = mCurPage!!.position - 1
            if (pos < 0) {
                return null
            }
            if (mPageChangeListener != null) {
                mPageChangeListener!!.onPageChange(pos)
            }
            return mCurPageList!![pos]
        }

    /**
     * @return:获取下一的页面
     */
    private val nextPage: TxtPage?
        get() {
            if (null != mCurPage) {
                val pos = mCurPage!!.position + 1
                if (pos >= mCurPageList!!.size) {
                    return null
                }
                if (mPageChangeListener != null) {
                    mPageChangeListener!!.onPageChange(pos)
                }
                return mCurPageList!![pos]
            }
            return TxtPage()
        }

    /**
     * @return:获取上一个章节的最后一页
     */
    private val prevLastPage: TxtPage
        get() {
            val pos = mCurPageList!!.size - 1
            return mCurPageList!![pos]
        }

    init {
        mTextSize = ReadSettingManager.textSize
        mTitleSize = mTextSize + ScreenUtils.spToPx(EXTRA_TITLE_SIZE)
        mPageMode = ReadSettingManager.pageMode
        isNightMode = ReadSettingManager.isNightMode
        mBgTheme = ReadSettingManager.readBgTheme

        if (isNightMode) {
            setBgColor(ReadSettingManager.NIGHT_MODE)
        } else {
            setBgColor(mBgTheme)
        }
        //初始化参数
        mMarginWidth = ScreenUtils.dpToPx(DEFAULT_MARGIN_WIDTH)
        mMarginHeight = ScreenUtils.dpToPx(DEFAULT_MARGIN_HEIGHT)
        mTextInterval = mTextSize / 2
        mTitleInterval = mTitleSize / 2
        mTextPara = mTextSize //段落间距由 text 的高度决定。
        mTitlePara = mTitleSize
        //配置参数
        mPageView.setPageMode(mPageMode)
        mPageView.setBgColor(mPageBg)
    }


    /****************************** public method */
    //跳转到上一章
    fun skipPreChapter(): Int {
        if (!isBookOpen) {
            return chapterPos
        }

        //载入上一章。
        if (prevChapter()) {
            mCurPage = getCurPage(0)
            mPageView.refreshPage()
        }
        return chapterPos
    }

    //跳转到下一章
    fun skipNextChapter(): Int {
        if (!isBookOpen) {
            return chapterPos
        }

        //判断是否达到章节的终止点
        if (nextChapter()) {
            mCurPage = getCurPage(0)
            mPageView.refreshPage()
        }
        return chapterPos
    }

    //跳转到指定章节
    open fun skipToChapter(pos: Int) {
        //正在加载
        pageStatus = STATUS_LOADING
        //绘制当前的状态
        chapterPos = pos
        //将上一章的缓存设置为null
        mWeakPrePageList = null

        //如果当前下一章缓存正在执行，则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp!!.dispose()
        }
        //将下一章缓存设置为null
        mNextPageList = null
        mPageChangeListener?.onChapterChange(chapterPos)
        mCurPage?.position = 0
        //需要对ScrollAnimation进行重新布局
        mPageView.refreshPage()
    }

    //跳转到具体的页
    fun skipToPage(pos: Int) {
        mCurPage = getCurPage(pos)
        mPageView.refreshPage()
    }

    //自动翻到上一章
    fun autoPrevPage(): Boolean {
        return if (!isBookOpen) false else mPageView.autoPrevPage()
    }

    //自动翻到下一章
    fun autoNextPage(): Boolean {
        return if (!isBookOpen) false else mPageView.autoNextPage()
    }

    //更新时间
    fun updateTime() {
        if (mPageView.isPrepare && !mPageView.isRunning) {
            mPageView.drawCurPage(true)
        }
    }

    //更新电量
    fun updateBattery(level: Int) {
        mBatteryLevel = level
        if (mPageView.isPrepare && !mPageView.isRunning) {
            mPageView.drawCurPage(true)
        }
    }

    //设置文字大小
    fun setTextSize(textSize: Int) {
        if (!isBookOpen) {
            return
        }

        //设置textSize
        mTextSize = textSize
        mTextInterval = mTextSize / 2
        mTextPara = mTextSize
        mTitleSize = mTextSize + ScreenUtils.spToPx(EXTRA_TITLE_SIZE)
        mTitleInterval /= 2
        mTitlePara = mTitleSize

        //设置画笔的字体大小
        mTextPaint.textSize = mTextSize.toFloat()
        //设置标题的字体大小
        mTitlePaint.textSize = mTitleSize.toFloat()
        //存储状态
        ReadSettingManager.textSize = mTextSize
        //取消缓存
        mWeakPrePageList = null
        mNextPageList = null
        //如果当前为完成状态。
        if (pageStatus == STATUS_FINISH) {
            //重新计算页面
            mCurPageList = loadPageList(chapterPos)

            //防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
            if (mCurPage!!.position >= mCurPageList!!.size) {
                mCurPage!!.position = mCurPageList!!.size - 1
            }
        }
        //重新设置文章指针的位置
        mCurPage = getCurPage(mCurPage!!.position)
        //绘制
        mPageView.refreshPage()
    }

    //设置夜间模式
    fun setNightMode(nightMode: Boolean) {
        isNightMode = nightMode
        if (isNightMode) {
            mBatteryPaint.color = Color.WHITE
            setBgColor(ReadSettingManager.NIGHT_MODE)
        } else {
            mBatteryPaint.color = Color.BLACK
            setBgColor(mBgTheme)
        }
        ReadSettingManager.isNightMode = nightMode
    }

    //绘制背景
    fun setBgColor(theme: Int) {
        if (isNightMode && theme == ReadSettingManager.NIGHT_MODE) {
            mTextColor = ContextCompat.getColor(BookUtils.mAppContext, R.color.color_fff_99)
            mPageBg = ContextCompat.getColor(BookUtils.mAppContext, R.color.black)
        } else if (isNightMode) {
            mBgTheme = theme
            ReadSettingManager.setReadBackground(theme)
        } else {
            ReadSettingManager.setReadBackground(theme)
            when (theme) {
                ReadSettingManager.READ_BG_DEFAULT -> {
                    mTextColor = ContextCompat.getColor(BookUtils.mAppContext, R.color.color_2c)
                    mPageBg = ContextCompat.getColor(BookUtils.mAppContext, R.color.color_cec29c)
                }
                ReadSettingManager.READ_BG_1 -> {
                    mTextColor = ContextCompat.getColor(BookUtils.mAppContext, R.color.color_2f332d)
                    mPageBg = ContextCompat.getColor(BookUtils.mAppContext, R.color.color_ccebcc)
                }
                ReadSettingManager.READ_BG_2 -> {
                    mTextColor = ContextCompat.getColor(BookUtils.mAppContext, R.color.color_92918c)
                    mPageBg = ContextCompat.getColor(BookUtils.mAppContext, R.color.color_aaa)
                }
                ReadSettingManager.READ_BG_3 -> {
                    mTextColor = ContextCompat.getColor(BookUtils.mAppContext, R.color.color_383429)
                    mPageBg = ContextCompat.getColor(BookUtils.mAppContext, R.color.color_d1cec5)
                }
                ReadSettingManager.READ_BG_4 -> {
                    mTextColor = ContextCompat.getColor(BookUtils.mAppContext, R.color.color_627176)
                    mPageBg = ContextCompat.getColor(BookUtils.mAppContext, R.color.color_001c27)
                }
            }
        }

        if (isBookOpen) {
            //设置参数
            mPageView.setBgColor(mPageBg)
            mTextPaint.color = mTextColor
            //重绘
            mPageView.refreshPage()
        }
    }

    //翻页动画
    fun setPageMode(pageMode: Int) {
        mPageMode = pageMode
        mPageView.setPageMode(mPageMode)
        ReadSettingManager.pageMode = mPageMode
        //重绘
        mPageView.drawCurPage(false)
    }

    //设置页面切换监听
    open fun setOnPageChangeListener(listener: OnPageChangeListener) {
        mPageChangeListener = listener
    }

    //保存阅读记录
    open fun saveRecord() {
        //书没打开，就没有记录
        if (!isBookOpen) {
            return
        }

        mBookRecord?.run {
            bookId = mCollBook.bookId
            chapter = chapterPos
            pagePos = mCurPage!!.position
            dataConfig.saveRecord(this)
        }

        //存储到数据库
        //        BookRecordHelper.getsInstance().saveRecordBook(mBookRecord);
    }

    //打开书本，初始化书籍
    open fun openBook(collBook: CollBookBean) {
        mCollBook = collBook
        //init book record

        //从数据库取阅读数据
        if (mBookRecord == null) {
            mBookRecord = dataConfig.getRecordById(collBook.bookId)
        }

        chapterPos = mBookRecord!!.chapter
        mLastChapter = chapterPos
    }

    //打开具体章节
    fun openChapter() {
        mCurPageList = loadPageList(chapterPos)
        //进行预加载
        preLoadNextChapter()
        //加载完成
        pageStatus = STATUS_FINISH
        //获取制定页面
        if (!isBookOpen) {
            isBookOpen = true
            //可能会出现当前页的大小大于记录页的情况。
            var position = mBookRecord!!.pagePos
            if (position >= mCurPageList!!.size) {
                position = mCurPageList!!.size - 1
            }
            mCurPage = getCurPage(position)
            mCancelPage = mCurPage
            if (mPageChangeListener != null) {
                mPageChangeListener!!.onChapterChange(chapterPos)
            }
        } else {
            mCurPage = getCurPage(0)
        }

        mPageView.drawCurPage(false)
    }

    fun chapterError() {
        //加载错误
        pageStatus = STATUS_ERROR
        //显示加载错误
        mPageView.drawCurPage(false)
    }

    //清除记录，并设定是否缓存数据
    open fun closeBook() {
        isBookOpen = false
        if (mPreLoadDisp != null) {
            mPreLoadDisp!!.dispose()
        }
    }

    /*******************************abstract method */
    //设置章节
    abstract fun setChapterList(bookChapters: List<BookChapterBean>)

    protected abstract fun loadPageList(chapter: Int): List<TxtPage>

    /***********************************default method */
    //通过流获取Page的方法
    internal fun loadPages(chapter: Int, txtChapter: TxtChapter, br: BufferedReader): List<TxtPage> {
        //生成的页面
        val pages = ArrayList<TxtPage>()
        //使用流的方式加载
        val lines = ArrayList<String>()
        var rHeight = mVisibleHeight //由于匹配到最后，会多删除行间距，所以在这里多加个行间距
        var titleLinesCount = 0
        var isTitle = true //不存在没有 Title 的情况，所以默认设置为 true。
        var paragraph: String? = txtChapter.title
//                //默认展示标题
        try {


            while (isTitle || run {
                    paragraph = br.readLine()
                    paragraph
                } != null) {

                //重置段落
                if (!isTitle) {
                    paragraph = paragraph!!.replace("\\s".toRegex(), "")
                    //如果只有换行符，那么就不执行
                    if (paragraph == "") {
                        continue
                    }
                    paragraph = StringUtils.halfToFull("  $paragraph\n")
                } else {
                    //设置 title 的顶部间距
                    rHeight -= mTitlePara
                }

                var wordCount: Int
                var subStr: String?
                while (paragraph!!.isNotEmpty()) {
                    //当前空间，是否容得下一行文字
                    rHeight -= if (isTitle) {
                        mTitlePaint.textSize.toInt()
                    } else {
                        mTextPaint.textSize.toInt()
                    }

                    //一页已经填充满了，创建 TextPage
                    if (rHeight < 0) {
                        //创建Page
                        val page = TxtPage()
                        page.position = pages.size
                        page.title = txtChapter.title
                        page.lines = ArrayList(lines)
                        page.titleLines = titleLinesCount
                        pages.add(page)
                        //重置Lines
                        lines.clear()
                        rHeight = mVisibleHeight
                        titleLinesCount = 0
                        continue
                    }

                    //测量一行占用的字节数
                    wordCount = if (isTitle) {
                        mTitlePaint.breakText(paragraph, true, mVisibleWidth.toFloat(), null)
                    } else {
                        mTextPaint.breakText(paragraph, true, mVisibleWidth.toFloat(), null)
                    }

                    subStr = paragraph!!.substring(0, wordCount)
                    if (subStr != "\n") {
                        //将一行字节，存储到lines中
                        lines.add(subStr)

                        //设置段落间距
                        if (isTitle) {
                            titleLinesCount += 1
                            rHeight -= mTitleInterval
                        } else {
                            rHeight -= mTextInterval
                        }
                    }
                    //裁剪
                    paragraph = paragraph!!.substring(wordCount)
                }

                //增加段落的间距
                if (!isTitle && lines.size != 0) {
                    rHeight = rHeight - mTextPara + mTextInterval
                }

                if (isTitle) {
                    rHeight = rHeight - mTitlePara + mTitleInterval
                    isTitle = false
                }
            }

            if (lines.size != 0) {
                //创建Page
                val page = TxtPage()
                page.position = pages.size
                page.title = txtChapter.title
                page.lines = ArrayList(lines)
                page.titleLines = titleLinesCount
                pages.add(page)
                //重置Lines
                lines.clear()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            IOUtils.close(br)
        }

        //可能出现内容为空的情况
        if (pages.size == 0) {
            val page = TxtPage()
            page.lines = ArrayList(1)
            pages.add(page)

            pageStatus = STATUS_EMPTY
        }
        //提示章节数量改变了。
        changePageCount(chapter, pages.size)
        return pages
    }

    private fun changePageCount(chapter: Int, pageSize: Int) {
        if (chapterPos == chapter) {
            mPageChangeListener?.onPageCountChange(pageSize)
        }

    }

    internal fun onDraw(bitmap: Bitmap, isUpdate: Boolean) {
        drawBackground(mPageView.bgBitmap, isUpdate)
        if (!isUpdate) {
            drawContent(bitmap)
        }
        //更新绘制
        mPageView.invalidate()
    }

    private fun drawBackground(bitmap: Bitmap?, isUpdate: Boolean) {
        val canvas = Canvas(bitmap!!)
        val tipMarginHeight = ScreenUtils.dpToPx(3)
        if (!isUpdate) {
            /****绘制背景 */
            canvas.drawColor(mPageBg)

            /*****初始化标题的参数 */
            //需要注意的是:绘制text的y的起始点是text的基准线的位置，而不是从text的头部的位置
            val tipTop = tipMarginHeight - mTipPaint.fontMetrics.top
            //根据状态不一样，数据不一样
            if (pageStatus != STATUS_FINISH) {
                if (mChapterList.isNullOrEmpty().not()) {
                    canvas.drawText(mChapterList!![chapterPos].title!!, mMarginWidth.toFloat(), tipTop, mTipPaint)
                }
            } else {
                canvas.drawText(mCurPage!!.title!!, mMarginWidth.toFloat(), tipTop, mTipPaint)
            }

            /******绘制页码 */
            //底部的字显示的位置Y
            val y = mDisplayHeight.toFloat() - mTipPaint.fontMetrics.bottom - tipMarginHeight.toFloat()
            //只有finish的时候采用页码
            if (pageStatus == STATUS_FINISH) {
                val percent = (mCurPage!!.position + 1).toString() + "/" + mCurPageList!!.size
                canvas.drawText(percent, mMarginWidth.toFloat(), y, mTipPaint)
            }
        } else {
            //擦除区域
            mBgPaint.color = mPageBg
            canvas.drawRect(
                (mDisplayWidth / 2).toFloat(),
                (mDisplayHeight - mMarginHeight + ScreenUtils.dpToPx(2)).toFloat(),
                mDisplayWidth.toFloat(),
                mDisplayHeight.toFloat(),
                mBgPaint
            )
        }
        /******绘制电池 */

        val visibleRight = mDisplayWidth - mMarginWidth
        val visibleBottom = mDisplayHeight - tipMarginHeight

        val outFrameWidth = mTipPaint.measureText("xxx").toInt()
        val outFrameHeight = mTipPaint.textSize.toInt()

        val polarHeight = ScreenUtils.dpToPx(6)
        val polarWidth = ScreenUtils.dpToPx(2)
        val border = 1
        val innerMargin = 1

        //电极的制作
        val polarLeft = visibleRight - polarWidth
        val polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2
        val polar = Rect(
            polarLeft, polarTop, visibleRight,
            polarTop + polarHeight - ScreenUtils.dpToPx(2)
        )

        mBatteryPaint.style = Paint.Style.FILL
        canvas.drawRect(polar, mBatteryPaint)

        //外框的制作
        val outFrameLeft = polarLeft - outFrameWidth
        val outFrameTop = visibleBottom - outFrameHeight
        val outFrameBottom = visibleBottom - ScreenUtils.dpToPx(2)
        val outFrame = Rect(outFrameLeft, outFrameTop, polarLeft, outFrameBottom)

        mBatteryPaint.style = Paint.Style.STROKE
        mBatteryPaint.strokeWidth = border.toFloat()
        canvas.drawRect(outFrame, mBatteryPaint)

        //内框的制作
        val innerWidth = (outFrame.width() - innerMargin * 2 - border) * (mBatteryLevel / 100.0f)
        val innerFrame = RectF(
            (outFrameLeft + border + innerMargin).toFloat(),
            (outFrameTop + border + innerMargin).toFloat(),
            outFrameLeft.toFloat() + border.toFloat() + innerMargin.toFloat() + innerWidth,
            (outFrameBottom - border - innerMargin).toFloat()
        )

        mBatteryPaint.style = Paint.Style.FILL
        canvas.drawRect(innerFrame, mBatteryPaint)

        /******绘制当前时间 */
        //底部的字显示的位置Y
        val y = mDisplayHeight.toFloat() - mTipPaint.fontMetrics.bottom - tipMarginHeight.toFloat()
        val time = StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_TIME)
        val x = outFrameLeft.toFloat() - mTipPaint.measureText(time) - ScreenUtils.dpToPx(4).toFloat()
        canvas.drawText(time, x, y, mTipPaint)
    }

    private fun drawContent(bitmap: Bitmap) {
        val canvas = Canvas(bitmap)

        if (mPageMode == PageView.PAGE_MODE_SCROLL) {
            canvas.drawColor(mPageBg)
        }
        /******绘制内容 */
        if (pageStatus != STATUS_FINISH) {
            //绘制字体
            var tip = ""
            when (pageStatus) {
                STATUS_LOADING -> tip = "正在拼命加载中..."
                STATUS_ERROR -> tip = "加载失败(点击边缘重试)"
                STATUS_EMPTY -> tip = "文章内容为空"
                STATUS_PARSE -> tip = "正在排版请等待..."
                STATUS_PARSE_ERROR -> tip = "文件解析错误"
            }

            //将提示语句放到正中间
            val fontMetrics = mTextPaint.fontMetrics
            val textHeight = fontMetrics.top - fontMetrics.bottom
            val textWidth = mTextPaint.measureText(tip)
            val pivotX = (mDisplayWidth - textWidth) / 2
            val pivotY = (mDisplayHeight - textHeight) / 2
            canvas.drawText(tip, pivotX, pivotY, mTextPaint)
        } else {
            var top: Float = if (mPageMode == PageView.PAGE_MODE_SCROLL) {
                -mTextPaint.fontMetrics.top
            } else {
                mMarginHeight - mTextPaint.fontMetrics.top
            }

            //设置总距离
            val interval = mTextInterval + mTextPaint.textSize.toInt()
            val para = mTextPara + mTextPaint.textSize.toInt()
            val titleInterval = mTitleInterval + mTitlePaint.textSize.toInt()
            val titlePara = mTitlePara + mTextPaint.textSize.toInt()
            var str: String?

            //对标题进行绘制
            for (i in 0 until mCurPage!!.titleLines) {
                str = mCurPage!!.lines!![i]

                //设置顶部间距
                if (i == 0) {
                    top += mTitlePara.toFloat()
                }

                //计算文字显示的起始点
                val start = (mDisplayWidth - mTitlePaint.measureText(str)).toInt() / 2
                //进行绘制
                canvas.drawText(str, start.toFloat(), top, mTitlePaint)

                //设置尾部间距
                top += if (i == mCurPage!!.titleLines - 1) {
                    titlePara.toFloat()
                } else {
                    //行间距
                    titleInterval.toFloat()
                }
            }

            //对内容进行绘制
            for (i in mCurPage!!.titleLines until mCurPage!!.lines!!.size) {
                str = mCurPage!!.lines!![i]

                canvas.drawText(str, mMarginWidth.toFloat(), top, mTextPaint)
                top += if (str.endsWith("\n")) {
                    para.toFloat()
                } else {
                    interval.toFloat()
                }
            }
        }
    }

    internal fun setDisplaySize(w: Int, h: Int) {
        //获取PageView的宽高
        mDisplayWidth = w
        mDisplayHeight = h

        //获取内容显示位置的大小
        mVisibleWidth = mDisplayWidth - mMarginWidth * 2
        mVisibleHeight = mDisplayHeight - mMarginHeight * 2

        //创建用来缓冲的 Bitmap
        mNextBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)

        //如果章节已显示，那么就重新计算页面
        if (pageStatus == STATUS_FINISH) {
            mCurPageList = loadPageList(chapterPos)
            //重新设置文章指针的位置
            mCurPage = getCurPage(mCurPage!!.position)
        }

        mPageView.drawCurPage(false)
    }

    //翻阅上一页
    internal fun prev(): Boolean {
        if (!checkStatus()) {
            return false
        }

        //判断是否达到章节的起始点
        val prevPage = prevPage
            ?: return if (!prevChapter()) {
                false
            } else {
                mCancelPage = mCurPage
                mCurPage = prevLastPage
                mPageView.drawNextPage()
                true
            }

        mCancelPage = mCurPage
        mCurPage = prevPage

        mPageView.drawNextPage()
        return true
    }

    //加载上一章
    internal open fun prevChapter(): Boolean {
        //判断是否上一章节为空
        if (chapterPos - 1 < 0) {
            ToastUtils.showShort("已经没有上一章了")
            return false
        }

        //加载上一章数据
        val prevChapter = chapterPos - 1
        //当前章变成下一章
        mNextPageList = mCurPageList

        //判断上一章缓存是否存在，如果存在则从缓存中获取数据。
        if (mWeakPrePageList != null && mWeakPrePageList!!.get() != null) {
            mCurPageList = mWeakPrePageList!!.get()
            mWeakPrePageList = null
        } else {
            mCurPageList = loadPageList(prevChapter)
        }//如果不存在则加载数据

        mLastChapter = chapterPos
        chapterPos = prevChapter

        if (mCurPageList != null) {
            pageStatus = STATUS_FINISH
        } else {
            pageStatus = STATUS_LOADING
            //重置position的位置，防止正在加载的时候退出时候存储的位置为上一章的页码
            mCurPage!!.position = 0
            mPageView.drawNextPage()
        }//如果当前章不存在，则表示在加载中

        if (mPageChangeListener != null) {
            mPageChangeListener!!.onChapterChange(chapterPos)
        }

        return true
    }

    //翻阅下一页
    fun next(): Boolean {
        if (!checkStatus()) {
            return false
        }
        //判断是否到最后一页了
        val nextPage0 = nextPage ?: return if (!nextChapter()) {
            false
        } else {
            mCancelPage = mCurPage
            mCurPage = getCurPage(0)
            mPageView.drawNextPage()
            true
        }
        mCancelPage = mCurPage
        mCurPage = nextPage0
        mPageView.drawNextPage()

        //为下一页做缓冲

        //加载下一页的文章

        return true
    }

    //缓存下一个要显示的页面
    //TODO:解决上下滑动卡顿问题
    private fun cacheNextBitmap() {

    }

    internal open fun nextChapter(): Boolean {
        //加载一章
        if (chapterPos + 1 >= mChapterList!!.size) {
            ToastUtils.showShort("已经没有下一章了")
            return false
        }

        //如果存在下一章，则存储当前Page列表为上一章
        if (mCurPageList != null) {
            mWeakPrePageList = WeakReference(ArrayList(mCurPageList!!))
        }
        mLastChapter = chapterPos
        chapterPos++
        //如果存在下一章预加载章节。
        if (mNextPageList != null) {
            mCurPageList = mNextPageList
            mNextPageList = null
            changePageCount(chapterPos, mCurPageList!!.size)
        } else {
            //这个PageList可能为 null，可能会造成问题。
            mCurPageList = loadPageList(chapterPos)
        }

        //如果存在当前章，预加载下一章
        if (mCurPageList != null) {
            pageStatus = STATUS_FINISH
            preLoadNextChapter()
        } else {
            pageStatus = STATUS_LOADING
            //重置position的位置，防止正在加载的时候退出时候存储的位置为上一章的页码
            mCurPage!!.position = 0
            mPageView.drawNextPage()
        }//如果当前章不存在，则表示在加载中
        mPageChangeListener?.onChapterChange(chapterPos)
        return true
    }

    //预加载下一章
    private fun preLoadNextChapter() {
        //判断是否存在下一章
        if (chapterPos + 1 >= mChapterList!!.size) {
            return
        }
        //判断下一章的文件是否存在
        val nextChapter = chapterPos + 1

        //如果之前正在加载则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp!!.dispose()
        }

        //调用异步进行预加载加载
        Observable.create(ObservableOnSubscribe<List<TxtPage>> { e -> e.onNext(loadPageList(nextChapter)!!) })
            .compose<List<TxtPage>> { RxUtils.toSimpleSingle(it) }
            .subscribe(object : Observer<List<TxtPage>> {
                override fun onSubscribe(d: Disposable) {
                    mPreLoadDisp = d
                }

                override fun onNext(pages: List<TxtPage>) {
                    mNextPageList = pages
                }


                override fun onError(e: Throwable) {
                    //无视错误
                }

                override fun onComplete() {

                }
            })

    }

    //取消翻页 (这个cancel有点歧义，指的是不需要看的页面)
    internal fun pageCancel() {
        //加载到下一章取消了
        if (mCurPage!!.position == 0 && chapterPos > mLastChapter) {
            prevChapter()
        } else if (mCurPageList == null || mCurPage!!.position == mCurPageList!!.size - 1 && chapterPos < mLastChapter) {
            nextChapter()
        }//加载上一章取消了 (可能有点小问题)
        //假设加载到下一页了，又取消了。那么需要重新装载的问题
        mCurPage = mCancelPage
    }

    /**
     * @return:获取初始显示的页面
     */
    private fun getCurPage(pos: Int): TxtPage? {
        if (mPageChangeListener != null) {
            mPageChangeListener!!.onPageChange(pos)
        }
        mCurPageList ?: return null
        if (pos in 0 until mCurPageList!!.size) {
            return mCurPageList?.get(pos)
        }
        return null
    }

    /**
     * 检测当前状态是否能够进行加载章节数据
     *
     * @return
     */
    private fun checkStatus(): Boolean {
        if (pageStatus == STATUS_LOADING) {
            ToastUtils.showShort("正在加载中，请稍等")
            return false
        } else if (pageStatus == STATUS_ERROR) {
            //点击重试
            pageStatus = STATUS_LOADING
            mPageView.drawCurPage(false)
            return false
        }
        //由于解析失败，让其退出
        return true
    }

    /*****************************************interface */

    interface OnPageChangeListener {
        fun onChapterChange(pos: Int)

        //请求加载回调
        fun onLoadChapter(chapters: List<TxtChapter>, pos: Int)

        //当目录加载完成的回调(必须要在创建的时候，就要存在了)
        fun onCategoryFinish(chapters: List<TxtChapter>)

        //页码改变
        fun onPageCountChange(count: Int)

        //页面改变
        fun onPageChange(pos: Int)
    }

    companion object {

        //当前页面的状态
        const val STATUS_LOADING = 1  //正在加载
        const val STATUS_FINISH = 2   //加载完成
        const val STATUS_ERROR = 3    //加载错误 (一般是网络加载情况)
        const val STATUS_EMPTY = 4    //空数据
        const val STATUS_PARSE = 5    //正在解析 (一般用于本地数据加载)
        const val STATUS_PARSE_ERROR = 6 //本地文件解析错误(暂未被使用)

        internal const val DEFAULT_MARGIN_HEIGHT = 28
        internal const val DEFAULT_MARGIN_WIDTH = 12

        //默认的显示参数配置
        private const val DEFAULT_TIP_SIZE = 12
        private const val EXTRA_TITLE_SIZE = 4


    }
}
