package com.example.androidDeviceDetails.collectors

import android.content.Context
import android.os.Build
import android.telephony.*
import android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.signalModels.SignalRaw
import com.example.androidDeviceDetails.utils.Signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.telephony.PhoneStateListener.LISTEN_NONE

/**
 *  Implements [BaseCollector].
 *  An event based collector which collects the CELLULAR signal data.
 *  Contains a [PhoneStateListener] : [SignalChangeListener] which is registered on
 *  initialization of this class.
 *  This listener requires [android.Manifest.permission.ACCESS_FINE_LOCATION] permission.
 **/
class SignalChangeCollector : BaseCollector() {

    private var mTelephonyManager: TelephonyManager =
        DeviceDetailsApplication.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    /**
     * A [PhoneStateListener] which gets notified from [LISTEN_SIGNAL_STRENGTHS]
     **/
    object SignalChangeListener : PhoneStateListener() {
        /**
         * Listener which gets notified when a change in signal strength occurs.
         *  Method is called when the strength of signal changes.
         *  Listener collects current timestamp, signal, strength, cellInfo type and level.
         *  These values are made into a [SignalRaw] and saved into the [RoomDB.signalDao].
         *  This listener requires [android.Manifest.permission.ACCESS_FINE_LOCATION] permission.
         **/
        override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
            val signalRaw: SignalRaw
            var level = 0
            var strength = 0
            var type = ""
            val signalDB = RoomDB.getDatabase()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                when (val cellInfo = signalStrength.cellSignalStrengths[0]) {
                    is CellSignalStrengthLte -> {
                        strength = cellInfo.rsrp
                        level = cellInfo.level
                        type = "LTE"
                    }
                    is CellSignalStrengthGsm -> {
                        strength = cellInfo.dbm
                        level = cellInfo.level
                        type = "GSM"
                    }
                    is CellSignalStrengthCdma -> {
                        strength = cellInfo.cdmaDbm
                        type = "CDMA"
                        level = cellInfo.level
                    }
                    is CellSignalStrengthWcdma -> {
                        strength = cellInfo.dbm
                        type = "WCDMA"
                        level = cellInfo.level
                    }
                    is CellSignalStrengthNr -> {
                        strength = cellInfo.csiRsrp
                        type = "NR"
                        level = cellInfo.level
                    }
                    is CellSignalStrengthTdscdma -> {
                        strength = cellInfo.dbm
                        type = "TDSCDMA"
                        level = cellInfo.level
                    }
                }
            } else {
                try {
                    val telephonyManager =
                        DeviceDetailsApplication.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    when (val cellInfo = telephonyManager.allCellInfo[0]) {
                        is CellInfoLte -> {
                            type = "LTE"
                            strength = cellInfo.cellSignalStrength.dbm
                            level = cellInfo.cellSignalStrength.level
                        }
                        is CellInfoGsm -> {
                            type = "GSM"
                            strength = cellInfo.cellSignalStrength.dbm
                            level = cellInfo.cellSignalStrength.level
                        }
                        is CellInfoCdma -> {
                            type = "CDMA"
                            strength = cellInfo.cellSignalStrength.dbm
                            level = cellInfo.cellSignalStrength.level
                        }
                        is CellInfoWcdma -> {
                            type = "WCDMA"
                            strength = cellInfo.cellSignalStrength.dbm
                            level = cellInfo.cellSignalStrength.level
                        }
                    }
                } catch (e: SecurityException) {
                }
            }
            signalRaw = SignalRaw(
                System.currentTimeMillis(), Signal.CELLULAR.ordinal, strength, type, level
            )
            GlobalScope.launch {
                signalDB?.signalDao()?.insertAll(signalRaw)
            }
        }
    }

    init {
        start()
    }

    /**
     * Registers the [SignalChangeListener] with [LISTEN_SIGNAL_STRENGTHS].
     **/
    override fun start() {
        mTelephonyManager.listen(SignalChangeListener, LISTEN_SIGNAL_STRENGTHS)
    }

    override fun collect() {
    }

    /**
     * Unregisters the [SignalChangeListener] with [LISTEN_NONE].
     **/
    override fun stop() {
        mTelephonyManager.listen(SignalChangeListener, LISTEN_NONE)
    }
}