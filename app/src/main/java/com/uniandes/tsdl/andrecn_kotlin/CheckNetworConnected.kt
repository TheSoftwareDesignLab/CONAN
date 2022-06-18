package com.uniandes.tsdl.andrecn_kotlin


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.OkHttpClient

class CheckNetworConnected {

    private val TAG: String = CheckNetworConnected::class.java.getSimpleName()

    fun toMyString(): String {

        val client = OkHttpClient()

        val client_2 = OkHttpClient()

        return "exito"
    }

    fun isCheckConnectivity(context:Context):Boolean
    {
//        val connMgr = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
//        var isWifiConn: Boolean = false
//        var isMobileConn: Boolean = false
//        connMgr.allNetworks.forEach { network ->
//            connMgr.getNetworkInfo(network)?.apply {
//                if (type == ConnectivityManager.TYPE_WIFI) {
//                    isWifiConn = isWifiConn or isConnected
//                }
//                if (type == ConnectivityManager.TYPE_MOBILE) {
//                    isMobileConn = isMobileConn or isConnected
//                }
//            }
//            val netwCap = connMgr.getNetworkCapabilities(network)
//            if (netwCap != null) {
//                netwCap.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
//            }
//        }
        return true
    }





}