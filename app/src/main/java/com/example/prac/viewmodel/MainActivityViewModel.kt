package com.example.jetpackdemo

import android.util.Log.d
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jetpackdemo.apiCall.Retrofit
import com.example.prac.model.ModelFranquicias
import com.example.prac.model.ModelMain
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class MainActivityViewModel : ViewModel(), Observable {
    var mainList: ArrayList<ModelFranquicias?> = ArrayList()
    var list: MutableLiveData<ArrayList<ModelFranquicias?>> = MutableLiveData()
    var model: MutableLiveData<ModelMain> = MutableLiveData()
    var showLoader = ObservableBoolean(true)
    var page = ObservableInt(0)
    var navigator: MainActivityNavigator? = null
    @Transient
    private var mCallbacks: PropertyChangeRegistry? = null

    fun updateList(listWithData: ArrayList<ModelFranquicias?>) {
        list.value = listWithData
    }

    fun getUsers() {
        if (page.get() == 0)
            showLoader.set(true)
        else
            showLoader.set(false)
//        d("mytag", "START::::" + page.get().toString())

        Retrofit.apiService()!!.getAllFranquicias()!!.enqueue(object :
            Callback<ModelMain> {
            override fun onFailure(call: Call<ModelMain>, t: Throwable) {
//
                if (page.get() == 0)
                    showLoader.set(false)
            }

            override fun onResponse(
                call: Call<ModelMain>,
                response: Response<ModelMain>
            ) {
                if (page.get() == 0) {
                    showLoader.set(false)
                    mainList.clear()
//                    list.value!!.clear()
                }
                mainList.clear()

                if (page.get() != 0)
                    navigator!!.removeLastPositionFromList()
                val modelPassengers: ModelMain = response.body()!!
                model.value = modelPassengers
                mainList.addAll(modelPassengers.franquicias!!)
                list.value = mainList
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

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    fun notifyChange() {
        synchronized(this) {
            if (mCallbacks == null) {
                return
            }
        }
        mCallbacks!!.notifyCallbacks(this, 0, null)
    }

    fun notifyPropertyChanged(fieldId: Int) {
        synchronized(this) {
            if (mCallbacks == null) {
                return
            }
        }
        mCallbacks!!.notifyCallbacks(this, fieldId, null)
    }
}