package com.aku.weyue.ui.type

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.aku.aac.core.BaseVMFragment
import com.aku.common.koin.parentViewModels
import com.aku.weyue.BookTypeArgs
import com.aku.weyue.R
import com.aku.weyue.data.BookTotal
import com.aku.weyue.data.BookType
import com.aku.weyue.data.source.SpSource
import com.aku.weyue.databinding.RecyclerViewBinding
import com.aku.weyue.ui.list.BookListTypeFragmentArgs

/**
 * @author Zsc
 * @date   2019/4/29
 * @desc
 */
class BaseTypeFragment : BaseVMFragment<RecyclerViewBinding>() {

    private val args: BookTypeArgs by navArgs()

    private val mAdapter by lazy { BookTypeAdapter() }

    private val bookTotalViewModel: BookTotalViewModel by parentViewModels()

    override val layout = R.layout.recycler_view

    override fun initData(savedInstanceState: Bundle?) {

        binding.recyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener { _, _, position ->
            if (SpSource.user == null) {
                requireActivity().findNavController(R.id.main_nav)
                    .navigate(
                        R.id.login_fragment
                    )
                return@setOnItemClickListener
            }
            val item = mAdapter.getItem(position)!!
            requireActivity().findNavController(R.id.main_nav)
                .navigate(
                    R.id.book_list,
                    BookListTypeFragmentArgs(item.name).toBundle()
                )
        }
        subscribeUi()

    }

    private fun subscribeUi() {
        binding.bookTotal = bookTotalViewModel
        bookTotalViewModel.bookTotalLiveData.observe(this, Observer { list ->
            mAdapter.setNewData(getListByType(list))
        })
        binding.loadingLayout.setOnReloadListener {
            loadData()
        }
        loadFirst()
    }

    private fun loadFirst() {
        if (args.bookType == BOY_TYPE) {
            loadData()
        }
    }

    private fun loadData() {
        bookTotalViewModel.load()
    }

    private fun getListByType(total: BookTotal): MutableList<BookType> {
        return when (args.bookType) {
            BOY_TYPE -> total.male
            GIRL_TYPE -> total.female
            PUBLISH_TYPE -> total.press
            else -> mutableListOf()
        }

    }

    companion object {

        const val BOY_TYPE = 0
        const val GIRL_TYPE = 1
        const val PUBLISH_TYPE = 2

        fun create(bookType: Int): BaseTypeFragment {
            return BaseTypeFragment()
                .apply {
                    arguments = BookTypeArgs(bookType).toBundle()
                }
        }
    }
}