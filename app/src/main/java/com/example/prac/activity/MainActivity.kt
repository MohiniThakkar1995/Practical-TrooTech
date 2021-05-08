package com.example.jetpackdemo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jetpackdemo.adapter.AdapterFranquicias
import com.example.prac.R
import com.example.prac.databinding.ActivityMainBinding
import com.example.prac.model.ModelFranquicias
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), MainActivityNavigator {
    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainActivityViewModel
    private lateinit var adapter: AdapterFranquicias

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            return intent
        }
    }

    //    oncreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.invalidateAll()
        viewModel = ViewModelProvider(this, ViewModelProviderFactory(MainActivityViewModel())).get(
            MainActivityViewModel::class.java
        )
        viewModel.navigator = this
        binding.viewmodel = viewModel
        adapter = AdapterFranquicias(viewModel, null, rvData)
        binding.rvData.adapter = adapter

        adapter.mOnClickListener = object : AdapterFranquicias.OnClickListener {
            override fun onClick(modelData: ModelFranquicias) {
                startActivity(
                    AllFranquiciasDataActivity.newIntent(
                        this@MainActivity,
                        modelData.APIKEY,
                        modelData.negocio
                    )
                )
            }
        }
        adapter.mOnLoadMoreListener = object : AdapterFranquicias.OnLoadMoreListener {
            override fun onLoadMore() {
                viewModel.page.set(viewModel.page.get() + 20)
                viewModel.list.value!!.add(null)
                adapter.notifyItemInserted(viewModel.list.value!!.size - 1)
                GlobalScope.launch {
                    viewModel.getUsers()
                }
            }

        }
//    api call using coroutine
        GlobalScope.launch {
            viewModel.getUsers()
        }
//        list oberserver
        viewModel.list.observe(this, Observer {
            adapter.setLoaded()
            adapter.setList(it)
        })
    }

    override fun removeLastPositionFromList() {
        if (viewModel.list.value!!.size > 0) {
            viewModel.list.value!!.removeAt(viewModel.list.value!!.size - 1)
            adapter.notifyItemRemoved(viewModel.list.value!!.size)
        }
    }
}
