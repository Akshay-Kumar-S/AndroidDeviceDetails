package com.example.androidDeviceDetails.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.RecyclerItemClickListener
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityMainBinding
import com.example.androidDeviceDetails.models.MainActivityCookedData
import com.example.androidDeviceDetails.services.AppService
import com.example.androidDeviceDetails.utils.PrefManager
import com.example.androidDeviceDetails.utils.Utils

const val permissionCode = 200
val permissions: Array<String> =
    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)

class MainActivity : AppCompatActivity() {
    companion object {
        const val NAME = "Main Activity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainActivityController: ActivityController<MainActivityCookedData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainActivityController = ActivityController(
            NAME, binding, this,
            null, supportFragmentManager = supportFragmentManager
        )
        supportActionBar?.hide()
        requestPermissions()
        if (!PrefManager.createInstance(this).getBoolean(PrefManager.INITIAL_LAUNCH, false)) {
            Utils.addInitialData(this)
            PrefManager.createInstance(this).putBoolean(PrefManager.INITIAL_LAUNCH, true)
        }

        binding.recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                binding.recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    val cardsTitles = resources.getStringArray(R.array.card_names)
                    override fun onItemClick(view: View, position: Int) {
                        when (view.findViewById<TextView>(R.id.cardTitle).text) {
                            cardsTitles[0] -> startActivity(
                                Intent(
                                    this@MainActivity,
                                    AppInfoActivity::class.java
                                )
                            )
                            cardsTitles[1] -> startActivity(
                                Intent(
                                    this@MainActivity,
                                    BatteryActivity::class.java
                                )
                            )
                            cardsTitles[2] -> startActivity(
                                Intent(
                                    this@MainActivity,
                                    NetworkUsageActivity::class.java
                                )
                            )
                        }
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                        TODO("do nothing")
                    }
                })
        )
        binding.pullToRefresh.setProgressBackgroundColorSchemeResource(R.color.app_green)
        binding.pullToRefresh.setOnRefreshListener {
            mainActivityController.refreshCooker()

            binding.pullToRefresh.isRefreshing = false
        }
    }

    private fun requestPermissions() =
        ActivityCompat.requestPermissions(this, permissions, permissionCode)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED)
            Toast.makeText(this, "Permission required", Toast.LENGTH_SHORT).show()
        else {
            if (!Utils.isUsageAccessGranted(this))
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(Intent(this, AppService::class.java))
            else startService(Intent(this, AppService::class.java))
        }
    }

}