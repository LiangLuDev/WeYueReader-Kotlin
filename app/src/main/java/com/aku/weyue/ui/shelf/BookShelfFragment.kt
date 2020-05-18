package com.aku.weyue.ui.shelf

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.aku.aac.core.BaseFragment
import com.aku.weyue.R
import com.aku.weyue.ui.book.BookDetailFragmentDirections
import com.aku.weyue.ui.read.ReadFragmentArgs
import kotlinx.android.synthetic.main.book_shelf_frag.*

/**
 * @author Zsc
 * @date   2019/5/18
 * @desc
 */
class BookShelfFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.book_shelf_frag

    private val bookViewModel: BookShelfViewModel by viewModels()
    private val mAdapter: BookShelfAdapter by lazy {
        BookShelfAdapter()
    }

    override fun initData(savedInstanceState: Bundle?) {
        rvShelf.adapter = mAdapter
        bookViewModel.books.observe(this, Observer {
            mAdapter.setNewData(it)
        })
        requireActivity().findNavController(R.id.main_nav)
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.main_fragment,
                    R.id.action_main_fragment_to_book_list -> {
                        reload()
                    }
                }
            }
        reload()
        mAdapter.setOnItemClickListener { _, _, position ->
            val item = mAdapter.getItem(position)!!
            if (item.isLocal) {
                requireActivity()
                    .findNavController(R.id.main_nav)
                    .navigate(
                        R.id.read_fragment,
                        ReadFragmentArgs(item.createCollBookBean()).toBundle()
                    )
            } else {
                requireActivity()
                    .findNavController(R.id.main_nav)
                    .navigate(BookDetailFragmentDirections.bookDetail(item._id))
            }

        }
        mAdapter.setOnItemLongClickListener { _, _, position ->
            val item = mAdapter.getItem(position)!!
            var clearLocal = false
            AlertDialog.Builder(requireContext())
                .setTitle(item.title)
                .setNegativeButton("取消", null)
                .setMultiChoiceItems(
                    arrayOf("删除本地书籍"),
                    booleanArrayOf(false)
                ) { _, _, isChecked ->
                    clearLocal = isChecked
                }
                .setPositiveButton("删除") { _, _ ->
                    bookViewModel.removeBookShelf(item, clearLocal)
                        .invokeOnCompletion {
                            mAdapter.remove(position)
                        }
                }.show()
            return@setOnItemLongClickListener true
        }
    }

    private fun reload() {
        bookViewModel.loadBookShelf()
    }

}