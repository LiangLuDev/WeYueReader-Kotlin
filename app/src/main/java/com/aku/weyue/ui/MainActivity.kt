package com.aku.weyue.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.aku.common.util.AttrUtils
import com.aku.weyue.R
import com.aku.weyue.util.ThemeUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SnackbarUtils

class MainActivity : AppCompatActivity(), ColorChooserDialog.ColorCallback {

    private var lastBackTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findNavController(R.id.main_nav)
            .addOnDestinationChangedListener { _, _, _ ->
                KeyboardUtils.hideSoftInput(this)
            }
    }

    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
        val colorArray = resources.getIntArray(R.array.colors)
        application.setTheme(ThemeUtils.getSelectTheme(colorArray, selectedColor))
        recreate()
    }

    override fun finish() {
        val curTime = System.currentTimeMillis()
        if (curTime - lastBackTime > 2000) {
            lastBackTime = curTime
            SnackbarUtils.with(window.decorView)
                .setMessage("再次返回退出")
                .setBgResource(AttrUtils.getAttrResourceId(R.attr.colorPrimary))
                .show()
        } else {
            super.finish()
        }
    }

}