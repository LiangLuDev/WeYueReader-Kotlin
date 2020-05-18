package com.aku.aac.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * @author Zsc
 * @date   2019/5/2
 * @desc
 */
abstract class BaseFragment : Fragment() {

    protected var rootView: View? = null

    @get:LayoutRes
    abstract val layout: Int

    /**
     * 初始化数据
     */
    abstract fun initData(savedInstanceState: Bundle?)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView == null) {
            rootView = initRootView(inflater, container, savedInstanceState)
            initHasMenu()
            //初始化数据
            initData(savedInstanceState)
        }
        return rootView
    }

    /**
     * 设置可以有menu
     */
    open fun initHasMenu() {
        setHasOptionsMenu(true)
    }

    /**
     * 初始化rootView
     */
    open fun initRootView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(layout, container, false)
    }

    /**
     * 重写getView方法，防止Kotlin在initData(){}中直接用id获取不到view
     */
    override fun getView(): View? {
        return rootView
    }


}