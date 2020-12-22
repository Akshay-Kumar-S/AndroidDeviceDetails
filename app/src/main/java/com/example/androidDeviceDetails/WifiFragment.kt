package com.example.androidDeviceDetails

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.WifiRaw
import de.nitri.gauge.Gauge

class WifiFragment : Fragment() {
    private var db = RoomDB.getDatabase()!!
    private var wifiStrength: Int = -80
    private var linkspeed: Int = 0
    private lateinit var linkspeedText : TextView
    private lateinit var strength: TextView
    lateinit private var gauge: Gauge
    lateinit var view: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_wifi_strength, container, false) as ViewGroup
        gauge = view.findViewById(R.id.wifiGauge)
        strength=view.findViewById(R.id.wifiStrengthText)
        linkspeedText=view.findViewById(R.id.linkspeed)
        updateGauge()
        db.wifiDao().getLastLive().observe(viewLifecycleOwner) {
            updateWifiGauge(it)
        }
        return view
    }

    private fun updateGauge() {
        gauge.moveToValue(wifiStrength.toFloat())
        gauge.setLowerText(linkspeed.toString())
        gauge.setUpperText(wifiStrength.toString())
        strength.text=wifiStrength.toString()
        linkspeedText.text=linkspeed.toString()
    }

    private fun updateWifiGauge(wifiRaw: WifiRaw) {
        Log.d("psy", "updateWifiGauge: ")
        wifiStrength = wifiRaw.strength!!
        linkspeed = wifiRaw.linkSpeed!!
        updateGauge()
    }


}