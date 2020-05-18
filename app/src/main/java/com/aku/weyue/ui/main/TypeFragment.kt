package com.aku.weyue.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.aku.aac.core.BaseFragment
import com.aku.weyue.R
import com.aku.weyue.ui.type.BaseTypeFragment
import kotlinx.android.synthetic.main.main_frag.*

/**
 * @author Zsc
 * @date   2019/5/29
 * @desc
 */
class TypeFragment : BaseFragment() {

    override val layout = R.layout.main_frag

    private val fragment0 by lazy {
        lazyOf(BaseTypeFragment.create(BaseTypeFragment.BOY_TYPE))
    }
    private val fragment1 by lazy {
        lazyOf(BaseTypeFragment.create(BaseTypeFragment.GIRL_TYPE))
    }
    private val fragment2 by lazy {
        lazyOf(BaseTypeFragment.create(BaseTypeFragment.PUBLISH_TYPE))
    }
    //用Lazy实现Fragment懒加载
    private val mLazyFragments = arrayOf(fragment0, fragment1, fragment2)
    private val titles = arrayOf("男生", "女生", "出版")
    private var curPage = 0

    override fun initData(savedInstanceState: Bundle?) {
        ntsMain.setTitles(*titles)
        vpMain.adapter = mPageAdapter
        ntsMain.setViewPager(vpMain)
        vpMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                curPage = position
                (parentFragment as? BaseOpen)?.canOpen(position == 0)
            }
        })
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            (parentFragment as? BaseOpen)?.canOpen(true)
        } else {
            (parentFragment as? BaseOpen)?.canOpen(curPage == 0)
        }
    }

    private val mPageAdapter by lazy {
        object : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                return mLazyFragments[position].value
            }

            override fun getCount(): Int {
                return mLazyFragments.size
            }
        }
    }
}