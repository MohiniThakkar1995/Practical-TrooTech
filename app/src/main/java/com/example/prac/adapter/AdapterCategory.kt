package com.example.jetpackdemo.adapter

import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jetpackdemo.MainActivityViewModel
import com.example.prac.R
import com.example.prac.model.ModelCategoria
import kotlinx.android.synthetic.main.row_loading.view.*
import kotlinx.android.synthetic.main.row_users.view.*

class AdapterCategory(
    var arrayList: ArrayList<ModelCategoria?>?,
    recyclerView: RecyclerView? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ROW_ITEM = 0
    private val ROW_PROG = 2
    private val visibleThreshold = 1
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    var isLoading: Boolean = false
    var mOnLoadMoreListener: OnLoadMoreListener? = null
    var mOnClickListener: OnClickListener? = null

    init {
        val linearLayoutManager = recyclerView?.layoutManager as LinearLayoutManager?
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager?.itemCount!!
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    /*
                     End has been reached Do something
                   */mOnLoadMoreListener?.onLoadMore()
                    isLoading = true
                }
            }
        })
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == ROW_ITEM) {
            val root =
                LayoutInflater.from(parent.context).inflate(R.layout.row_category, parent, false)
            MainViewHolder(root)
        } else {
            val root =
                LayoutInflater.from(parent.context).inflate(R.layout.row_loading, parent, false)
            LoadingView(root)
        }
    }

    override fun getItemCount(): Int {
        return if (arrayList != null && arrayList!!.size > 0)
            arrayList?.size!!
        else
            0
    }

    override fun getItemViewType(position: Int): Int {
        return if (arrayList?.get(position) != null) {
            ROW_ITEM
        } else
            ROW_PROG
    }

    inner class MainViewHolder(binding: View) : RecyclerView.ViewHolder(binding)
    inner class LoadingView(binding: View) : RecyclerView.ViewHolder(binding)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MainViewHolder) {
            val model: ModelCategoria = arrayList?.get(position)!!
            holder.itemView.title.text = model.nombremenu

            holder.itemView.setOnClickListener {
                mOnClickListener!!.onClick(model)
            }
        } else {
            holder.itemView.progress.isIndeterminate = true
        }
    }

    interface OnClickListener {
        fun onClick(modelData: ModelCategoria)
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    fun setLoaded() {
        isLoading = false
    }

    fun setList(arrayList: ArrayList<ModelCategoria?>?) {
        this.arrayList = arrayList
        notifyDataSetChanged()
    }
}
