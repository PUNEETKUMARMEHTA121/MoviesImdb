package com.example.popularmovies.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.annotation.RequiresApi

object Utility {
    const val API_KEY = "09585aebee9ee5d6d6503e772b474b39"
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w342"
    const val PAGE_SIZE = 6
    const val movieId = "movieId"

    fun isOnline(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isOnlineModern(context)
        } else {
            isOnlineOld(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isOnlineModern(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    @Suppress("DEPRECATION")  // Suppress deprecation warning for old API
    private fun isOnlineOld(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        @Suppress("DEPRECATION")  // Suppress deprecation warning for old API
        val networkInfo: android.net.NetworkInfo? = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }
}