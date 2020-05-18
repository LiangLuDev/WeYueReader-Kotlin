package com.aku.weyue.ui.main

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.aku.aac.core.BaseVMFragment
import com.aku.weyue.R
import com.aku.weyue.data.MainMenuBean
import com.aku.weyue.data.UserBean
import com.aku.weyue.databinding.MainFrag1Binding
import com.aku.weyue.ui.MainActivity
import com.aku.weyue.ui.MainMenuAdapter
import com.aku.weyue.ui.UserViewModel
import com.aku.weyue.ui.shelf.BookShelfFragment
import com.aku.weyue.util.rxPermission
import com.aku.weyue.widget.ResideLayout
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.SnackbarUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.main_frag_1.*

/**
 * @author Zsc
 * @date   2019/4/28
 * @desc
 */
class MainFragment : BaseVMFragment<MainFrag1Binding>(),
    BaseOpen {


    override val layout = R.layout.main_frag_1

    private val userModel: UserViewModel by activityViewModels()

    private val mTypeFragment by lazy { TypeFragment() }
    private val mShelFragment by lazy { BookShelfFragment() }

    private val mAdapter by lazy { MainMenuAdapter(getMenuData()) }
    private var curItem = 0

    override fun initData(savedInstanceState: Bundle?) {
        //解决切换主题后resideLayout左侧颜色和当前控件临时丢失
        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.setNavigationOnClickListener {
            binding.resideLayout.openPane()
        }
        requireActivity().onBackPressedDispatcher
            .addCallback(this, true) {
                if (resideLayout.isOpen) {
                    resideLayout.closePane()
                } else {
                    requireActivity().finish()
                }
            }
        binding.userModel = userModel
        rv_menu.layoutManager = LinearLayoutManager(requireContext())
        rv_menu.adapter = mAdapter
        iv_avatar.setOnClickListener {
            if (userModel.user.value == null) {
                findNavController().navigate(R.id.login_fragment)
            } else {
                findNavController().navigate(R.id.userInfoFragment)
            }
        }
        userModel.user.observeForever {
            setLoginText(it)
        }
        resideLayout.setPanelSlideListener(object : ResideLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
            }

            override fun onPanelOpened(panel: View?) {
                SnackbarUtils.dismiss()
            }

            override fun onPanelClosed(panel: View?) {
            }

        })
        changeFragment(0, mTypeFragment)
        mAdapter.setOnItemClickListener { _, _, position ->
            resideLayout.closePane()
            when (position) {
                0 -> changeFragment(0, mTypeFragment)
                1 -> changeFragment(1, mShelFragment)
                2 -> {
                    rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe {
                            if (it) {
                                findNavController().navigate(R.id.scanBookFragment)
                            } else {
                                ToastUtils.showShort("请打开文件权限")
                            }
                        }
                }
                3 -> findNavController().navigate(R.id.feedbackFragment)
                4 -> findNavController().navigate(R.id.aboutAuthorFragment)
            }
        }

        tvTheme.setOnClickListener {
            ColorChooserDialog.Builder(requireActivity() as MainActivity, R.string.theme)
                .customColors(R.array.colors, null)
                .doneButton(R.string.done)
                .cancelButton(R.string.cancel)
                .allowUserColorInput(false)
                .allowUserColorInputAlpha(false)
                .show()
        }
        tvSetting.setOnClickListener {
            resideLayout.closePane()
            if (userModel.user.value == null) {
                findNavController().navigate(R.id.login_fragment)
            } else {
                findNavController().navigate(R.id.settingFragment)
            }
        }
    }

    /**
     * 防止侧边栏打开时，跳转，再返回时侧边变为空白
     */
    override fun onStart() {
        super.onStart()
        if (resideLayout.isOpen) {
            resideLayout.post {
                menu.isVisible = true
            }
        }
    }

    private fun setLoginText(user: UserBean?) {
        if (user == null) {
            tvSetting.text = "登录"
            tv_desc.text = "登录"
        } else {
            tvSetting.text = "设置"
            tv_desc.text = user.brief
        }
    }

    private fun getMenuData(): List<MainMenuBean> {
        val list = mutableListOf<MainMenuBean>()
        val menuName = resources.getStringArray(R.array.main_menu_name)
        val menuIcon = resources.obtainTypedArray(R.array.main_menu_icon)

        for (i in menuName.indices) {
            val menuBean = MainMenuBean()
            menuBean.name = menuName[i]
            menuBean.icon = menuIcon.getResourceId(i, 0)
            list.add(menuBean)
        }
        return list
    }

    private fun changeFragment(position: Int, fragment: Fragment) {
        if (fragment.isAdded && fragment.isVisible) {
            return
        }
        curItem = position
        changeTitle(position)
        FragmentUtils.hide(childFragmentManager)
        if (fragment.isAdded) {
            FragmentUtils.show(fragment)
        } else {
            FragmentUtils.add(childFragmentManager, fragment, R.id.flTypeFrag)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeTitle(curItem)
    }

    private fun changeTitle(position: Int) {
        val item = mAdapter.getItem(position)!!
        binding.toolbar.setNavigationIcon(item.icon)
        binding.toolbar.title = item.name
    }

    override fun canOpen(can: Boolean) {
        binding.resideLayout.setCanLeftSlide(can)
    }

}


/*override fun finish() {
    val nowTime = System.currentTimeMillis()
    if (nowTime - lastBackTime > 2000) {
        lastBackTime = nowTime
        ToastUtils.showShort("再次点击退出")
        return
    }
    super.finish()
}


override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
    val colorArray = resources.getIntArray(R.array.colors)
    application.setTheme(ThemeUtils.getSelectTheme(colorArray, selectedColor))
    recreate()
}*/
