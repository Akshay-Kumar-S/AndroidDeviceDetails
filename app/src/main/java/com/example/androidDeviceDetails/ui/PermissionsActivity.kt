package com.example.androidDeviceDetails.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.PermissionsAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityPermissionsBinding

class PermissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionsBinding
    private lateinit var controller: ActivityController<String>

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
        val adapter = parent.adapter as PermissionsAdapter
        val item = adapter.getItem(position)
        val intent = Intent(this@PermissionsActivity, PermittedAppsActivity::class.java)
        intent.putExtra("permission", item)
        startActivity(intent)
    }

}