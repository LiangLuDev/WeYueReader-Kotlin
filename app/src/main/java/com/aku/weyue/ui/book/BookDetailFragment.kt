package com.aku.weyue.ui.book

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.aku.aac.core.BaseVMFragment
import com.aku.weyue.BookTagArgs
import com.aku.weyue.R
import com.aku.weyue.databinding.BookDetailFragBinding
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.book_detail_frag.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * @author Zsc
 * @date   2019/5/3
 * @desc
 */
class BookDetailFragment : BaseVMFragment<BookDetailFragBinding>(),
    DialogInterface.OnDismissListener {

    override val layout: Int
        get() = R.layout.book_detail_frag

    private val tags = mutableListOf<String>()

    private val args: BookDetailFragmentArgs by navArgs()

    private val bookViewModel: BookDetailViewModel by viewModel {
        parametersOf(args.bookId)
    }

    override fun initData(savedInstanceState: Bundle?) {
        toolbar.setupWithNavController(findNavController())
        binding.t = bookViewModel
        binding.loadingLayout.setOnReloadListener {
            loadData()
        }

        loadData()
        ll_fow.setOnClickListener {
            bookViewModel.changeCollect()
        }
        crl_start_read.setOnClickListener {
            val navDirection = BookDetailFragmentDirections
                .actionBookDetailToReadFragment(
                    bookViewModel.book.value!!.createCollBookBean()
                )
            findNavController().navigate(navDirection)
        }
        fl_tags.adapter = tagAdapter
        bookViewModel.book.observe(this, Observer {
            //            bookViewModel.book.set(it)
            if (it?.tags != null) {
                tags.clear()
                tags.addAll(it.tags!!)
                tagAdapter.notifyDataChanged()
            }
        })

        bookViewModel.collect.observe(this, Observer {
            binding.ctvAddbook.text = if (it) {
                "移出书架"
            } else "添加书架"
        })

        fl_tags.setOnTagClickListener { _, position, _ ->
            showTagDialog(bookViewModel.book.value!!.tags!![position])
            true
        }

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

    private fun loadData() {
        bookViewModel.loadData()
    }

    private val tagAdapter = object : TagAdapter<String>(tags) {
        override fun getView(parent: FlowLayout, position: Int, s: String): View {
            val tv = LayoutInflater.from(requireActivity()).inflate(
                R.layout.tags_tv,
                fl_tags, false
            ) as TextView
            tv.text = s
            return tv
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        hideAnimator()
    }

}