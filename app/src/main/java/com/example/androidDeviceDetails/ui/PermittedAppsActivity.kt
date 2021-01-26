package com.example.androidDeviceDetails.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.PermissionsListAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityPermittedAppsBinding
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppList

class PermittedAppsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermittedAppsBinding
    private lateinit var controller: ActivityController<PermittedAppList>

    companion object {
        const val NAME = "permittedapps"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permitted_apps)
        this.setTitle(intent.getStringExtra("permission").toString());
        controller = ActivityController(
            NAME+intent.getStringExtra("permission").toString(),
            binding,
            this,
            null,
            supportFragmentManager
        )
        Log.d("Tag", intent.getStringExtra("permission").toString())
        binding.apply {
            permittedAppsListView.isEnabled = true
        }
    }

    private fun showApps(parent: AdapterView<*>, position: Int) {
        val adapter = parent.adapter as PermissionsListAdapter
        val item = adapter.getItem(position)
        val infoIntent = Intent(Settings.ACTION_PRIVACY_SETTINGS)
        intent.putExtra("permission", item)
        startActivity(infoIntent)
    }
}