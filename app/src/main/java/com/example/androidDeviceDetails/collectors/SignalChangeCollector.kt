package com.example.androidDeviceDetails.collectors

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.*
import android.telephony.PhoneStateListener.LISTEN_NONE
import android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.collectors.SignalChangeCollector.SignalChangeListener
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.database.SignalRaw
import com.example.androidDeviceDetails.models.signal.Signal
import com.example.androidDeviceDetails.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
            val signalDB = RoomDB.getDatabase()
            var level = 0
            var strength = Signal.CELLULAR_MIN
            var band = "unknown"
            var networkBand = "unknown"
            val strengthPercentage: Float
            val telephonyManager =
                DeviceDetailsApplication.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (signalStrength.cellSignalStrengths.isNotEmpty())
                    when (val cellInfo = signalStrength.cellSignalStrengths[0]) {
                        is CellSignalStrengthLte -> {
                            strength = cellInfo.rsrp
                            level = cellInfo.level
                            band = "LTE"
                            networkBand = "4g"
                        }
                        is CellSignalStrengthGsm -> {
                            strength = cellInfo.dbm
                            level = cellInfo.level
                            band = "GSM"
                            networkBand = "2g"
                        }
                        is CellSignalStrengthCdma -> {
                            strength = cellInfo.cdmaDbm
                            band = "CDMA"
                            level = cellInfo.level
                            networkBand = "3g"
                        }
                        is CellSignalStrengthWcdma -> {
                            strength = cellInfo.dbm
                            band = "WCDMA"
                            level = cellInfo.level
                            networkBand = "3g"
                        }
                        is CellSignalStrengthNr -> {
                            strength = cellInfo.csiRsrp
                            band = "NR"
                            level = cellInfo.level
                            networkBand = "5g"
                        }
                        is CellSignalStrengthTdscdma -> {
                            strength = cellInfo.dbm
                            band = "TDSCDMA"
                            level = cellInfo.level
                            networkBand = "3g"
                        }
                    }
            } else {
                if (DeviceDetailsApplication.instance.checkCallingOrSelfPermission("android.Manifest.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED)
                    if (telephonyManager.allCellInfo.isNotEmpty())
                        when (val cellInfo = telephonyManager.allCellInfo[0]) {
                            is CellInfoLte -> {
                                band = "LTE"
                                strength = cellInfo.cellSignalStrength.dbm
                                level = cellInfo.cellSignalStrength.level
                                networkBand = "4g"
                            }
                            is CellInfoGsm -> {
                                band = "GSM"
                                strength = cellInfo.cellSignalStrength.dbm
                                level = cellInfo.cellSignalStrength.level
                                networkBand = "2g"
                            }
                            is CellInfoCdma -> {
                                band = "CDMA"
                                strength = cellInfo.cellSignalStrength.dbm
                                level = cellInfo.cellSignalStrength.level
                                networkBand = "3g"
                            }
                            is CellInfoWcdma -> {
                                band = "WCDMA"
                                strength = cellInfo.cellSignalStrength.dbm
                                level = cellInfo.cellSignalStrength.level
                                networkBand = "3g"
                            }
                        }
            }

            strengthPercentage = (Signal.CELLULAR_MIN - strength) / Signal.CELLULAR_RANGE.toFloat() * (-100)
            val signalRaw = SignalRaw(
                timeStamp = System.currentTimeMillis(),
                signal = Signal.CELLULAR,
                strength = strength,
                cellInfoType = band,
                linkSpeed = null,
                level = Utils.getSignalLevel(level),
                operatorName = telephonyManager.networkOperatorName,
                isRoaming = telephonyManager.isNetworkRoaming,
                band = networkBand,
                strengthPercentage = strengthPercentage
            )
            GlobalScope.launch { signalDB?.signalDao()?.insert(signalRaw) }
        }
    }

    /**
     * Registers the [SignalChangeListener] with [LISTEN_SIGNAL_STRENGTHS].
     **/
    override fun start() {
        mTelephonyManager.listen(SignalChangeListener, LISTEN_SIGNAL_STRENGTHS)
    }

    /**
     * Unregisters the [SignalChangeListener] with [LISTEN_NONE].
     **/
    override fun stop() {
        mTelephonyManager.listen(SignalChangeListener, LISTEN_NONE)
    }
}