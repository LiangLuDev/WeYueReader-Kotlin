package com.dct.aac.toolbarsample

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.aku.aac.core.BaseFragment
import kotlinx.android.synthetic.main.first_frag.*

/**
 * @author Zsc
 * @date   2019/5/29
 * @desc
 */
class ThirdFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.first_frag

    override fun initData(savedInstanceState: Bundle?) {
        toolBar.setupWithNavController(findNavController())
        tvContent.text = "ThirdFragment"
    }

}