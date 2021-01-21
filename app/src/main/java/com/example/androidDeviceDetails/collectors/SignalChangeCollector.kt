package com.example.androidDeviceDetails.collectors

import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import android.telephony.*
import android.telephony.PhoneStateListener.LISTEN_NONE
import android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
import android.util.Log
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.collectors.SignalChangeCollector.SignalChangeListener
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.signalModels.SignalRaw
import com.example.androidDeviceDetails.utils.Signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 *  Implements [BaseCollector].
 *  An event based collector which collects the CELLULAR signal data.
 *  Contains a [PhoneStateListener] : [SignalChangeListener] which is registered on
 *  initialization of this class.
 *  This listener requires [android.Manifest.permission.ACCESS_FINE_LOCATION] permission.
 **/
class SignalChangeCollector() : BaseCollector(), Parcelable {

    private var mTelephonyManager: TelephonyManager =
        DeviceDetailsApplication.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    constructor(parcel: Parcel) : this() {

    }


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
            val signalEntity: SignalRaw
            var level = 0
            var strength = -120
            var type = ""
            val signalDB = RoomDB.getDatabase()
            var isRoaming = false
            var operatorName = ""
            var countryCode = ""
            var networkBand = ""
            val telephonyManager =
                DeviceDetailsApplication.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
               if(signalStrength.cellSignalStrengths.isNotEmpty())
                when ( val cellInfo = signalStrength.cellSignalStrengths[0]) {
                    is CellSignalStrengthLte -> {
                        strength = cellInfo.rsrp
                        level = cellInfo.level
                        type = "LTE"
                        networkBand = "4g"
                    }
                    is CellSignalStrengthGsm -> {
                        strength = cellInfo.dbm
                        level = cellInfo.level
                        type = "GSM"
                        networkBand = "2g"
                    }
                    is CellSignalStrengthCdma -> {
                        strength = cellInfo.cdmaDbm
                        type = "CDMA"
                        level = cellInfo.level
                        networkBand = "3g"
                    }
                    is CellSignalStrengthWcdma -> {
                        strength = cellInfo.dbm
                        type = "WCDMA"
                        level = cellInfo.level
                        networkBand = "3g"
                    }
                    is CellSignalStrengthNr -> {
                        strength = cellInfo.csiRsrp
                        type = "NR"
                        level = cellInfo.level
                        networkBand = "5g"
                    }
                    is CellSignalStrengthTdscdma -> {
                        strength = cellInfo.dbm
                        type = "TDSCDMA"
                        level = cellInfo.level
                        networkBand = "3g"
                    }
                }
            } else {
                try {
                    if(telephonyManager.allCellInfo.isNotEmpty())
                    when (val cellInfo = telephonyManager.allCellInfo[0]) {
                        is CellInfoLte -> {
                            type = "LTE"
                            strength = cellInfo.cellSignalStrength.dbm
                            level = cellInfo.cellSignalStrength.level
                            networkBand = "4g"
                        }
                        is CellInfoGsm -> {
                            type = "GSM"
                            strength = cellInfo.cellSignalStrength.dbm
                            level = cellInfo.cellSignalStrength.level
                            networkBand = "2g"
                        }
                        is CellInfoCdma -> {
                            type = "CDMA"
                            strength = cellInfo.cellSignalStrength.dbm
                            level = cellInfo.cellSignalStrength.level
                            networkBand = "3g"
                        }
                        is CellInfoWcdma -> {
                            type = "WCDMA"
                            strength = cellInfo.cellSignalStrength.dbm
                            level = cellInfo.cellSignalStrength.level
                            networkBand = "3g"
                        }
                    }
                } catch (e: SecurityException) {
                }
            }
            isRoaming = telephonyManager.isNetworkRoaming
            operatorName = telephonyManager.networkOperatorName
            countryCode = telephonyManager.simCountryIso
            signalEntity = SignalRaw(
                System.currentTimeMillis(),
                Signal.CELLULAR.ordinal,
                strength,
                type,
                level,
                operatorName,
                isRoaming,
                networkBand,
                countryCode
            )
            GlobalScope.launch {
                signalDB?.signalDao()?.insertAll(signalEntity)
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SignalChangeCollector> {
        override fun createFromParcel(parcel: Parcel): SignalChangeCollector {
            return SignalChangeCollector(parcel)
        }

        override fun newArray(size: Int): Array<SignalChangeCollector?> {
            return arrayOfNulls(size)
        }
    }
}