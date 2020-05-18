package com.aku.weyue.ui.setting

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.aku.aac.core.BaseFragment
import com.aku.weyue.BuildConfig
import com.aku.weyue.R
import com.aku.weyue.data.source.SpSource
import com.aku.weyue.ui.UserViewModel
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.setting_frag.*

/**
 * @author Zsc
 * @date   2019/6/1
 * @desc
 */
class SettingFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.setting_frag

    private val userViewModel: UserViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        toolbar.setupWithNavController(findNavController())
        tv_version.text = "版本号：${BuildConfig.VERSION_NAME}"
        btn_out.setOnClickListener {
            SpSource.user = null
            userViewModel.user.postValue(null)
            findNavController().popBackStack()
        }
    }
}