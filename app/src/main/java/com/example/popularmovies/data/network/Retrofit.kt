package com.example.popularmovies.data.network

import com.example.popularmovies.data.Utility
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = Utility.BASE_URL

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object MoviesApiServiceInstance {
    val apiService: MovieApiService by lazy {
        RetrofitInstance.retrofit.create(MovieApiService::class.java)
    }
}