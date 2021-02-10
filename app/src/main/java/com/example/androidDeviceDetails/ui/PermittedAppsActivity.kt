package com.example.androidDeviceDetails.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.PermissionsListAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityPermittedAppsBinding
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData

class PermittedAppsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermittedAppsBinding
    private lateinit var controller: ActivityController<PermittedAppsCookedData>

    companion object {
        const val NAME = "permittedapps"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permitted_apps)
        controller = ActivityController(
            NAME,
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
//        infoIntent.addCategory(Intent.CATEGORY_DEFAULT)
//        infoIntent.data = Uri.parse("package:${item?.get(position)}")
        startActivity(infoIntent)
    }
}