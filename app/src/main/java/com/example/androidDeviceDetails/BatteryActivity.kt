package com.example.androidDeviceDetails

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.managers.AppBatteryUsageManager
import com.example.androidDeviceDetails.managers.AppEntry


class BatteryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery)

        val batteryListView = findViewById<ListView>(R.id.batteryListView)
//        val items = arrayListOf<AppEntry>()
//        items.add(AppEntry("com.whatsapp", 6))
//        items.add(AppEntry("com.example.Broadband", 4))
//        items.add(AppEntry("com.android.packageinstaller", 4))
//        items.add(AppEntry("com.microsoft.teams", 4))
//        items.add(AppEntry("com.google.android.apps.photos", 4))
        AppBatteryUsageManager().cookBatteryData(this,batteryListView,System.currentTimeMillis()-12*60*60*1000)
    }
}

class BatteryListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: ArrayList<AppEntry>
):ArrayAdapter<AppEntry>(_context, resource, items){
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        val view = layoutInflater.inflate(resource, null)

        val appNameView = view.findViewById<TextView>(R.id.appName)
        val dropTextView = view.findViewById<TextView>(R.id.dropText)
        val appIconView = view.findViewById<ImageView>(R.id.appIcon)

        val packageManager = _context.packageManager

        val info = packageManager.getApplicationInfo(items[position].packageId, PackageManager.GET_META_DATA)
        val appName = packageManager.getApplicationLabel(info) as String
        val appIcon =packageManager.getApplicationIcon(items[position].packageId)


        appNameView.text = appName
        dropTextView.text = items[position].drop.toString()+ "% Used"
        appIconView.setImageDrawable(appIcon)

        return view
    }
}