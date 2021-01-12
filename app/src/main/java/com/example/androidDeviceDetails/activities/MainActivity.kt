package com.example.androidDeviceDetails.activities

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.MainActivityAdapter
import com.example.androidDeviceDetails.databinding.ActivityMainBinding
import com.example.androidDeviceDetails.models.CardItem
import com.example.androidDeviceDetails.services.AppService
import com.example.androidDeviceDetails.utils.PrefManager
import com.example.androidDeviceDetails.utils.Utils


const val permissionCode = 200
val permissions: Array<String> =
    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar?.hide()
        requestPermissions()
        if (!PrefManager.createInstance(this).getBoolean(PrefManager.INITIAL_LAUNCH, false)
        ) {
            Utils.addInitialData(this)
            PrefManager.createInstance(this)
                .putBoolean(PrefManager.INITIAL_LAUNCH, true)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(Intent(this, AppService::class.java))
        else startService(Intent(this, AppService::class.java))
        binding.apply {
            binding.appInfo.button.setOnClickListener(this@MainActivity)
            binding.batteryInfo.button.setOnClickListener(this@MainActivity)
            binding.toLocationActivity.button.setOnClickListener(this@MainActivity)
            binding.appData.button.setOnClickListener(this@MainActivity)
            binding.signal.button.setOnClickListener(this@MainActivity)
        }
        val icons = intArrayOf(
            R.drawable.ic_twotone_system_update_24,
            R.drawable.ic_round_battery_std_24,
            R.drawable.ic_twotone_location_on_24,
            R.drawable.ic_round_data_usage_24,
            R.drawable.ic_twotone_cell_wifi_24,
        )
        val iconsName = arrayOf(
            "App Info",
            "Google Drive",
            "Facebook",
            "Twitter",
            "Google Maps",
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
        }
    }

    private fun requestPermissions() =
        ActivityCompat.requestPermissions(this, permissions, permissionCode)

}