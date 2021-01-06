package com.example.androidDeviceDetails.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.SignalAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivitySignalStrengthBinding
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.SignalRaw
import com.example.androidDeviceDetails.models.WifiRaw

class SignalViewModel(
    private val signalStrengthBinding: ActivitySignalStrengthBinding,
    val context: Context
) : BaseViewModel() {
    private var strength: Int = -100
    private var linkspeed: String = "0 MHz"
    private var cellInfoType: String = "LTE"
    private var db = RoomDB.getDatabase()!!

    @SuppressLint("SetTextI18n")
    fun updateWifiGauge(wifiRaw: WifiRaw) {
        Log.d("test", "updateWifiGauge: ")
        strength = wifiRaw.strength!!
        linkspeed = wifiRaw.linkSpeed.toString()
        signalStrengthBinding.gauge.moveToValue(strength.toFloat())
        //signalStrengthBinding.gauge.setLowerText(strength.toString())
        signalStrengthBinding.textStrength.text = "$strength dBm"
        signalStrengthBinding.signalText.text = "LinkSpeed"
        signalStrengthBinding.signalValue.text = "$linkspeed MHz"
    }

    @SuppressLint("SetTextI18n")
    fun updateCellularGauge(cellularRaw: CellularRaw) {
        Log.d("tag", "updateCellularGauge: ")
        strength = cellularRaw.strength!!
        cellInfoType = cellularRaw.type.toString()
        signalStrengthBinding.gauge.moveToValue(strength.toFloat())
        //signalStrengthBinding.gauge.setLowerText(strength.toString())
        signalStrengthBinding.textStrength.text = "$strength dBm"
        signalStrengthBinding.signalText.text = "Type"
        signalStrengthBinding.signalValue.text = cellInfoType
    }

    fun updateGauge(max: Float, min: Float) {
        signalStrengthBinding.gauge.setMaxValue(max)
        signalStrengthBinding.gauge.setMinValue(min)
    }

    fun observeSignal(lifecycleOwner: LifecycleOwner) {
        db.cellularDao().getLastLive().observe(lifecycleOwner) {
            if (signalStrengthBinding.bottomNavigationView.selectedItemId == R.id.cellularStrength)
                updateCellularGauge(it)
        }
        db.wifiDao().getLastLive().observe(lifecycleOwner) {
            if (signalStrengthBinding.bottomNavigationView.selectedItemId == R.id.wifiStrength)
                updateWifiGauge(it)
        }
    }

    @SuppressLint("SetTextI18n")
    fun displayList() {
        signalStrengthBinding.display.isVisible = false
        signalStrengthBinding.list.isVisible = true
        signalStrengthBinding.listView.isVisible = true
        if (signalStrengthBinding.bottomNavigationView.selectedItemId == R.id.cellularStrength)
            signalStrengthBinding.general.text = "Type"
        else
            signalStrengthBinding.general.text = "Linkspeed"
    }

    override fun <T> onData(outputList: ArrayList<T>) {
        if (outputList.isNotEmpty()) {
            signalStrengthBinding.root.post {
                displayList()
                val adapter =
                    SignalAdapter(
                        context,
                        R.layout.signal_tile,
                        outputList as ArrayList<SignalRaw>
                    )
                signalStrengthBinding.listView.adapter = adapter
            }
        } else
            signalStrengthBinding.root.post {
                signalStrengthBinding.listView.isVisible = false
                signalStrengthBinding.display.isVisible = true
                signalStrengthBinding.list.isVisible = false
            }
    }

}