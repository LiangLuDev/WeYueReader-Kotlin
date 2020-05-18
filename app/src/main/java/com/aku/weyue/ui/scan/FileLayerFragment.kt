package com.aku.weyue.ui.scan

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.aku.aac.core.BaseFragment
import com.aku.weyue.R
import com.aku.common.koin.parentViewModels
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.file_layer_frag.*
import java.io.File

/**
 * @author Zsc
 * @date   2019/6/2
 * @desc
 */
class FileLayerFragment : BaseFragment(), BaseScanBook {


    override val layout: Int
        get() = R.layout.file_layer_frag

    private val mAdapter: FileAdapter by lazy { FileAdapter() }

    private val sdCard = Environment.getExternalStorageDirectory();
    private var curFile: File = sdCard

    private val File.sdPath: String
        get() = "储存卡:${path.replace(sdCard.path, "")}"

    private val fileLayerVM: LocalBookViewModel by parentViewModels()
    private val selectChange by lazy {
        parentFragment as SelectChange
    }

    override fun initData(savedInstanceState: Bundle?) {
        mAdapter.bindToRecyclerView(rv_file_category)
        rv_file_category.layoutManager = LinearLayoutManager(requireContext())
        fileLayerVM.loadBooks(curFile)
        fileLayerVM.bookLayerFilesLiveData.observe(this, Observer {
            mAdapter.setNewData(it)
            refreshSelect()
        })
        file_category_tv_path.text = curFile.sdPath
        file_category_tv_back_last.setOnClickListener {
            if (curFile == sdCard) {
                ToastUtils.showShort("已经是最上一级了")
                return@setOnClickListener
            }
            curFile = curFile.parentFile
            file_category_tv_path.text = curFile.sdPath
            mAdapter.allShelfBookIds.clear()
            fileLayerVM.loadBooks(curFile)
        }
        fileLayerVM.localBookIdsLiveData.observeForever {
            mAdapter.allShelfBookIds.clear()
            mAdapter.allShelfBookIds.addAll(it)
            mAdapter.notifyDataSetChanged()
        }
        mAdapter.setOnItemClickListener { _, _, position ->
            val item = mAdapter.getItem(position)!!
            if (item.file.isDirectory) {
                curFile = item.file
                file_category_tv_path.text = curFile.sdPath
                mAdapter.allShelfBookIds.clear()
                fileLayerVM.loadBooks(curFile)
            } else {
                if (item.isTxt.not()
                    || mAdapter.allShelfBookIds.contains(item.path)
                ) {
                    return@setOnItemClickListener
                }
                item.isSelect = item.isSelect.not()
                mAdapter.notifyItemChanged(position)
                refreshSelect()
            }
        }

    }

    override fun changeAll(select: Boolean) {
        mAdapter.data.filter {
            it.isTxt &&
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
            fileLayerVM.addShelf(this)
                .invokeOnCompletion {
                    forEach {
                        it.isSelect = false
                    }
                    refreshSelect()
                    fileLayerVM.refreshLocalBooks()
                }
        }
    }

    override fun refreshSelect() {
        selectChange.changeSelect(mAdapter.data.any { it.isSelect.not() },
            mAdapter.data.count { it.isSelect })
    }
}