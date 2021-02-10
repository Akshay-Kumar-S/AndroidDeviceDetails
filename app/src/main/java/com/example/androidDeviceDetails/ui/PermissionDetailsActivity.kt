package com.example.androidDeviceDetails.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.databinding.ActivityPermittedAppsBinding
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.example.androidDeviceDetails.viewModel.PermissionsDetailsViewModel
import com.google.gson.Gson

class PermissionDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermittedAppsBinding

    companion object {
        const val NAME = "permittedApps"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permitted_apps)
        this.title = intent.getStringExtra("permission").toString()
        val appList = Gson().fromJson(
            intent.getStringExtra("appList").toString(),
            Array<PermittedAppsCookedData>::class.java
        )
        binding.tabLayout.setTabTextColors(
            ContextCompat.getColor(this, R.color.labelBackGround),
            ContextCompat.getColor(this, R.color.white)
        )

        PermissionsDetailsViewModel(binding, this).onDone(appList)
    }
}