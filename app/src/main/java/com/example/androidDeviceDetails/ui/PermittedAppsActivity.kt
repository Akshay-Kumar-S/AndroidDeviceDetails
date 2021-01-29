package com.example.androidDeviceDetails.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.PermittedAppsListAdapter
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityPermittedAppsBinding
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppList

class PermittedAppsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermittedAppsBinding
    private lateinit var controller: ActivityController<PermittedAppList>

    companion object {
        const val NAME = "permittedApps"
        var PERMISSION = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permitted_apps)
        this.title = intent.getStringExtra("permission").toString();
        PERMISSION = intent.getStringExtra("permission").toString()
        controller = ActivityController(
            NAME,
            binding,
            this,
            null,
            supportFragmentManager
        )
        Log.d("Tag", intent.getStringExtra("permission").toString())
        binding.apply {
            permittedAppsListView.setOnItemClickListener { parent, _, position, _ ->
                showApps(parent, position)
            }
            permittedAppsListView.isEnabled = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.permitted_apps_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val title = findViewById<TextView>(R.id.filter_text)
        var filter = 1
        when (item.itemId) {
            R.id.allowed -> {
                title.text = "Allowed"
                filter = 1
            }
            R.id.denied -> {
                title.text = "Denied"
                filter = 0
            }
            else -> super.onSupportNavigateUp()
        }
        controller.filterView(filter)

        return true
    }

    private fun showApps(parent: AdapterView<*>, position: Int) {
        val adapter = parent.adapter as PermittedAppsListAdapter
        val item = adapter.getItem(position)
        val infoIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        infoIntent.addCategory(Intent.CATEGORY_DEFAULT)
        infoIntent.data = Uri.parse("package:${item?.package_name}")
        startActivity(infoIntent)
    }
}