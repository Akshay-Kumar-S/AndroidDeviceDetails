package com.example.androidDeviceDetails.managers

import android.content.Context
import android.os.Build
import android.telephony.*
import android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
import android.util.Log
import com.example.androidDeviceDetails.DeviceDetailsApplication
import com.example.androidDeviceDetails.base.BaseCollector
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.signalModels.SignalEntity
import com.example.androidDeviceDetails.utils.Signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignalChangeListener : BaseCollector() {

    private var mTelephonyManager: TelephonyManager =
        DeviceDetailsApplication.instance.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    object phoneStateListener : PhoneStateListener() {
        override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
            Log.d("servicestart", "started")
            val signalEntity: SignalEntity
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
            signalEntity = SignalEntity(
                System.currentTimeMillis(), Signal.CELLULAR.ordinal, strength, type, level
            )
            GlobalScope.launch {
                signalDB?.signalDao()?.insertAll(signalEntity)
            }
        }
    }

    init {
        start()
    }

    override fun start() {
        mTelephonyManager.listen(phoneStateListener, LISTEN_SIGNAL_STRENGTHS)
    }

    override fun collect() {
    }

    override fun stop() {
        mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }

    init {
        start()
    }
}