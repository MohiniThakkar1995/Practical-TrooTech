package com.example.jetpackdemo.apiCall

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class Retrofit {
    companion object{
        private fun <T> builder(endpoint: Class<T>): T {
            return Retrofit.Builder()
                .baseUrl("https://api.invupos.com/invuApiPos/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint)
        }
        fun apiService(): ApiService? {
            return builder(ApiService::class.java)
        }
    }
}