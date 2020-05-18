package com.aku.weyue.ui.splash

import android.os.Bundle
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.aku.aac.core.BaseFragment
import com.aku.weyue.R

/**
 * @author Zsc
 * @date   2019/5/27
 * @desc
 */
class SplashFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.splash_frag

    override fun initData(savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher
            .addCallback(this, true) {
                //什么都不做，拦截返回键，使返回键无效
            }
    }

    /**
     * 解决引导未完成时切出app，导致一直停留在引导界面的bug
     */
    override fun onStart() {
        super.onStart()
        view?.postDelayed({
            findNavController().popBackStack()
            findNavController().navigate(R.id.main_fragment)
        }, 500)
    }

}