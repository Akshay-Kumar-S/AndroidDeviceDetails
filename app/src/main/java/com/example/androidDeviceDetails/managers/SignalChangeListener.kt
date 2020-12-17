package com.example.androidDeviceDetails.managers

import android.content.Context
import android.os.Build
import android.telephony.*
import android.util.Log
import android.widget.Toast
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val data = signalStrength.getCellSignalStrengths()[0]
            when (data) {
                is CellSignalStrengthLte -> {
                    strength = data.rsrp
                    level = data.level
                    type = "LTE"
                    asuLevel = data.asuLevel
                }
                is CellSignalStrengthGsm -> {
                    strength = data.dbm
                    level = data.level
                    type = "GSM"
                    asuLevel = data.asuLevel
                }
                is CellSignalStrengthCdma -> {
                    strength = data.cdmaDbm
                    type = "CDMA"
                    level = data.level
                    asuLevel = data.asuLevel
                }
                is CellSignalStrengthWcdma -> {
                    strength = data.dbm
                    type = "WCDMA"
                    level = data.level
                    asuLevel = data.asuLevel
                }
                is CellSignalStrengthNr -> {
                    strength = data.csiRsrp
                    type = "CDMA"
                    level = data.level
                    asuLevel = data.asuLevel
                }
                is CellSignalStrengthTdscdma -> {
                    strength = data.dbm
                    type = "WCDMA"
                    level = data.level
                    asuLevel = data.asuLevel
                }
            }
        } else {
            try {
                val telephonyManager =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val cellInfo = telephonyManager.allCellInfo[0]
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
            }
//            catch (e: Exception) {
//                Log.d("tagdata1", "datacrash: $strength, $level,$asuLevel,$type")
//            }
        }
        Toast.makeText(context, "this is toast message   $strength", Toast.LENGTH_SHORT).show()
        Log.d("tagdata1", "data: $strength, $level,$asuLevel,$type")
        cellularRaw = CellularRaw(
            System.currentTimeMillis(), type, strength, level, asuLevel
        )
        GlobalScope.launch {
            signalDB?.cellularDao()?.insertAll(cellularRaw)
        }

    }


}