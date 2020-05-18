package com.aku.weyue.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.aku.aac.core.BaseFragment
import com.aku.weyue.R
import kotlinx.android.synthetic.main.book_list_type_frag.*
import kotlinx.android.synthetic.main.include_toolbar.*

/**
 * @author Zsc
 * @date   2019/5/3
 * @desc
 */
class BookListTypeFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.book_list_type_frag

    private val args: BookListTypeFragmentArgs by navArgs()

    private val fragment0 by lazy {
        lazyOf(BookListFragment.create(args.bookTitle, BookListFragment.HOT))
    }
    private val fragment1 by lazy {
        lazyOf(BookListFragment.create(args.bookTitle, BookListFragment.NEW))
    }
    private val fragment2 by lazy {
        lazyOf(BookListFragment.create(args.bookTitle, BookListFragment.GOOD))
    }
    //用Lazy实现Fragment懒加载,这里必须加by lazy 不然会找不到args导致奔溃
    private val mLazyFragments by lazy {
        arrayOf(fragment0, fragment1, fragment2)
    }
    private val titles = arrayOf("热门", "新书", "好评")

    override fun initData(savedInstanceState: Bundle?) {
        toolbar.setupWithNavController(findNavController())
        ntsMain.setTitles(*titles)
        vpMain.adapter = mPageAdapter
        ntsMain.setViewPager(vpMain)

    }

    private val mPageAdapter by lazy {
        object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return mLazyFragments[position].value
            }

            override fun getCount(): Int {
                return mLazyFragments.size
            }

        }
    }
}