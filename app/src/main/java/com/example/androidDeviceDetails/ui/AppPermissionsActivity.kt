package com.example.androidDeviceDetails.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.PermissionsAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityPermissionsBinding
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppsCookedData
import com.example.androidDeviceDetails.viewModel.PermissionsViewModel
import com.google.gson.Gson

class AppPermissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionsBinding
    private lateinit var controller: ActivityController<String>
    private lateinit var appPermissionsViewModel: PermissionsViewModel

    companion object {
        const val NAME = "permissions"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permissions)
        controller = ActivityController(
            NAME,
            binding,
            this,
            null,
            supportFragmentManager
        )
        binding.apply {
            permissionsListView.isEnabled = true
            permissionsListView.setOnItemClickListener { parent, _, position, _ ->
                showApps(parent, position)
            }
        }
    }

    private fun showApps(parent: AdapterView<*>, position: Int) {
        appPermissionsViewModel = controller.viewModel as PermissionsViewModel
        val adapter = parent.adapter as PermissionsAdapter
        val item = adapter.getItem(position).toString()
        val appList = getAppPermissionsAppList(appPermissionsViewModel, item)
        val signalJson = Gson().toJson(appList)
        val intent = Intent(this@AppPermissionsActivity, PermittedAppsActivity::class.java)
        intent.putExtra("permission", item)
        intent.putExtra("appList", signalJson)
        startActivity(intent)
    }

    private fun getAppPermissionsAppList(
        appPermissionsViewModel: PermissionsViewModel,
        permission: String
    ): ArrayList<PermittedAppsCookedData> {
        var appList = arrayListOf<PermittedAppsCookedData>()
        for (id in appPermissionsViewModel.permittedAppList) {
            if (id.allowed_permissions.contains(permission) && !id.denied_permissions.contains(
                    permission
                )
            ) {
                appList.add(
                    PermittedAppsCookedData(
                        id.package_name,
                        id.apk_title,
                        id.version_name,
                        true
                    )
                )
            }
            if (id.denied_permissions.contains(permission)) {
                appList.add(
                    PermittedAppsCookedData(
                        id.package_name,
                        id.apk_title,
                        id.version_name,
                        false
                    )
                )
            }
        }
        return appList
    }
}