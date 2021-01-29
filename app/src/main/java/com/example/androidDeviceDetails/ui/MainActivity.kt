package com.example.androidDeviceDetails.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.databinding.ActivityMainBinding
import com.example.androidDeviceDetails.services.AppService
import com.example.androidDeviceDetails.utils.PrefManager
import com.example.androidDeviceDetails.utils.Utils

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val NAME = "Main Activity"
        const val permissionCode = 200
        val permissions: Array<String> =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainActivityController: ActivityController<Any>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainActivityController = ActivityController(this, NAME, binding)
        supportActionBar?.hide()
        requestPermissions()
        if (!PrefManager.createInstance(this).getBoolean(PrefManager.INITIAL_LAUNCH, false)) {
            Utils.addInitialData(this)
            PrefManager.createInstance(this).putBoolean(PrefManager.INITIAL_LAUNCH, true)
        }
        binding.apply {
            binding.appInfo.cardView.setOnClickListener(this@MainActivity)
            binding.batteryInfo.cardView.setOnClickListener(this@MainActivity)
            binding.networkUsage.cardView.setOnClickListener(this@MainActivity)
            binding.locationInfo.cardView.setOnClickListener(this@MainActivity)
            binding.signalData.cardView.setOnClickListener(this@MainActivity)
        }
        swipeToRefresh()
    }

    private fun swipeToRefresh() {
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

    override fun onClick(v: View?) {
        when (v!!) {
            binding.locationInfo.cardView -> navigate(LocationActivity::class.java)
            binding.batteryInfo.cardView -> navigate(BatteryActivity::class.java)
            binding.appInfo.cardView -> navigate(AppEventActivity::class.java)
            binding.networkUsage.cardView -> navigate(NetworkUsageActivity::class.java)
            binding.signalData.cardView -> navigate(SignalActivity::class.java)
        }
    }

    private fun <T> navigate(clazz: Class<T>) {
        startActivity(Intent(this, clazz))
    }
}