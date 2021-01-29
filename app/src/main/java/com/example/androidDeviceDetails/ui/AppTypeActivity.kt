package com.example.androidDeviceDetails.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.database.AppInfoRaw
import com.example.androidDeviceDetails.databinding.ActivityAppTypeBinding

class AppTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppTypeBinding
    private lateinit var controller: ActivityController<AppInfoRaw>

    companion object {
        const val NAME = "appType"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_type)
        controller = ActivityController(this, NAME, binding)
        binding.tabLayout.setTabTextColors(
            ContextCompat.getColor(this, R.color.white),
            ContextCompat.getColor(this, R.color.white)
        )
    }
}