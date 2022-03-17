package com.daily.news.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build

fun isNetworkConnected(context: Context): Boolean {
    val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    connMgr ?: return false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network: Network = connMgr.activeNetwork ?: return false
        val capabilities = connMgr.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    } else {
        val networkInfo = connMgr.activeNetworkInfo ?: return false
        return networkInfo.isAvailable && (networkInfo.isConnected || networkInfo.isConnectedOrConnecting)
    }
}