package com.aku.weyue.ui.scan

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.aku.aac.core.BaseFragment
import com.aku.weyue.R
import com.aku.common.koin.parentViewModels
import com.aku.weyue.util.bindDialog
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.local_book_frag.*

/**
 * @author Zsc
 * @date   2019/6/2
 * @desc
 */
class LocalBookFragment : BaseFragment(), BaseScanBook {

    override val layout: Int
        get() = R.layout.local_book_frag

    private val mAdapter: FileAdapter by lazy { FileAdapter() }
    private val localBookVM: LocalBookViewModel by parentViewModels()
    private val selectChange by lazy {
        parentFragment as SelectChange
    }

    override fun initData(savedInstanceState: Bundle?) {
        mAdapter.bindToRecyclerView(rv_files)
        rv_files.layoutManager = LinearLayoutManager(requireContext())
        btn_scan.setOnClickListener {
            mAdapter.setNewData(null)
            localBookVM.loadBooksFromCursor()
                .bindDialog()
        }
        mAdapter.setOnItemClickListener { _, _, position ->
            val item = mAdapter.getItem(position)!!
            if (mAdapter.allShelfBookIds.contains(item.path)) {
                return@setOnItemClickListener
            }
            item.isSelect = item.isSelect.not()
            mAdapter.notifyItemChanged(position)
            refreshSelect()
        }
        //直接永久绑定，在viewModel里已添加了协程取消
        localBookVM.bookFilesLiveData.observe(this, Observer {
            LogUtils.d(it.size)
            mAdapter.addData(it)
        })
        localBookVM.refreshLocalBooks()
        localBookVM.localBookIdsLiveData.observe(this, Observer {
            mAdapter.allShelfBookIds.clear()
            mAdapter.allShelfBookIds.addAll(it)
            mAdapter.notifyDataSetChanged()
        })

    }

    override fun changeAll(select: Boolean) {
        mAdapter.data.filter {
            mAdapter.allShelfBookIds.contains(it.path).not()
        }.forEach {
            it.isSelect = select
        }
        mAdapter.notifyDataSetChanged()
        refreshSelect()
    }

    override fun delete() {
        val selectFiles = mAdapter.data.filter {
            it.isSelect
        }
        if (selectFiles.isEmpty()) {
            return
        }
        AlertDialog.Builder(requireContext())
            .setTitle("删除")
            .setMessage("确定要删除选中的${selectFiles.size}个文件么？")
            .setPositiveButton("删除") { _, _ ->
                ToastUtils.showShort("假装删除")
            }.setNegativeButton("取消", null)
            .show()
    }

    override fun addShelf() {
        mAdapter.data.filter {
            it.isSelect
        }.run {
            if (isEmpty()) {
                ToastUtils.showShort("请先选择文件")
                return
            }
            ToastUtils.showShort("已添加到书架")
            localBookVM.addShelf(this)
                .invokeOnCompletion {
                    forEach {
                        it.isSelect = false
                    }
                    refreshSelect()
                    localBookVM.refreshLocalBooks()
                }
        }

    }

    override fun refreshSelect() {
        selectChange.changeSelect(mAdapter.data.any { it.isSelect.not() },
            mAdapter.data.count { it.isSelect })
    }

}