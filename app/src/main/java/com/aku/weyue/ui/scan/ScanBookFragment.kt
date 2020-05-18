package com.aku.weyue.ui.scan

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager.widget.ViewPager
import com.aku.aac.core.BaseFragment
import com.aku.weyue.R
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.scan_book_frag.*

/**
 * @author Zsc
 * @date   2019/6/2
 * @desc
 */
class ScanBookFragment : BaseFragment(), SelectChange {


    override val layout: Int
        get() = R.layout.scan_book_frag

    private val fragment0 = lazyOf(LocalBookFragment())
    private val fragment1 = lazyOf(FileLayerFragment())

    private val mLazyFragments by lazy {
        arrayOf<Lazy<Fragment>>(fragment0, fragment1)
    }

    @SuppressLint("SetTextI18n")
    override fun changeSelect(all: Boolean, count: Int) {
        file_system_cb_selected_all.text =
            if (all) "取消" else "全选"
        if (count == 0) {
            file_system_btn_add_book.text = "加入书架"
        } else {
            file_system_btn_add_book.text = "加入书架($count)"
        }
    }

    private val titles = arrayOf("智能导入", "手机目录")

    override fun initData(savedInstanceState: Bundle?) {
        toolbar.setupWithNavController(findNavController())
        nts_scan.setTitles(*titles)
        vp_scan.adapter = mPageAdapter
        nts_scan.setViewPager(vp_scan)

        vp_scan.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> fragment0.value.refreshSelect()
                    1 -> fragment1.value.refreshSelect()
                }
            }

        })


        file_system_btn_add_book.setOnClickListener {
            when (vp_scan.currentItem) {
                0 -> fragment0.value.addShelf()
                1 -> fragment1.value.addShelf()
            }
        }
        file_system_btn_delete.setOnClickListener {
            when (vp_scan.currentItem) {
                0 -> fragment0.value.delete()
                1 -> fragment1.value.delete()
            }
        }
        file_system_cb_selected_all.setOnCheckedChangeListener { _, isChecked ->
            when (vp_scan.currentItem) {
                0 -> fragment0.value.changeAll(isChecked)
                1 -> fragment1.value.changeAll(isChecked)
            }
        }
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