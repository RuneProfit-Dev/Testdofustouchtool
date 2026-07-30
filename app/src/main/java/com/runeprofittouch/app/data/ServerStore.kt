package com.runeprofittouch.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ServerStore {

    val servers = listOf("Tiliwan", "Kelerog", "Blair", "Talok")

    private const val PREFERENCES = "server_preferences"
    private const val KEY_SELECTED_SERVER = "selected_server"
    private val mutableSelectedServer = MutableStateFlow("Tiliwan")

    val selectedServer: StateFlow<String> = mutableSelectedServer.asStateFlow()

    fun initialize(context: Context) {
        val saved = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getString(KEY_SELECTED_SERVER, "Tiliwan")
            .orEmpty()
        mutableSelectedServer.value = saved.takeIf { it in servers } ?: "Tiliwan"
    }

    fun select(context: Context, server: String) {
        if (server !in servers) return
        mutableSelectedServer.value = server
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SELECTED_SERVER, server)
            .apply()
    }
}
