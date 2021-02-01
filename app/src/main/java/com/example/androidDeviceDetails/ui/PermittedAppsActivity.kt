package com.example.androidDeviceDetails.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityPermittedAppsBinding
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData

class PermittedAppsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermittedAppsBinding
    private lateinit var controller: ActivityController<PermittedAppsCookedData>

    companion object {
        const val NAME = "permittedApps"
        var PERMISSION = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permitted_apps)
        this.title = intent.getStringExtra("permission").toString();
        PERMISSION = intent.getStringExtra("permission").toString()
        controller = ActivityController(NAME, binding, this, null, supportFragmentManager)
        binding.tabLayout.setTabTextColors(
            ContextCompat.getColor(this, R.color.white),
            ContextCompat.getColor(this, R.color.white)
        )
    }
}