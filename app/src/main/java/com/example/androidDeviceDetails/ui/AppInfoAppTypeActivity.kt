package com.example.androidDeviceDetails.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityAppInfoAppTypeBinding
import com.example.androidDeviceDetails.models.appInfo.appType.FilterType
import com.example.androidDeviceDetails.models.database.AppInfoRaw
import com.example.androidDeviceDetails.utils.Utils


class AppInfoAppTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppInfoAppTypeBinding
    private lateinit var controller: ActivityController<AppInfoRaw>

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_info_app_type_menu, menu)
        return true
    }

    companion object {
        const val NAME = "appType"
    }

    @SuppressLint("SetTextI18n")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val title = findViewById<TextView>(R.id.filterAppType)
        var filter = 0
        when (item.itemId) {
            R.id.appTypeAll -> {
                title.text = "All"
                filter = FilterType.ALL.ordinal
            }
            R.id.appTypeUser -> {
                title.text = "User"
                filter = FilterType.USER.ordinal

            }
            R.id.appTypeSystem -> {
                title.text = "System"
                filter = FilterType.SYSTEM.ordinal
            }
            else -> super.onSupportNavigateUp()
        }
        controller.filterView(filter)

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_info_app_type)
        controller = ActivityController(
            NAME,
            binding,
            this,
            null,
            supportFragmentManager
        )
        binding.appTypeListView.isEnabled = true

    }

    fun deleteApp(view: View) {
        Utils.uninstallApp(view.tag as String, packageManager, this)
    }


}