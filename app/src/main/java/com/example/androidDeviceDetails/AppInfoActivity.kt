package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidDeviceDetails.adapters.AppInfoListAdapter
import com.example.androidDeviceDetails.adapters.BatteryListAdapter
import com.example.androidDeviceDetails.databinding.ActivityAppInfoBinding
import com.example.androidDeviceDetails.managers.AppStateCooker
import com.example.androidDeviceDetails.models.AppInfoCookedData
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.services.CollectorService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class AppInfoActivity : AppCompatActivity() {


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(Intent(this, CollectorService::class.java))
        } else {
            this.startService(Intent(this, CollectorService::class.java))
        }

        val binding: ActivityAppInfoBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_app_info)
        var appList: List<AppInfoCookedData>
        val today: Calendar = Calendar.getInstance()
        binding.datePicker.init(
            today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )
        { _, year, month, day ->
            today.set(year, month, day)
            today[Calendar.HOUR_OF_DAY] = 0
            today[Calendar.MINUTE] = 0
            today[Calendar.SECOND] = 0
            val startTime = today.timeInMillis
            val endTime = startTime + (((((23 * 60) + 59) * 60) + 59) * 1000)
            var swapText = ""
            GlobalScope.launch(Dispatchers.IO) {
                appList = AppStateCooker.createInstance()
                    .getAppsBetween(startTime, endTime, applicationContext)
                val db = RoomDB.getDatabase(applicationContext)!!
                for (app in appList) {
                    app.packageName = db.appsDao().getPackageByID(app.appId)
                }
                appList = appList.sortedBy { it.appName }
                binding.appInfoListView.post {
                    binding.appInfoListView.adapter =
                        AppInfoListAdapter(applicationContext, R.layout.appinfo_tile, appList)
                }


            }
        }
    }


}