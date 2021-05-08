package com.example.jetpackdemo.apiCall

import com.example.prac.model.ModelData
import com.example.prac.model.ModelMain
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @Headers("APIKEY: bd_suvlascentralpos")
    @GET("index.php?r=configuraciones/franquicias")
    fun getAllFranquicias(): Call<ModelMain>?

    @GET("index.php?r=menu")
    fun getAllFranquiciasData(@Header("APIKEY") key : String): Call<ModelData>?
}