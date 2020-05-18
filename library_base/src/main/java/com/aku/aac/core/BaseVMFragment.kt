package com.aku.aac.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * @author Zsc
 * @date   2019/5/2
 * @desc
 */
abstract class BaseVMFragment<T : ViewDataBinding> : BaseFragment() {

    protected lateinit var binding: T

    override fun initRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(
            inflater,
            layout,
            container,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }


}