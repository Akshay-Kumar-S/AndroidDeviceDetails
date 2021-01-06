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
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.signalModels.SignalEntity
import com.example.androidDeviceDetails.utils.Signal

class SignalViewModel(
    private val signalBinding: ActivitySignalStrengthBinding,
    val context: Context
) : BaseViewModel() {
    private var cellularStrength: Int = -100
    private var wifiStrength: Int = -80
    private var linkspeed: String = "0 Mbps"
    private var cellInfoType: String = "LTE"
    private var strength: Int = 0
    private var text: String = ""
    private var value: String = ""

    private var db = RoomDB.getDatabase()!!


    @SuppressLint("SetTextI18n")
    fun updateView(signalEntity: SignalEntity) {
        if (signalEntity.signal == Signal.WIFI.ordinal) {
            wifiStrength = signalEntity.strength
            linkspeed = "${signalEntity.attribute} Mbps"
        } else {
            cellularStrength = signalEntity.strength
            cellInfoType = signalEntity.attribute
        }
        updateCard()
    }

    @SuppressLint("SetTextI18n")
    fun updateCard() {
        Log.d("upd", "updateCard: ")
        if ( signalBinding.bottomNavigationView.selectedItemId == R.id.wifi) {
            strength = wifiStrength
            text = "Linkspeed"
            value = linkspeed
        } else {
            strength = cellularStrength
            text = "CellInfo Type"
            value = cellInfoType
        }
        signalBinding.gauge.moveToValue(strength.toFloat())
        //signalStrengthBinding.gauge.setLowerText(strength.toString())
        signalBinding.textStrength.text = "$strength dBm"
        signalBinding.signalText.text = text
        signalBinding.signalValue.text = value
    }

    fun observeSignal(lifecycleOwner: LifecycleOwner) {
        db.signalDao().getLastLive().observe(lifecycleOwner) {
            updateView(it)
        }
    }

    @SuppressLint("SetTextI18n")
    fun displayList() {
        signalBinding.display.isVisible = false
        signalBinding.list.isVisible = true
        signalBinding.listView.isVisible = true
        if (signalBinding.bottomNavigationView.selectedItemId == R.id.cellular)
            signalBinding.general.text = "Type"
        else
            signalBinding.general.text = "Linkspeed"
    }

    override fun <T> onData(outputList: ArrayList<T>) {
        if (outputList.isNotEmpty()) {
            signalBinding.root.post {
                displayList()
                val adapter =
                    SignalAdapter(
                        context,
                        R.layout.signal_tile,
                        outputList as ArrayList<SignalEntity>
                    )
                signalBinding.listView.adapter = adapter
            }
        } else
            signalBinding.root.post {
                signalBinding.listView.isVisible = false
                signalBinding.display.isVisible = true
                signalBinding.list.isVisible = false
            }
    }

}