package com.example.androidDeviceDetails.receivers

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.*
import android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
import android.util.Log
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.database.RoomDB
import com.example.androidDeviceDetails.database.SignalRaw
import com.example.androidDeviceDetails.models.signal.Signal
import com.example.androidDeviceDetails.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
    @SuppressLint("MissingPermission")
    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
        val signalDB = RoomDB.getDatabase()
        var level = 0
        var strength = Signal.CELLULAR_MIN
        var band = Signal.NO_DATA
        var networkBand = Signal.NO_DATA
        val strengthPercentage: Float
        val telephonyManager =
            DeviceDetailsApplication.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && signalStrength.cellSignalStrengths.isNotEmpty()) {
            val cellInfos = signalStrength.cellSignalStrengths[0]
            val x= cellInfos.dbm
            val y= cellInfos.level
            val z=telephonyManager.voiceNetworkType
            val a = networkTypeClass(z)
            Log.d("zzz", "onSignalStrengthsChanged---------------: $x $y $a")

            when (val cellInfo = signalStrength.cellSignalStrengths[0]) {
                is CellSignalStrengthLte -> {
                    strength = cellInfo.rsrp
                    level = cellInfo.level
                    band = Signal.TYPE_LTE
                    networkBand = Signal.FOURTH_GEN
                }
                is CellSignalStrengthGsm -> {
                    strength = cellInfo.dbm
                    level = cellInfo.level
                    band = Signal.TYPE_GSM
                    networkBand = Signal.SECOND_GEN
                }
                is CellSignalStrengthCdma -> {
                    strength = cellInfo.cdmaDbm
                    band = Signal.TYPE_CDMA
                    level = cellInfo.level
                    networkBand = Signal.THIRD_GEN
                }
                is CellSignalStrengthWcdma -> {
                    strength = cellInfo.dbm
                    band = Signal.TYPE_WCDMA
                    level = cellInfo.level
                    networkBand = Signal.THIRD_GEN
                }
                is CellSignalStrengthNr -> {
                    strength = cellInfo.csiRsrp
                    band = Signal.TYPE_NR
                    level = cellInfo.level
                    networkBand = Signal.FIFTH_GEN
                }
                is CellSignalStrengthTdscdma -> {
                    strength = cellInfo.dbm
                    band = Signal.TYPE_TDSCDMA
                    level = cellInfo.level
                    networkBand = Signal.THIRD_GEN
                }
            }
            Log.d("zzz", "onSignalStrengthsChanged===============: $strength $level $networkBand")

        } else {
            if (/*DeviceDetailsApplication.instance.checkCallingOrSelfPermission("android.Manifest.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED &&*/ telephonyManager.allCellInfo.isNotEmpty()) {
                when (val cellInfo = telephonyManager.allCellInfo[0]) {
                    is CellInfoLte -> {
                        band = Signal.TYPE_LTE
                        strength = cellInfo.cellSignalStrength.dbm
                        level = cellInfo.cellSignalStrength.level
                        networkBand = Signal.FOURTH_GEN
                    }
                    is CellInfoGsm -> {
                        band = Signal.TYPE_GSM
                        strength = cellInfo.cellSignalStrength.dbm
                        level = cellInfo.cellSignalStrength.level
                        networkBand = Signal.SECOND_GEN
                    }
                    is CellInfoCdma -> {
                        band = Signal.TYPE_CDMA
                        strength = cellInfo.cellSignalStrength.dbm
                        level = cellInfo.cellSignalStrength.level
                        networkBand = Signal.THIRD_GEN
                    }
                    is CellInfoWcdma -> {
                        band = Signal.TYPE_WCDMA
                        strength = cellInfo.cellSignalStrength.dbm
                        level = cellInfo.cellSignalStrength.level
                        networkBand = Signal.THIRD_GEN
                    }
                }
                Log.d("zzz", "onSignalStrengthsChanged^^^^^^^^^^^^^^^^^^^^^^^^^: $strength $level $networkBand")

            }
        }

        strengthPercentage =
            (strength - Signal.CELLULAR_MIN) / Signal.CELLULAR_RANGE.toFloat() * 100
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

    fun networkTypeClass(networkType: Int): String {
        when (networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN,
            TelephonyManager.NETWORK_TYPE_GSM
            -> return "2G"
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_TD_SCDMA
            -> return "3G"
            TelephonyManager.NETWORK_TYPE_LTE
            -> return "4G"
            TelephonyManager.NETWORK_TYPE_NR
            -> return "5G"
            else -> return "Unknown"
        }
    }
}