package com.example.androidDeviceDetails.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.view.isVisible
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
    private var isInitialised = false
    private var cellularStrength: Int = -100
    private var wifiStrength: Int = -80
    private var linkspeed: String = "0 Mbps"
    private var cellInfoType: String = "LTE"
    private var strength: Int = 0
    private var text: String = ""
    private var value: String = ""
    private var signal: Int = Signal.CELLULAR.ordinal
    private lateinit var cellularList: ArrayList<SignalEntity>
    private lateinit var wifiList: ArrayList<SignalEntity>
    private val db = RoomDB.getDatabase()!!

    init {
        observeSignal()
    }

    private fun observeSignal() {
        db.signalDao().getLastLive().observe(signalBinding.lifecycleOwner!!) {
            updateValue(it)
        }
    }

    private fun initialView() {
        if (cellularList.isNotEmpty()) {
            cellularStrength = cellularList.last().strength
            cellInfoType = cellularList.last().attribute
        }
        if (wifiList.isNotEmpty()) {
            wifiStrength = wifiList.last().strength
            linkspeed = "${wifiList.last().attribute} Mbps"
        }
        updateCardView()
        isInitialised = true
    }

    @SuppressLint("SetTextI18n")
    fun updateValue(signalEntity: SignalEntity) {
        when (signal) {
            Signal.WIFI.ordinal -> {
                wifiStrength = signalEntity.strength
                linkspeed = "${signalEntity.attribute} Mbps"
            }
            Signal.CELLULAR.ordinal -> {
                cellularStrength = signalEntity.strength
                cellInfoType = signalEntity.attribute
            }
        }
        if (signalEntity.signal == signal)
            updateCardView()
    }

    @SuppressLint("SetTextI18n")
    fun updateCardView() {
        when (signal) {
            Signal.WIFI.ordinal -> {
                strength = wifiStrength
                text = "Linkspeed"
                value = linkspeed
            }
            Signal.CELLULAR.ordinal -> {
                strength = cellularStrength
                text = "CellInfo Type"
                value = cellInfoType
            }
        }
        signalBinding.gauge.moveToValue(strength.toFloat())
        //signalStrengthBinding.gauge.setLowerText(strength.toString())
        signalBinding.textStrength.text = "$strength dBm"
        signalBinding.signalText.text = text
        signalBinding.signalValue.text = value
    }


    @SuppressLint("SetTextI18n")
    private fun updateListHeading() {
        signalBinding.display.isVisible = false
        signalBinding.list.isVisible = true
        signalBinding.listView.isVisible = true
        when (signal) {
            Signal.WIFI.ordinal -> signalBinding.attribute.text = "Type"
            Signal.CELLULAR.ordinal -> signalBinding.attribute.text = "Linkspeed"
        }
    }

    override fun <T> onData(outputList: ArrayList<T>) {
        wifiList = arrayListOf()
        cellularList = arrayListOf()
        val signalList = outputList as ArrayList<SignalEntity>
        if (signalList.isNotEmpty()) {
            for (signal in signalList) {
                if (signal.signal == Signal.CELLULAR.ordinal)
                    cellularList.add(signal)
                else
                    wifiList.add(signal)
            }
        }
        updateListView()
        if (!isInitialised)
            initialView()
    }

    override fun display(filter: Int) {
        signal = filter
        updateGauge()
        updateCardView()
        updateListView()
    }

    private fun updateListView() {
        val signalList =
            if (signal == Signal.CELLULAR.ordinal)
                cellularList
            else
                wifiList
        if (signalList.isNotEmpty()) {
            signalBinding.root.post {
                val adapter =
                    SignalAdapter(
                        context,
                        R.layout.signal_tile,
                        signalList
                    )
                updateListHeading()
                signalBinding.listView.adapter = adapter
            }
        } else
            signalBinding.root.post {
                signalBinding.listView.isVisible = false
                signalBinding.display.isVisible = true
                signalBinding.list.isVisible = false
            }
    }

    private fun updateGauge() {
        when (signal) {
            Signal.WIFI.ordinal -> {
                signalBinding.gauge.setMaxValue(0f)
                signalBinding.gauge.setMinValue(-100f)
            }
            Signal.CELLULAR.ordinal -> {
                signalBinding.gauge.setMaxValue(-50f)
                signalBinding.gauge.setMinValue(-150f)
            }
        }
    }
}