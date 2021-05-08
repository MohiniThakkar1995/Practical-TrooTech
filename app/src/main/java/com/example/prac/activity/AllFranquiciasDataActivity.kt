package com.example.jetpackdemo

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.jetpackdemo.adapter.AdapterAllData
import com.example.jetpackdemo.roomdb.Data
import com.example.jetpackdemo.roomdb.DatabaseData
import com.example.prac.R
import com.example.prac.databinding.ActivityFranquiciasBinding
import com.example.prac.model.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.doAsync

class AllFranquiciasDataActivity : AppCompatActivity(), DataListActivityNavigator {
    lateinit var binding: ActivityFranquiciasBinding
    lateinit var viewModel: DataListActivityViewModel
    private lateinit var adapter: AdapterAllData
    private lateinit var strAPikey: String
    private lateinit var strNomre: String
    private lateinit var mDb: DatabaseData

    companion object {
        const val APIKEY = "apiKey";
        const val NEGOCIO = "negocio";
        fun newIntent(context: Context, strApiKey: String, negocio: String): Intent {
            val intent = Intent(context, AllFranquiciasDataActivity::class.java)
            intent.putExtra(APIKEY, strApiKey)
            intent.putExtra(NEGOCIO, negocio)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_franquicias)
        mDb = DatabaseData.getInstance(applicationContext)

        getIntentData()
        binding.invalidateAll()
        viewModel =
            ViewModelProvider(this, ViewModelProviderFactory(DataListActivityViewModel())).get(
                DataListActivityViewModel::class.java
            )
        viewModel.navigator = this
        binding.viewmodel = viewModel
//        binding.rvData.adapter = adapter
        adapter = AdapterAllData(viewModel, null, rvData, this)
        binding.rvData.adapter = adapter

        adapter.mOnClickListener = object : AdapterAllData.OnClickListener {
            override fun onClick(modelFilteredData: ModelDataInfoToStoreInRoomDB) {
            }
        }

        adapter.mOnAddClickListener = object : AdapterAllData.OnAddClickListener {
            override fun onAdd(modelFilteredData: ModelDataInfoToStoreInRoomDB, pos: Int) {
                openCustomDialog(modelFilteredData, pos)
            }
        }
        adapter.mOnLoadMoreListener = object : AdapterAllData.OnLoadMoreListener {
            override fun onLoadMore() {
                viewModel.page.set(viewModel.page.get() + 20)
                viewModel.list.value!!.add(null)
                adapter.notifyItemInserted(viewModel.list.value!!.size - 1)
                GlobalScope.launch {
                    viewModel.getUsers(strAPikey)
                }
            }
        }
        GlobalScope.launch {
            viewModel.getUsers(strAPikey)
        }
        viewModel.list.observe(this, Observer {
            //            d("mytag", "List" + Gson().toJson(it))
            adapter.setLoaded()
            adapter.setList(it)
        })
        binding.titleNomre.text = strNomre

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun getDataFromDataBase() {
        val dbTest = Room.databaseBuilder(
            applicationContext,
            DatabaseData::class.java, "roomdb"
        ).build()

        val dao = dbTest.studentDao()
        val allStoredData: List<Data?> = dao.allData()

        d("mytag", "ALL STORED DATA" + Gson().toJson(allStoredData))
        val list: ArrayList<ModelDataInfoToStoreInRoomDB?> = ArrayList()
        for (i in allStoredData.indices) {
            val modelDataStoreInRommDb = ModelDataInfoToStoreInRoomDB(
                allStoredData[i]!!.idmenu,
                allStoredData[i]!!.precioSugerido,
                allStoredData[i]!!.qty,
                allStoredData[i]!!.nombre,
                allStoredData[i]!!.nombremenu
            )
            list.add(modelDataStoreInRommDb)
        }
        runOnUiThread {
            viewModel.updateList(list)
        }
    }

//    open quantity dialog
    private fun openCustomDialog(modelData: ModelDataInfoToStoreInRoomDB, pos: Int) {
        val priceMain = modelData.precioSugerido.toDouble() / modelData.qty.toInt()
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_article)
        val ivPlus = dialog.findViewById(R.id.ivPlus) as ImageView
        val ivMinus = dialog.findViewById(R.id.ivMinus) as ImageView
        val etQuantity = dialog.findViewById(R.id.edtQuantity) as EditText
        val name = dialog.findViewById(R.id.name) as TextView
        val price = dialog.findViewById(R.id.price) as TextView
        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
        val tvConfirm = dialog.findViewById(R.id.tvConfirm) as TextView
        ivPlus.setOnClickListener {
            etQuantity.setText((etQuantity.text.toString().toInt() + 1).toString())
            price.setText((etQuantity.text.toString().toInt() * priceMain).toString())
        }
        ivMinus.setOnClickListener {
            if (etQuantity.text.toString().toInt() > 1) {
                etQuantity.setText((etQuantity.text.toString().toInt() - 1).toString())
                price.setText((etQuantity.text.toString().toInt() * priceMain).toString())
            }
        }

        doAsync {
            val modelDataDB = mDb.studentDao().getLiveOrderById(modelData.idmenu)
            runOnUiThread {
                name.text = modelDataDB!!.nombremenu
                etQuantity.setText(modelDataDB.qty)
                price.setText((modelData.precioSugerido.toDouble()).toString())
            }
        }

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        tvConfirm.setOnClickListener {
            dialog.dismiss()
            updateDataToRoomDB(price.text.toString(), etQuantity.text.toString(), modelData.idmenu)
        }
        dialog.show()

    }

//    update data to roomdb
    private fun updateDataToRoomDB(amount: String, qty: String, id: String) {
        doAsync {
            mDb.studentDao().update(amount, qty, id)
            getDataFromDataBase()
        }
    }

    private fun getIntentData() {
        if (intent != null) {

            if (intent.hasExtra(APIKEY)) {
                strAPikey = intent.getStringExtra(APIKEY)!!
            }
            if (intent.hasExtra(NEGOCIO)) {
                strNomre = intent.getStringExtra(NEGOCIO)!!
            }
        }
    }

    override fun removeLastPositionFromList() {
        if (viewModel.list.value!!.size > 0) {
            viewModel.list.value!!.removeAt(viewModel.list.value!!.size - 1)
            adapter.notifyItemRemoved(viewModel.list.value!!.size)
        }
    }

//    store list in room db
    override fun sendListToRoomDB(arrList: ArrayList<ModelDataInfoToStoreInRoomDB?>) {
        doAsync {
            for (i in arrList.indices) {
                val data = Data(
                    arrList[i]!!.idmenu, arrList[i]!!.precioSugerido, arrList[i]!!.qty,
                    arrList[i]!!.nombre,
                    arrList[i]!!.nombremenu
                )
                val modelDataDB = mDb.studentDao().getLiveOrderById(arrList[i]!!.idmenu)
                if (modelDataDB == null)
                    mDb.studentDao().insert(data)
            }

            val dbTest = Room.databaseBuilder(
                applicationContext,
                DatabaseData::class.java, "roomdb"
            ).build()

            val dao = dbTest.studentDao()
            val allStoredData: List<Data?> = dao.allData()

            d("mytag", "ALL STORED DATA" + Gson().toJson(allStoredData))
            val list: ArrayList<ModelDataInfoToStoreInRoomDB?> = ArrayList()
            for (i in allStoredData.indices) {
                val modelDataStoreInRommDb = ModelDataInfoToStoreInRoomDB(
                    allStoredData[i]!!.idmenu,
                    allStoredData[i]!!.precioSugerido,
                    allStoredData[i]!!.qty,
                    allStoredData[i]!!.nombre,
                    allStoredData[i]!!.nombremenu
                )
                list.add(modelDataStoreInRommDb)
            }
            runOnUiThread {
                viewModel.updateList(list)
            }

        }
    }

}
