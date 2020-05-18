package com.aku.weyue.ui.list

import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.aku.aac.core.BaseFragment
import com.aku.weyue.BookListArgs
import com.aku.weyue.R
import kotlinx.android.synthetic.main.book_list_frag.*
import kotlinx.android.synthetic.main.include_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author Zsc
 * @date   2019/5/3
 * @desc
 */
class BookListFragment : BaseFragment() {
    override val layout: Int
        get() = R.layout.book_list_frag

    private val mBooksViewModel: BookListTypeViewModel by viewModel()
    private val args: BookListArgs by navArgs()
    private val mAdapter by lazy { BookAdapter() }
    private var curPage = 1

    override fun initData(savedInstanceState: Bundle?) {
        mAdapter.bindToRecyclerView(rvBookInfo)
        refreshLayout.setOnRefreshListener {
            loadData(1)
        }.setOnLoadMoreListener {
            loadData(curPage + 1)
        }
        loadData(1)
        mAdapter.setOnItemClickListener { _, v, position ->
            val img = v.findViewById<ImageView>(R.id.book_brief_iv_portrait)
            val item = mAdapter.getItem(position)!!
            val navDirections = BookListTypeFragmentDirections.actionBookListToBookDetail(item._id)

            requireActivity()
                .findNavController(R.id.main_nav)
                .navigate(
                    navDirections
                    //Fixme 共享view动画无效！！！
                    , FragmentNavigatorExtras(Pair(img, "bookImage"))
                )

        }
        mBooksViewModel.status.observe(this, Observer {
            refreshLayout.finishRefresh()
                .finishLoadMore()
        })

        mBooksViewModel.books.observe(this, Observer {
            curPage = mBooksViewModel.curPage
            if (curPage == 1) {
                mAdapter.setNewData(it)
            } else {
                mAdapter.addData(it)
            }
        })


    }

    private fun loadData(page: Int) {
        mBooksViewModel.load(
            args.bookType,
            args.bookTitle,
            page
        )

    }


    companion object {

        const val HOT = "hot"
        const val NEW = "new"
        const val GOOD = "reputation"

        fun create(bookTitle: String, bookType: String): BookListFragment {
            return BookListFragment()
                .apply {
                    arguments = BookListArgs(bookTitle, bookType).toBundle()
                }
        }
    }
}