package com.example.androidDeviceDetails.managers

import android.content.Context
import android.os.Build
import android.telephony.*
import android.util.Log
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.RoomDB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignalChangeListener(private val context: Context) : PhoneStateListener() {

    private var signalDB = RoomDB.getDatabase()


    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
        val cellularRaw: CellularRaw
        var level = 0
        var strength = 0
        var type = ""
        var asuLevel = 0
        val lteData: CellSignalStrengthLte
        val gsmData: CellSignalStrengthGsm
        val cdmaData: CellSignalStrengthCdma
        val wcdmaData: CellSignalStrengthWcdma


        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (signalStrength.cellSignalStrengths[0] is CellSignalStrengthLte) {
                lteData = signalStrength.cellSignalStrengths[0] as CellSignalStrengthLte
                strength = lteData.rsrp
                level = lteData.level
                type = "LTE"
                asuLevel = lteData.asuLevel
            }
            if (signalStrength.cellSignalStrengths[0] is CellSignalStrengthGsm) {
                gsmData = signalStrength.cellSignalStrengths[0] as CellSignalStrengthGsm
                strength = gsmData.dbm
                level = gsmData.level
                type = "GSM"
                asuLevel = gsmData.asuLevel
            }
            if (signalStrength.cellSignalStrengths[0] is CellSignalStrengthCdma) {
                cdmaData = signalStrength.cellSignalStrengths[0] as CellSignalStrengthCdma
                strength = cdmaData.cdmaDbm
                type = "CDMA"
                level = cdmaData.level
                asuLevel = cdmaData.asuLevel
            }
            if (signalStrength.cellSignalStrengths[0] is CellSignalStrengthWcdma) {
                wcdmaData = signalStrength.cellSignalStrengths[0] as CellSignalStrengthWcdma
                strength = wcdmaData.dbm
                type = "WCDMA"
                level = wcdmaData.level
                asuLevel = wcdmaData.asuLevel
            }
        } else {
            try {
                val telephonyManager =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val cellInfo = telephonyManager.allCellInfo[0]
                Log.d("test", "onSignalStrengthsChanged: ")
                when (cellInfo) {
                    is CellInfoLte -> {
                        type = "LTE"
                        strength = cellInfo.cellSignalStrength.dbm
                        level = cellInfo.cellSignalStrength.level
                        asuLevel = cellInfo.cellSignalStrength.asuLevel
                        Log.d("tags", "lte")
                    }
                    is CellInfoGsm -> {
                        type = "GSM"
                        strength = cellInfo.cellSignalStrength.dbm
                        level = cellInfo.cellSignalStrength.level
                        asuLevel = cellInfo.cellSignalStrength.asuLevel
                        Log.d("tags", "gsm")
                    }
                    is CellInfoCdma -> {
                        type = "CDMA"
                        strength = cellInfo.cellSignalStrength.dbm
                        level = cellInfo.cellSignalStrength.level
                        asuLevel = cellInfo.cellSignalStrength.asuLevel
                        Log.d("tags", "cdma")
                    }
                    is CellInfoWcdma -> {
                        type = "WCDMA"
                        strength = cellInfo.cellSignalStrength.dbm
                        level = cellInfo.cellSignalStrength.level
                        asuLevel = cellInfo.cellSignalStrength.asuLevel
                        Log.d("tags", "wcdma")
                    }
                    else -> {

                    }
                }

            } catch (e: SecurityException) {
            } catch (e: Exception) {
            }
        }
        Log.d("tagdata1", "data: $strength, $level,$asuLevel,$type")

        cellularRaw = CellularRaw(
            System.currentTimeMillis(), type, strength, level, asuLevel
        )
        GlobalScope.launch {
            signalDB?.cellularDao()?.insertAll(cellularRaw)
        }
    }
}