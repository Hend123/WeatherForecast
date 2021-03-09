package com.example.retrofitandcoroutine.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private fun getInstance():Retrofit{
        return Retrofit.Builder().baseUrl(Constant.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    }
    fun getApiService(): ApiService {
        return getInstance().create(ApiService::class.java)
    }
}