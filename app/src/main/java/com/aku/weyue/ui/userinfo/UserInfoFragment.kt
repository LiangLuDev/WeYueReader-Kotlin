package com.aku.weyue.ui.userinfo

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.aku.aac.android.isEmpty
import com.aku.aac.android.string
import com.aku.aac.core.BaseVMFragment
import com.aku.aac.kchttp.ext.doCancel
import com.aku.aac.kchttp.ext.doError
import com.aku.aac.kchttp.ext.doSuccess
import com.aku.weyue.BookTagArgs
import com.aku.weyue.R
import com.aku.weyue.api.BookApi
import com.aku.weyue.data.UserDetail
import com.aku.weyue.data.source.SpSource
import com.aku.weyue.databinding.UserInfoFragBinding
import com.aku.weyue.ui.UserViewModel
import com.aku.weyue.ui.book.BookDetailFragmentArgs
import com.aku.weyue.ui.book.BookTagDialogFragment
import com.aku.weyue.util.bindDialog
import com.aku.weyue.util.rxPermission
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jph.takephoto.app.TakePhoto
import com.jph.takephoto.app.TakePhotoImpl
import com.jph.takephoto.compress.CompressConfig
import com.jph.takephoto.model.CropOptions
import com.jph.takephoto.model.InvokeParam
import com.jph.takephoto.model.TContextWrap
import com.jph.takephoto.model.TResult
import com.jph.takephoto.permission.InvokeListener
import com.jph.takephoto.permission.PermissionManager
import com.jph.takephoto.permission.TakePhotoInvocationHandler
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.user_info_frag.*
import java.io.File

/**
 * @author Zsc
 * @date   2019/6/5
 * @desc
 */
class UserInfoFragment : BaseVMFragment<UserInfoFragBinding>(),
    InvokeListener, TakePhoto.TakeResultListener,
    DialogInterface.OnDismissListener {

    override val layout: Int
        get() = R.layout.user_info_frag
    private val userInfoViewModel: UserInfoViewModel by viewModels()

    private val userViewModel: UserViewModel by activityViewModels()

    private val cropOptions by lazy {
        CropOptions.Builder()
            .setAspectX(1)
            .setAspectY(1)
            .setWithOwnCrop(true)
            .create()!!
    }
    private val compressConfig by lazy {
        CompressConfig.Builder().setMaxSize(50 * 1024).setMaxPixel(800).create()!!
    }
    private val takePhoto by lazy {
        (TakePhotoInvocationHandler.of(this)
            .bind(TakePhotoImpl(this, this))
                as TakePhoto).apply {
            onEnableCompress(compressConfig, false)
        }
    }
    private var invokeParam: InvokeParam? = null

    override fun initData(savedInstanceState: Bundle?) {
        toolbar.setupWithNavController(findNavController())
        binding.vm = userInfoViewModel
        binding.fabEditUserinfo.setOnClickListener {
            binding.fabMenu.toggle()
            userInfoViewModel.isEdit.value = true
        }
        binding.fabEditPassword.setOnClickListener {
            binding.fabMenu.toggle()
            MaterialDialog.Builder(requireContext())
                .title("修改用户密码")
                .inputRange(2, 20)
//                        .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input("请输入新密码", null) { d, s ->
                    d.dismiss()
                    userInfoViewModel.updatePassword(s.toString())
                        .bindDialog()
                }.show()
        }
        userInfoViewModel.loadUserDetail().bindDialog()
        userInfoViewModel.userPassword.observe(this, Observer { r ->
            r.doSuccess {
                ToastUtils.showShort("修改密码成功")
            }.doError {
                ToastUtils.showShort(it.msg)
            }.doCancel {
                ToastUtils.showShort("修改密码取消")
            }
        })

        binding.ivAvatar.setOnClickListener {
            val items = arrayOf("相册", "拍摄")
            MaterialDialog.Builder(requireActivity())
                .title("选择照片方式")
                .items(*items)
                .itemsCallback { dialog, _, position, _ ->
                    dialog.dismiss()
                    when (position) {
                        //从相册中选取图片并裁剪
                        0 -> rxPermission.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe {
                                if (it) {
                                    takePhoto.onPickFromGalleryWithCrop(getImageCropUri(), cropOptions)
                                } else {
                                    ToastUtils.showShort("请同意文件读取权限")
                                }
                            }
                        //拍照并裁剪
                        1 -> rxPermission.request(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        ).subscribe {
                            if (it) {
                                takePhoto.onPickFromCaptureWithCrop(getImageCropUri(), cropOptions)
                            } else {
                                ToastUtils.showShort("请同意文件和拍照权限")
                            }
                        }
                    }
                }
                .show()
        }
        userInfoViewModel.userIcon.observe(this, Observer {
            it.doSuccess {
                loadUserIcon(BookApi.BASE_URL + userViewModel.user.value?.icon)
                userViewModel.user.postValue(userViewModel.user.value)
            }.doError { error ->
                ToastUtils.showShort(error.msg)
            }
        })
        loadUserIcon(BookApi.BASE_URL + SpSource.user?.icon)
        userInfoViewModel.userEdit.observe(this, Observer {
            initTag(it)
            userInfoViewModel.isEdit.value = false
            SpSource.user = it.getUser()
            userViewModel.user.postValue(it)
        })
        binding.btnConfirm.setOnClickListener {
            when {
                binding.etNickname.isEmpty -> ToastUtils.showShort("昵称不能为空")
                binding.etBrief.isEmpty -> ToastUtils.showShort("我的格言不能为空")
                else -> userInfoViewModel.updateUserInfo(
                    binding.etNickname.string,
                    binding.etBrief.string
                ).bindDialog()
            }
        }
    }

    private fun initTag(userDetail: UserDetail) {
        val likeBooks = userDetail.likebooks
        if (likeBooks.isEmpty()) {
            return
        }
        val titles = likeBooks.map {
            it.title ?: ""
        }
        val bookTags = mutableListOf<String>()
        likeBooks.forEach {
            it.tags?.run {
                bookTags.addAll(this)
            }
        }
        //喜欢的书籍
        binding.flBookName.adapter = object : TagAdapter<String>(titles) {
            override fun getView(parent: FlowLayout, position: Int, s: String): View {
                val tv = LayoutInflater.from(requireContext())
                    .inflate(
                        R.layout.tags_tv,
                        binding.flBookName, false
                    ) as TextView
                tv.text = s
                return tv
            }
        }
        binding.flBookName.setOnTagClickListener { _, position, _ ->
            findNavController()
                .navigate(
                    R.id.book_detail,
                    BookDetailFragmentArgs(likeBooks.get(position)._id).toBundle()
                )
            true
        }
        binding.flBookType.adapter = object : TagAdapter<String>(bookTags) {
            override fun getView(parent: FlowLayout, position: Int, s: String): View {
                val tv = LayoutInflater.from(requireContext()).inflate(
                    R.layout.tags_tv,
                    binding.flBookType, false
                ) as TextView
                tv.text = s
                return tv
            }
        }

        binding.flBookType.setOnTagClickListener { _, position, _ ->
            showTagDialog(bookTags[position])
            true
        }
    }


    //获得照片的输出保存Uri
    private fun getImageCropUri(): Uri {
        val file = File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg")
        if (!file.parentFile.exists()) file.parentFile.mkdirs()
        return Uri.fromFile(file)
    }

    private fun loadUserIcon(url: String) {
        Glide.with(this)
            .load(url)
            .apply(
                RequestOptions
                    .bitmapTransform(BlurTransformation(15))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(
                        true
                    )
            )
            .into(iv_avatar)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        takePhoto.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun invoke(invokeParam: InvokeParam): PermissionManager.TPermissionType {
        val type = PermissionManager
            .checkPermission(TContextWrap.of(this), invokeParam.method)
        if (PermissionManager.TPermissionType.WAIT == type) {
            this.invokeParam = invokeParam
        }
        return type
    }

    override fun takeSuccess(result: TResult) {
        val mIconPath = result.image.originalPath
        userInfoViewModel.uploadAvatar(mIconPath)
            .bindDialog()
    }

    override fun takeCancel() {
    }

    override fun takeFail(result: TResult?, msg: String?) {
        ToastUtils.showShort(msg)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        hideAnimator()
    }

    private fun showTagDialog(tag: String) {
        BookTagDialogFragment().apply {
            arguments = BookTagArgs(tag).toBundle()
        }.also {
            it.setTargetFragment(this, 0)
            it.show(fragmentManager!!, "")
        }

        val duration: Long = 500
        val display = requireActivity().windowManager.defaultDisplay
        val scale = FloatArray(2)
        scale[0] = 1.0f
        scale[1] = 0.8f
        val animView = requireActivity().findViewById<View>(android.R.id.content)
        val scaleX = ObjectAnimator.ofFloat(animView, "scaleX", *scale).setDuration(duration)
        val scaleY = ObjectAnimator.ofFloat(animView, "scaleY", *scale).setDuration(duration)
        val rotation = floatArrayOf(0f, 10f, 0f)
        val rotationX = ObjectAnimator.ofFloat(animView, "rotationX", *rotation).setDuration(duration)

        val translation = FloatArray(1)
        translation[0] = -display.getWidth() * 0.2f / 2
        val translationY = ObjectAnimator.ofFloat(animView, "translationY", *translation).setDuration(duration)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, rotationX, translationY)
        animatorSet.setTarget(animView)
        animatorSet.start()

    }

    /**
     * 弹框关闭页面动画
     */
    private fun hideAnimator() {
        val duration: Long = 500
        val scale = FloatArray(2)
        scale[0] = 0.8f
        scale[1] = 1.0f
        val animView = requireActivity().findViewById<View>(android.R.id.content)
        val scaleX = ObjectAnimator.ofFloat(animView, "scaleX", *scale).setDuration(duration)
        val scaleY = ObjectAnimator.ofFloat(animView, "scaleY", *scale).setDuration(duration)
        val rotation = floatArrayOf(0f, 10f, 0f)
        val rotationX = ObjectAnimator.ofFloat(animView, "rotationX", *rotation).setDuration(duration)

        val translation = FloatArray(1)
        translation[0] = 0f
        val translationY = ObjectAnimator.ofFloat(animView, "translationY", *translation).setDuration(duration)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, rotationX, translationY)
        animatorSet.setTarget(animView)
        animatorSet.start()
    }
}