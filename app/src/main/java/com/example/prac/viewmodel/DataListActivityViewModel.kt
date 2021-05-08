package com.example.jetpackdemo

import android.util.Log.d
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jetpackdemo.apiCall.Retrofit
import com.example.prac.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class DataListActivityViewModel : ViewModel(), Observable {
    var modelStoreDataToDB: ArrayList<ModelDataInfoToStoreInRoomDB?> = ArrayList()
    var filteredList: ArrayList<ModelFilteredDataInfo?> = ArrayList()
    var mainList: ArrayList<ModelDataInfo?> = ArrayList()
    var list: MutableLiveData<ArrayList<ModelDataInfoToStoreInRoomDB?>> = MutableLiveData()
    var model: MutableLiveData<ModelData> = MutableLiveData()
    var showLoader = ObservableBoolean(true)
    var page = ObservableInt(0)
    var navigator: DataListActivityNavigator? = null
    @Transient
    private var mCallbacks: PropertyChangeRegistry? = null

    fun updateList(listWithData: ArrayList<ModelDataInfoToStoreInRoomDB?>) {
        list.value = listWithData
    }

    fun getUsers(strKey: String) {
        if (page.get() == 0)
            showLoader.set(true)
        else
            showLoader.set(false)

        Retrofit.apiService()!!.getAllFranquiciasData(strKey)!!.enqueue(object :
            Callback<ModelData> {
            override fun onFailure(call: Call<ModelData>, t: Throwable) {
                if (page.get() == 0)
                    showLoader.set(false)
            }

            override fun onResponse(
                call: Call<ModelData>,
                response: Response<ModelData>
            ) {
                if (page.get() == 0) {
                    showLoader.set(false)
                    mainList.clear()
                }
                if (page.get() != 0)
                    navigator!!.removeLastPositionFromList()
                val modelPassengers: ModelData = response.body()!!
                model.value = modelPassengers
                mainList.addAll(modelPassengers.data!!)

                for (i in mainList.indices) {
                    for (j in filteredList.indices) {
                        //                        if main list has same nomre, take category object from main list object and ic_add it in filtered list
                        if (mainList[i]!!.nombre == filteredList[j]!!.nombre) run {
                            filteredList[j]!!.categoria.add(mainList[i]!!.categoria)
                        }
                        //                        Otherwise create new object and ic_add it in Filteredlist
                        else {
                            val categoria = ModelCategoria(
                                mainList[i]!!.categoria.idcategoriamenu,
                                mainList[i]!!.categoria.nombremenu
                            )
                            val arrListCategoria: ArrayList<ModelCategoria?> = ArrayList()
                            arrListCategoria.add(categoria)
                            val modelFilteredData = ModelFilteredDataInfo(
                                mainList[i]!!.idmenu,
                                mainList[i]!!.precioSugerido,
                                mainList[i]!!.nombre,
                                arrListCategoria
                            )
                            filteredList.add(modelFilteredData)
                        }
                    }
                }
                for (i in filteredList.indices) {
                    val modelRoomDB = ModelDataInfoToStoreInRoomDB(
                        filteredList[i]!!.idmenu, filteredList[i]!!.precioSugerido, "1",
                        filteredList[i]!!.nombre,
                        filteredList[i]!!.categoria.nombremenu
                    )
                    modelStoreDataToDB.add(modelRoomDB)
                }
                navigator!!.sendListToRoomDB(modelStoreDataToDB)
//                list.value = mainList
            }
        })
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        synchronized(this) {
            if (mCallbacks == null) {
                return
            }
        }
        mCallbacks!!.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        synchronized(this) {
            if (mCallbacks == null) {
                mCallbacks = PropertyChangeRegistry()
            }
        }
        mCallbacks!!.add(callback)
    }

}