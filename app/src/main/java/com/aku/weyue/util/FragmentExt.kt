package com.aku.weyue.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

/**
 * @author Zsc
 * @date   2019/5/4
 * @desc
 */
/**
 * 设置toolbar的title
 * 暂时找不到更优雅的方式
 */
fun Fragment.setNewLabel(label: String) {
    //调用时生效，返回后不生效
    (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = label
    //调用时不生效，后续生效！！
    findNavController().currentDestination?.label = label
}
