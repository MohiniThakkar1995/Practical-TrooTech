package com.example.jetpackdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jetpackdemo.DataListActivityViewModel
import com.example.prac.R
import com.example.prac.model.ModelDataInfoToStoreInRoomDB
import kotlinx.android.synthetic.main.row_expandable.view.*
import kotlinx.android.synthetic.main.row_loading.view.*
import kotlinx.android.synthetic.main.row_users.view.title

//TODO DELETE
class AdapterAllData(
    val viewModel: DataListActivityViewModel,
    var arrayList: ArrayList<ModelDataInfoToStoreInRoomDB?>?,
    recyclerView: RecyclerView? = null,
    val context: Context
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
    var mOnAddClickListener: OnAddClickListener? = null

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
                LayoutInflater.from(parent.context).inflate(R.layout.row_expandable, parent, false)
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
            val model: ModelDataInfoToStoreInRoomDB = arrayList?.get(position)!!
            holder.itemView.title.text = model.nombremenu
            holder.itemView.tvNomre.text = model.nombre
            holder.itemView.setOnClickListener {
                mOnClickListener!!.onClick(model)
            }
            holder.itemView.ivPlus.setOnClickListener {
                mOnAddClickListener!!.onAdd(model, position)
            }
        } else {
            holder.itemView.progress.isIndeterminate = true
        }
    }

    interface OnClickListener {
        fun onClick(ModelDataInfoToStoreInRoomDB: ModelDataInfoToStoreInRoomDB)
    }

    interface OnAddClickListener {
        fun onAdd(ModelDataInfoToStoreInRoomDB: ModelDataInfoToStoreInRoomDB, pos: Int)
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    fun setLoaded() {
        isLoading = false
    }

    fun setList(arrayList: ArrayList<ModelDataInfoToStoreInRoomDB?>?) {
        this.arrayList = arrayList
        notifyDataSetChanged()
    }
}
