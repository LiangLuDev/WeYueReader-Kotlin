package com.aku.common.koin

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModel

/**
 * @author Zsc
 * @date   2019/6/8
 * @desc  Fragment 注入的拓展
 */


/**
 * 在子[Fragment]中注入[ViewModel]时使用
 * @desc 如果直接使用[androidx.fragment.app.activityViewModels]会在
 * [Fragment.getParentFragment]销毁时[ViewModel]不销毁
 */
@MainThread
inline fun <reified VM : ViewModel> Fragment.parentViewModels(
    noinline factoryProducer: (() -> Factory)? = null
) = createViewModelLazy(
    VM::class, {
        parentFragment?:throw IllegalStateException(
            "parentViewModels can be accessed only when Fragment has parentFragment"
        )
        parentFragment!!.viewModelStore
    },
    factoryProducer
)
