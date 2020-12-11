package com.example.androidDeviceDetails.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class PrefManager {
    private val name = "AppInfo"
    private val mode = Context.MODE_PRIVATE
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    companion object {
        const val INITIAL_LAUNCH = "initialLaunch"

        fun createInstance(context: Context): PrefManager {
            val prefManager = PrefManager()
            prefManager.initPref(context)
            return prefManager
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun initPref(context: Context) {
        if (!this::sharedPreferences.isInitialized)
            sharedPreferences = context.getSharedPreferences(name, mode)
        editor = sharedPreferences.edit()
    }

    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).commit()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    fun remove(key: String): Boolean {
        return editor.remove(key).commit()
    }

    fun clearPref(): Boolean {
        return editor.clear().commit()
    }
}