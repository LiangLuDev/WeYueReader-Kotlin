package com.aku.weyue.ui.book

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aku.aac.kchttp.ext.awaitResult
import com.aku.aac.kchttp.ext.doError
import com.aku.aac.kchttp.ext.doSuccess
import com.aku.weyue.BookTagArgs
import com.aku.weyue.R
import com.aku.weyue.api.BookApi
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

/**
 * Created by Liang_Lu on 2017/12/20.
 */

class BookTagDialogFragment : DialogFragment() {

    private val args: BookTagArgs by navArgs()

    private val bookApi: BookApi by inject()

    private lateinit var mRefreshLayout: SmartRefreshLayout
    private lateinit var mRvBooks: RecyclerView
    private lateinit var mBookTagsAdapter: BookTagsAdapter
    internal var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, R.style.MyDialog)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        mRefreshLayout = view.findViewById(R.id.refreshLayout)
        mRvBooks = view.findViewById(R.id.rv_books)
        tvTitle.text = args.bookTag
        mRvBooks.layoutManager = LinearLayoutManager(requireContext())
        mBookTagsAdapter = BookTagsAdapter()
        mRvBooks.adapter = mBookTagsAdapter
        mBookTagsAdapter.setOnItemClickListener { _, _, position ->
            onDismiss(dialog!!)
            val item = mBookTagsAdapter.getItem(position)!!
            requireActivity().findNavController(R.id.main_nav)
                .navigate(
                    R.id.book_detail,
                    BookDetailFragmentArgs(item._id)
                        .toBundle()
                )
        }

        mRefreshLayout.setOnLoadMoreListener {
            ++page
            this@BookTagDialogFragment.getBooksByTag()
        }
        getBooksByTag()


    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window!!
        window.setGravity(Gravity.BOTTOM)
        val attributes = window.attributes
        val display = window.windowManager.defaultDisplay
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        val margin = resources.getDimensionPixelOffset(R.dimen.wy_margin_large)
        attributes.width = display.width - 2 * margin
        attributes.height = display.height / 2
        attributes.y = margin
        window.attributes = attributes


    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val listener = targetFragment as? DialogInterface.OnDismissListener
        listener?.onDismiss(dialog)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_book_tag, container, false)

    }


    private fun getBooksByTag() {
        GlobalScope.launch(Dispatchers.IO) {
            val books = bookApi.booksByTagAsync(args.bookTag, page).awaitResult()
            withContext(Dispatchers.Main) {
                if (isVisible) {
                    books.doSuccess {
                        mRefreshLayout.finishLoadMore()
                        mBookTagsAdapter.addData(it)
                    }.doError {
                        mRefreshLayout.finishRefresh()
                            .finishLoadMore()
                        ToastUtils.showShort(it.msg)
                    }
                }
            }
        }
    }


}
