package com.aku.weyue.util

import com.aku.weyue.R
import com.aku.weyue.data.source.SpSource

/**
 * @author Zsc
 * @date   2019/5/15
 * @desc
 */
object ThemeUtils {

    private val themeArray by lazy {
        intArrayOf(
            R.style.BlueTheme,
            R.style.RedTheme,
            R.style.BrownTheme,
            R.style.GreenTheme,
            R.style.PurpleTheme,
            R.style.TealTheme,
            R.style.PinkTheme,
            R.style.DeepPurpleTheme,
            R.style.OrangeTheme,
            R.style.IndigoTheme,
            R.style.CyanTheme,
            R.style.LightGreenTheme,
            R.style.LimeTheme,
            R.style.DeepOrangeTheme,
            R.style.BlueGreyTheme
        )
    }



    /**
     * 通过颜色获取主题
     */
    fun getSelectTheme(
        colorArray: IntArray,
        selectColor: Int
    ): Int {

        val cur = colorArray.indexOf(selectColor)
        if (cur in 0 until themeArray.size) {
            SpSource.appTheme = cur
            return themeArray[cur]
        }
        SpSource.appTheme = 0
        return themeArray[0]
    }

    /**
     * 获取当前选中的主题
     */
    fun getSelectTheme(
        options: Int
    ): Int {
        if (options in 0 until themeArray.size) {
            return themeArray[options]
        }
        return themeArray[0]
    }

    fun getSelectPrimaryColor(
        options: Int
    ):Int{

        if (options in 0 until themeArray.size) {
            return themeArray[options]
        }
        return themeArray[0]


    }


}