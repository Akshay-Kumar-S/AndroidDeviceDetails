package com.example.androidDeviceDetails.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.view.isVisible
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.ui.SignalActivity
import com.example.androidDeviceDetails.adapters.SignalListAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.cooker.SignalCooker
import com.example.androidDeviceDetails.databinding.ActivitySignalBinding
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.signalModels.SignalRaw
import com.example.androidDeviceDetails.utils.Signal

/**
 * Implements [BaseViewModel]
 */
class SignalViewModel(
    private val signalBinding: ActivitySignalBinding,
    val context: Context
) : BaseViewModel() {
    private var isInitialised: Boolean = false
    private var cellularStrength: Int = -100
    private var wifiStrength: Int = -80
    private var linkspeed: String = "0 Mbps"
    private var cellInfoType: String = "LTE"
    private var strength: Int = 0
    private var text: String = ""
    private var value: String = ""
    private var signal: Int = Signal.CELLULAR.ordinal
    private lateinit var cellularList: ArrayList<SignalRaw>
    private lateinit var wifiList: ArrayList<SignalRaw>
    private val db = RoomDB.getDatabase()!!

    init {
        observeSignal()
    }

    /**
     * This method is called on the initialisation of the [SignalViewModel].
     * It is used to observe the last live data of [SignalRaw].
     * And on notification, updates values via [updateValue].
     */
    private fun observeSignal() {
        db.signalDao().getLastLive().observe(signalBinding.lifecycleOwner!!) {
            if (it != null) updateValue(it)
        }
    }

    /**
     * This method is called only initially to prepopulate the card view.
     */
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

    /**
     * This method updates the values of wifi strength, link speed,
     * cellular strength and cell info type upon call from [observeSignal].
     */
    @SuppressLint("SetTextI18n")
    fun updateValue(signalRaw: SignalRaw) {
        when (signalRaw.signal) {
            Signal.WIFI.ordinal -> {
                wifiStrength = signalRaw.strength
                linkspeed = "${signalRaw.attribute} Mbps"
            }
            Signal.CELLULAR.ordinal -> {
                cellularStrength = signalRaw.strength
                cellInfoType = signalRaw.attribute
            }
        }
        if (signalRaw.signal == signal)
            updateCardView()
    }

    /**
     * This method updates the Card view in the UI based on the selected menu - CELLULAR or WIFI.
     */
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

    /**
     * This method is called once the [SignalCooker] finishes cooking.
     * This method separates the cooked data into CELLULAR list and WIFI list
     * and calls [updateListView] to update list.
     * >
     * Overrides : [onDone] in [BaseViewModel].
     * @param outputList List of cooked data.
     */
    override fun <T> onDone(outputList: ArrayList<T>) {
        wifiList = arrayListOf()
        cellularList = arrayListOf()
        val signalList = outputList as ArrayList<SignalRaw>
        if (signalList.isNotEmpty()) {
            for (signal in signalList) {
                when (signal.signal) {
                    Signal.WIFI.ordinal -> wifiList.add(signal)
                    Signal.CELLULAR.ordinal -> cellularList.add(signal)
                }
            }
        }
        updateListView()
    }

    /**
     * This method is called whenever the menu is chosen from the [SignalActivity].
     * Depending on the menu chosen - WIFI or CELLULAR, respective gauge, card and list
     * views are updated.
     * @param type to indicate which signal menu is chosen - CELLULAR or WIFI.
     */
    override fun filter(type: Int) {
        signal = type
        updateGauge()
        updateCardView()
        updateListView()
    }

    /**
     * This method updates List in the UI based on the selected menu.
     * If the selected menu is CELLULAR, displays list of CELLULAR signal values.
     * And if the selected menu is WIFI, displays list of WIFI signal values.
     */
    private fun updateListView() {
        val signalList =
            if (signal == Signal.CELLULAR.ordinal)
                cellularList
            else
                wifiList
        if (signalList.isNotEmpty()) {
            signalBinding.root.post {
                val adapter =
                    SignalListAdapter(
                        context,
                        R.layout.signal_tile,
                        signalList
                    )
                signalBinding.display.isVisible = false
                signalBinding.listView.isVisible = true
                signalBinding.listView.adapter = adapter
                if (!isInitialised)
                    initialView()
            }
        } else
            signalBinding.root.post {
                signalBinding.listView.isVisible = false
                signalBinding.display.isVisible = true
            }
    }

    /**
     * This method sets the maximum and minimum values of the gauge depending on whether the
     * selected signal to be displayed is CELLULAR or WIFI.
     */
    private fun updateGauge() {
        when (signal) {
            Signal.WIFI.ordinal -> {
                signalBinding.gauge.setMaxValue(0f)
                signalBinding.gauge.setMinValue(-127f)
            }
            Signal.CELLULAR.ordinal -> {
                signalBinding.gauge.setMaxValue(-50f)
                signalBinding.gauge.setMinValue(-150f)
            }
        }
    }
}