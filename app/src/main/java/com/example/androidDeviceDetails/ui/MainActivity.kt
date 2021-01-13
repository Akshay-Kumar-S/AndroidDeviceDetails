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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.controller.ActivityController
import com.example.androidDeviceDetails.adapters.MainActivityAdapter
import com.example.androidDeviceDetails.databinding.ActivityMainBinding
import com.example.androidDeviceDetails.models.appInfoModels.AppInfoCookedData
import com.example.androidDeviceDetails.models.CardItem
import com.example.androidDeviceDetails.services.AppService
import com.example.androidDeviceDetails.utils.PrefManager
import com.example.androidDeviceDetails.utils.Utils


const val permissionCode = 200
val permissions: Array<String> =
    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val NAME = "Main Activity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: ActivityController<AppInfoCookedData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar?.hide()
        requestPermissions()
        if (!PrefManager.createInstance(this).getBoolean(PrefManager.INITIAL_LAUNCH, false)) {
            Utils.addInitialData(this)
            PrefManager.createInstance(this).putBoolean(PrefManager.INITIAL_LAUNCH, true)
        }
        binding.apply {
            binding.appInfo.button.setOnClickListener(this@MainActivity)
            binding.batteryInfo.button.setOnClickListener(this@MainActivity)
            binding.toLocationActivity.button.setOnClickListener(this@MainActivity)
            binding.appData.button.setOnClickListener(this@MainActivity)
            binding.signal.button.setOnClickListener(this@MainActivity)
        }
        val icons = intArrayOf(
            R.drawable.app_info,
            R.drawable.battery,
            R.drawable.route,
            R.drawable.database,
            R.drawable.wifi,
        )
        val iconsName = arrayOf(
            "App Info",
            "Battery Usage",
            "Location",
            "Network Usage",
            "Signal Data",
        )

        val recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        val arrayList  = arrayListOf<CardItem>()

        recyclerView.layoutManager = LinearLayoutManager(
            applicationContext,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.itemAnimator = DefaultItemAnimator()

        for (i in icons.indices) {
            val itemModel = CardItem()
            itemModel.image = icons[i]
            itemModel.name = iconsName[i]

            //add in array list
            arrayList.add(itemModel)
        }

        val adapter = MainActivityAdapter(applicationContext, arrayList)
        recyclerView.adapter = adapter
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.toLocationActivity -> startActivity(Intent(this, LocationActivity::class.java))
            R.id.batteryInfo -> startActivity(Intent(this, BatteryActivity::class.java))
            R.id.appInfo -> startActivity(Intent(this, AppInfoActivity::class.java))
            R.id.appData -> startActivity(Intent(this, NetworkUsageActivity::class.java))
            R.id.signal -> startActivity(Intent(this, SignalActivity::class.java))
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