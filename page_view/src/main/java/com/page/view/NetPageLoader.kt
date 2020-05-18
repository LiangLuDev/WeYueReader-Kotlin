package com.page.view

import com.page.view.data.BookChapterBean
import com.page.view.data.CollBookBean
import com.page.view.data.TxtChapter
import com.page.view.data.TxtPage
import com.page.view.utils.Constant
import com.page.view.utils.StringUtils

import java.io.*
import java.util.ArrayList

/**
 * 网络页面加载器
 */
class NetPageLoader(pageView: PageView) : PageLoader(pageView) {

    //初始化书籍
    override fun openBook(collBook: CollBookBean) {
        super.openBook(collBook)
        isBookOpen = false
        if (collBook.bookChapters == null) {
            return
        }
        mChapterList = convertTxtChapter(collBook.bookChapters!!)
        //设置目录回调
        if (mPageChangeListener != null) {
            mPageChangeListener!!.onCategoryFinish(mChapterList!!)
        }
        //提示加载下面的章节
        loadCurrentChapter()
    }

    private fun convertTxtChapter(bookChapters: List<BookChapterBean>): List<TxtChapter> {
        return bookChapters.map {
            TxtChapter().apply {
                bookId = it.bookId
                title = it.title
                link = it.link
            }
        }
    }

    override fun loadPageList(chapter: Int): List<TxtPage> {
        if (mChapterList == null) {
            throw IllegalArgumentException("chapter list must not null")
        }

        //获取要加载的文件
        val txtChapter = mChapterList!![chapter]
        val file = File(
            Constant.BOOK_CACHE_PATH
                    + mCollBook.bookId + File.separator
                    + mChapterList!![chapter].title + Constant.SUFFIX_WY
        )
        if (!file.exists()) {
            return listOf()
        }

        var reader: Reader? = null
        try {
            reader = FileReader(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        val br = BufferedReader(reader!!)

        return loadPages(chapter,txtChapter, br)
    }

    //装载上一章节的内容
    override fun prevChapter(): Boolean {

        val hasPrev = super.prevChapter()
        if (!hasPrev) {
            return false
        }

        if (pageStatus == STATUS_FINISH) {
            loadCurrentChapter()
            return true
        } else if (pageStatus == STATUS_LOADING) {
            loadCurrentChapter()
            return false
        }
        return false
    }

    //装载下一章节的内容
    override fun nextChapter(): Boolean {
        val hasNext = super.nextChapter()
        if (!hasNext) return false

        if (pageStatus == STATUS_FINISH) {
            loadNextChapter()
            return true
        } else if (pageStatus == STATUS_LOADING) {
            loadCurrentChapter()
            return false
        }
        return false
    }

    //跳转到指定章节
    override fun skipToChapter(pos: Int) {
        super.skipToChapter(pos)

        //提示章节改变，需要下载
        loadCurrentChapter()
    }

    private fun loadPrevChapter() {
        //提示加载上一章
        if (mPageChangeListener != null) {
            //提示加载前面3个章节（不包括当前章节）
            val current = chapterPos
            var prev = current - 3
            if (prev < 0) {
                prev = 0
            }
            mPageChangeListener!!.onLoadChapter(mChapterList!!.subList(prev, current), chapterPos)
        }
    }

    private fun loadCurrentChapter() {
        if (mPageChangeListener != null) {
            val bookChapters = ArrayList<TxtChapter>(5)
            //提示加载当前章节和前面两章和后面两章
            val current = chapterPos
            bookChapters.add(mChapterList!![current])

            //如果当前已经是最后一章，那么就没有必要加载后面章节
            if (current != mChapterList!!.size) {
                val begin = current + 1
                var next = begin + 2
                if (next > mChapterList!!.size) {
                    next = mChapterList!!.size
                }
                bookChapters.addAll(mChapterList!!.subList(begin, next))
            }

            //如果当前已经是第一章，那么就没有必要加载前面章节
            if (current != 0) {
                var prev = current - 2
                if (prev < 0) {
                    prev = 0
                }
                bookChapters.addAll(mChapterList!!.subList(prev, current))
            }
            mPageChangeListener!!.onLoadChapter(bookChapters, chapterPos)
        }
    }

    private fun loadNextChapter() {
        //提示加载下一章
        if (mPageChangeListener != null) {
            //提示加载当前章节和后面3个章节
            val current = chapterPos + 1
            var next = chapterPos + 3
            if (next > mChapterList!!.size) {
                next = mChapterList!!.size
            }
            mPageChangeListener!!.onLoadChapter(mChapterList!!.subList(current, next), chapterPos)
        }
    }

    override fun setChapterList(bookChapters: List<BookChapterBean>) {

        mChapterList = convertTxtChapter(bookChapters)

        if (mPageChangeListener != null) {
            mPageChangeListener!!.onCategoryFinish(mChapterList!!)
        }
    }

    override fun saveRecord() {
        super.saveRecord()
        if (isBookOpen) {
            //表示当前CollBook已经阅读
            mCollBook.isUpdate = false
            mCollBook.lastRead = StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE)
            //直接更新 TODO book db

            //            CollBookHelper.getsInstance().saveBook(mCollBook);
            //            BookRepository.getInstance()
            ////                    .saveCollBook(mCollBook);
        }
    }
}

