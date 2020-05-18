package com.page.view

import com.blankj.utilcode.util.ToastUtils
import com.page.view.data.BookChapterBean
import com.page.view.data.CollBookBean
import com.page.view.data.TxtChapter
import com.page.view.data.TxtPage
import com.page.view.utils.*
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.io.*
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

/**
 * 问题:
 * 1. 异常处理没有做好
 */
class LocalPageLoader(pageView: PageView) : PageLoader(pageView) {

    //章节解析模式
    private var mChapterPattern: Pattern? = null
    //获取书本的文件
    private var mBookFile: File? = null
    //编码类型
    private lateinit var mCharsetT: Charset

    private var mChapterDisp: Disposable? = null

    init {
        pageStatus = STATUS_PARSE
    }

    override fun openBook(collBook: CollBookBean) {
        super.openBook(collBook)
        mBookFile = File(collBook.bookId)
        //这里id表示本地文件的路径

        //判断是否文件存在
        if (!mBookFile!!.exists()) {
            return
        }
        //文件内容为空
        if (mBookFile!!.length() == 0L) {
            pageStatus = PageLoader.STATUS_EMPTY
            return
        }

        isBookOpen = false
        //通过RxJava异步处理分章事件

        Single.create<Boolean> {
            loadBook(mBookFile!!)
            it.onSuccess(true)
        }.compose<Boolean> { RxUtils.toSimpleSingle(it) }
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                    mChapterDisp = d
                }

                override fun onSuccess(value: Boolean) {
                    mChapterDisp = null
                    //提示目录加载完成
                    if (mPageChangeListener != null) {
                        mPageChangeListener!!.onCategoryFinish(mChapterList!!)
                    }
                    //打开章节，并加载当前章节
                    openChapter()
                }

                override fun onError(e: Throwable) {
                    //数据读取错误(弄个文章解析错误的Tip,会不会好一点)
                    pageStatus = PageLoader.STATUS_ERROR
                    ToastUtils.showShort("数据解析错误")
                }
            })
    }

    //采用的是随机读取
    private fun loadBook(bookFile: File) {
        //获取文件编码
        val charsetString = FileUtils.getFileCharsetSimple(bookFile.absolutePath)
        mCharsetT = if (charsetString != null) {
            Charset.forName(charsetString)
        } else {
            Charset.defaultCharset()
        }
        //查找章节，分配章节
        loadChapters()
    }

    /**
     * 未完成的部分:
     * 1. 序章的添加
     * 2. 章节存在的书本的虚拟分章效果
     *
     * @throws IOException
     */
    private fun loadChapters() {
        val chapters = ArrayList<TxtChapter>()
        //获取文件流
        val bookStream = RandomAccessFile(mBookFile, "r")
        //寻找匹配文章标题的正则表达式，判断是否存在章节名
        val hasChapter = checkChapterType(bookStream)
        //加载章节
        val buffer = ByteArray(BUFFER_SIZE)
        //获取到的块起始点，在文件中的位置
        var curOffset: Long = 0
        //block的个数
        var blockPos = 0
        //读取的长度
        var length: Int = bookStream.read(buffer, 0, buffer.size)

        //获取文件中的数据到buffer，直到没有数据为止
        while (length > 0) {
            ++blockPos
            //如果存在Chapter
            if (hasChapter) {
                //将数据转换成String

                val blockContent = String(buffer, 0, length, mCharsetT)
                //当前Block下使过的String的指针
                var seekPos = 0
                //进行正则匹配
                val matcher = mChapterPattern!!.matcher(blockContent)
                //如果存在相应章节
                while (matcher.find()) {
                    //获取匹配到的字符在字符串中的起始位置
                    val chapterStart = matcher.start()

                    //如果 seekPos == 0 && nextChapterPos != 0 表示当前block处前面有一段内容
                    //第一种情况一定是序章 第二种情况可能是上一个章节的内容
                    if (seekPos == 0 && chapterStart != 0) {
                        //获取当前章节的内容
                        val chapterContent = blockContent.substring(seekPos, chapterStart)
                        //设置指针偏移
                        seekPos += chapterContent.length

                        //如果当前对整个文件的偏移位置为0的话，那么就是序章
                        if (curOffset == 0L) {
                            //创建序章
                            val preChapter = TxtChapter()
                            preChapter.title = "序章"
                            preChapter.start = 0
                            preChapter.end = chapterContent.toByteArray(mCharsetT)
                                .size.toLong() //获取String的byte值,作为最终值

                            //如果序章大小大于30才添加进去
                            if (preChapter.end - preChapter.start > 30) {
                                chapters.add(preChapter)
                            }

                            //创建当前章节
                            val curChapter = TxtChapter()
                            curChapter.title = matcher.group()
                            curChapter.start = preChapter.end
                            chapters.add(curChapter)
                        } else {
                            //获取上一章节
                            val lastChapter = chapters[chapters.size - 1]
                            //将当前段落添加上一章去
                            lastChapter.end =
                                lastChapter.end + chapterContent.toByteArray(mCharsetT).size

                            //如果章节内容太小，则移除
                            if (lastChapter.end - lastChapter.start < 30) {
                                chapters.remove(lastChapter)
                            }

                            //创建当前章节
                            val curChapter = TxtChapter()
                            curChapter.title = matcher.group()
                            curChapter.start = lastChapter.end
                            chapters.add(curChapter)
                        }//否则就block分割之后，上一个章节的剩余内容
                    } else {
                        //是否存在章节
                        if (chapters.size != 0) {
                            //获取章节内容
                            val chapterContent = blockContent.substring(seekPos, matcher.start())
                            seekPos += chapterContent.length

                            //获取上一章节
                            val lastChapter = chapters[chapters.size - 1]
                            lastChapter.end =
                                lastChapter.start + chapterContent.toByteArray(mCharsetT).size

                            //如果章节内容太小，则移除
                            if (lastChapter.end - lastChapter.start < 30) {
                                chapters.remove(lastChapter)
                            }

                            //创建当前章节
                            val curChapter = TxtChapter()
                            curChapter.title = matcher.group()
                            curChapter.start = lastChapter.end
                            chapters.add(curChapter)
                        } else {
                            val curChapter = TxtChapter()
                            curChapter.title = matcher.group()
                            curChapter.start = 0
                            chapters.add(curChapter)
                        }//如果章节不存在则创建章节
                    }
                }
            } else {
                //章节在buffer的偏移量
                var chapterOffset = 0
                //当前剩余可分配的长度
                var strLength = length
                //分章的位置
                var chapterPos = 0

                while (strLength > 0) {
                    ++chapterPos
                    //是否长度超过一章
                    if (strLength > MAX_LENGTH_WITH_NO_CHAPTER) {
                        //在buffer中一章的终止点
                        var end = length
                        //寻找换行符作为终止点
                        for (i in chapterOffset + MAX_LENGTH_WITH_NO_CHAPTER until length) {
                            //换行符
                            if (buffer[i] == 0x0A.toByte()) {
                                end = i
                                break
                            }
                        }
                        val chapter = TxtChapter()
                        chapter.title = "第" + blockPos + "章" + "(" + chapterPos + ")"
                        chapter.start = curOffset + chapterOffset.toLong() + 1
                        chapter.end = curOffset + end
                        chapters.add(chapter)
                        //减去已经被分配的长度
                        strLength -= (end - chapterOffset)
                        //设置偏移的位置
                        chapterOffset = end
                    } else {
                        val chapter = TxtChapter()
                        chapter.title = "第" + blockPos + "章" + "(" + chapterPos + ")"
                        chapter.start = curOffset + chapterOffset.toLong() + 1
                        chapter.end = curOffset + length
                        chapters.add(chapter)
                        strLength = 0
                    }
                }
            }//进行本地虚拟分章

            //block的偏移点
            curOffset += length.toLong()

            if (hasChapter) {
                //设置上一章的结尾
                val lastChapter = chapters[chapters.size - 1]
                lastChapter.end = curOffset
            }

            //当添加的block太多的时候，执行GC
            if (blockPos % 15 == 0) {
                System.gc()
                System.runFinalization()
            }
            length = bookStream.read(buffer, 0, buffer.size)
        }

        mChapterList = chapters
        IOUtils.close(bookStream)

        System.gc()
        System.runFinalization()
    }

    override fun loadPageList(chapter: Int): List<TxtPage> {
        if (mChapterList == null) {
            throw IllegalArgumentException("Chapter list must not null")
        }

        val txtChapter = mChapterList!![chapter]
        //从文件中获取数据
        val content = getChapterContent(txtChapter)
        val br = BufferedReader(InputStreamReader(ByteArrayInputStream(content), mCharsetT))
        return loadPages(chapter,txtChapter, br)
    }

    /**
     * 从文件中提取一章的内容
     *
     * @param chapter
     * @return
     */
    private fun getChapterContent(chapter: TxtChapter): ByteArray {
        var bookStream: RandomAccessFile? = null
        try {
            bookStream = RandomAccessFile(mBookFile, "r")
            bookStream.seek(chapter.start)
            val extent = (chapter.end - chapter.start).toInt()
            val content = ByteArray(extent)
            bookStream.read(content, 0, extent)
            return content
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            IOUtils.close(bookStream)
        }

        return ByteArray(0)
    }

    /**
     * 1. 检查文件中是否存在章节名
     * 2. 判断文件中使用的章节名类型的正则表达式
     *
     * @return 是否存在章节名
     */
    @Throws(IOException::class)
    private fun checkChapterType(bookStream: RandomAccessFile): Boolean {
        //首先获取128k的数据
        val buffer = ByteArray(BUFFER_SIZE / 4)
        val length = bookStream.read(buffer, 0, buffer.size)
        //进行章节匹配
        for (str in CHAPTER_PATTERNS) {
            val pattern = Pattern.compile(str, Pattern.MULTILINE)
            val matcher = pattern.matcher(
                String(buffer, 0, length, mCharsetT)
            )
            //如果匹配存在，那么就表示当前章节使用这种匹配方式
            if (matcher.find()) {
                mChapterPattern = pattern
                //重置指针位置
                bookStream.seek(0)
                return true
            }
        }

        //重置指针位置
        bookStream.seek(0)
        return false
    }

    override fun prevChapter(): Boolean {
        return if (pageStatus == STATUS_PARSE_ERROR) {
            false
        } else super.prevChapter()
    }

    override fun nextChapter(): Boolean {
        return if (pageStatus == STATUS_PARSE_ERROR) {
            false
        } else super.nextChapter()
    }

    override fun skipToChapter(pos: Int) {
        super.skipToChapter(pos)
        //加载章节
        openChapter()
    }

    override fun setOnPageChangeListener(listener: OnPageChangeListener) {
        super.setOnPageChangeListener(listener)
        //额，写的不太优雅，之后再改
        if (mChapterList != null && mChapterList!!.isNotEmpty()) {
            mPageChangeListener!!.onCategoryFinish(mChapterList!!)
        }
    }

    /*空实现*/
    override fun setChapterList(bookChapters: List<BookChapterBean>) {}

    override fun saveRecord() {
        super.saveRecord()
        //修改当前COllBook记录
        if (isBookOpen) {
            //表示当前CollBook已经阅读
            mCollBook.isUpdate = false
            mCollBook.lastChapter = mChapterList!![chapterPos].title
            mCollBook.lastRead = StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE)
            //fixme db 直接更新
            //CollBookHelper.getsInstance().saveBook(mCollBook);
        }
    }

    override fun closeBook() {
        super.closeBook()
        if (mChapterDisp != null) {
            mChapterDisp!!.dispose()
            mChapterDisp = null
        }
    }

    companion object {
        //默认从文件中获取数据的长度
        private const val BUFFER_SIZE = 512 * 1024
        //没有标题的时候，每个章节的最大长度
        private const val MAX_LENGTH_WITH_NO_CHAPTER = 10 * 1024


        //正则表达式章节匹配模式
        // "(第)([0-9零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,10})([章节回集卷])(.*)"
        //fixme 章节匹配
        private val CHAPTER_PATTERNS = arrayOf(
            "^(.{0,8})(\u7b2c)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\u7ae0\u8282\u56de\u96c6\u5377])(.{0,30})$",
            "^(\\s{0,4})([\\(\u3010\u300a]?(\u5377)?)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\\.:\uff1a\u0020\\f\t])(.{0,30})$",
            "^(\\s{0,4})([\\(\uff08\u3010\u300a])(.{0,30})([\\)\uff09\u3011\u300b])(\\s{0,2})$",
            "^(\\s{0,4})(\u6b63\u6587)(.{0,20})$",
            "^(.{0,4})(Chapter|chapter)(\\s{0,4})([0-9]{1,4})(.{0,30})$"
        )
    }
}
