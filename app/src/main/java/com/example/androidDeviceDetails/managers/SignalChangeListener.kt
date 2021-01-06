package com.example.androidDeviceDetails.managers

import android.content.Context
import android.os.Build
import android.telephony.*
import android.util.Log
import com.example.androidDeviceDetails.models.RoomDB
import com.example.androidDeviceDetails.models.signalModels.SignalEntity
import com.example.androidDeviceDetails.utils.Signal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignalChangeListener(private val context: Context) : PhoneStateListener() {
    private var db = RoomDB.getDatabase()!!

    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
        val signalEntity: SignalEntity
        var level = 0
        var strength = 0
        var type = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (val data = signalStrength.cellSignalStrengths[0]) {
                is CellSignalStrengthLte -> {
                    strength = data.rsrp
                    level = data.level
                    type = "LTE"
                }
                is CellSignalStrengthGsm -> {
                    strength = data.dbm
                    level = data.level
                    type = "GSM"
                }
                is CellSignalStrengthCdma -> {
                    strength = data.cdmaDbm
                    type = "CDMA"
                    level = data.level
                }
                is CellSignalStrengthWcdma -> {
                    strength = data.dbm
                    type = "WCDMA"
                    level = data.level
                }
                is CellSignalStrengthNr -> {
                    strength = data.csiRsrp
                    type = "CDMA"
                    level = data.level
                }
                is CellSignalStrengthTdscdma -> {
                    strength = data.dbm
                    type = "WCDMA"
                    level = data.level
                }
            }
        } else {
            try {
                val telephonyManager =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
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

        Log.d("data", "data: $strength, $level,$type")

        signalEntity = SignalEntity(
            System.currentTimeMillis(), Signal.CELLULAR.ordinal, strength, type, level
        )
        GlobalScope.launch {
            db.signalDao().insertAll(signalEntity)
        }
    }
}