package com.aku.weyue.ui.feedback

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.aku.aac.android.isEmpty
import com.aku.aac.core.BaseFragment
import com.aku.aac.kchttp.ext.doError
import com.aku.aac.kchttp.ext.doSuccess
import com.aku.weyue.R
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.feed_back_frag.*
import kotlinx.android.synthetic.main.main_frag_1.*

/**
 * @author Zsc
 * @date   2019/6/1
 * @desc
 */
class FeedbackFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.feed_back_frag

    private val feedbackViewModel: FeedbackViewModel by viewModels()

    override fun initData(savedInstanceState: Bundle?) {
        toolbar.setupWithNavController(findNavController())
        btn_commit.setOnClickListener {
            when {
                et_qq.isEmpty -> ToastUtils.showShort("请输入QQ号")
                et_feedback.isEmpty -> ToastUtils.showShort("请输入反馈信息")
                else -> feedbackViewModel.feedBack()
            }
        }

        feedbackViewModel.resultApi.observe(this
            , Observer {
                LogUtils.d("resultApi")
                it.doSuccess {
                    findNavController().navigateUp()
                }.doError { error ->
                    ToastUtils.showShort(error.msg)
                }
            })

    }
}