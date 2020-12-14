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
        val lteData: CellSignalStrengthLte
        val gsmData: CellSignalStrengthGsm
        val cdmaData: CellSignalStrengthCdma
        val wcdmaData: CellSignalStrengthWcdma
        val nrData: CellSignalStrengthNr
        val tscdmaData: CellSignalStrengthTdscdma


        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            when {
                signalStrength.getCellSignalStrengths()[0] is CellSignalStrengthLte -> {
                    lteData = signalStrength.getCellSignalStrengths()[0] as CellSignalStrengthLte
                    strength = lteData.rsrp
                    level = lteData.level
                    type = "LTE"
                    asuLevel = lteData.asuLevel
                }
                signalStrength.getCellSignalStrengths()[0] is CellSignalStrengthGsm -> {
                    gsmData = signalStrength.getCellSignalStrengths()[0] as CellSignalStrengthGsm
                    strength = gsmData.dbm
                    level = gsmData.level
                    type = "GSM"
                    asuLevel = gsmData.asuLevel
                }
                signalStrength.getCellSignalStrengths()[0] is CellSignalStrengthCdma -> {
                    cdmaData = signalStrength.getCellSignalStrengths()[0] as CellSignalStrengthCdma
                    strength = cdmaData.cdmaDbm
                    type = "CDMA"
                    level = cdmaData.level
                    asuLevel = cdmaData.asuLevel
                }
                signalStrength.getCellSignalStrengths()[0] is CellSignalStrengthWcdma -> {
                    wcdmaData = signalStrength.getCellSignalStrengths()[0] as CellSignalStrengthWcdma
                    strength = wcdmaData.dbm
                    type = "WCDMA"
                    level = wcdmaData.level
                    asuLevel = wcdmaData.asuLevel
                }
                signalStrength.getCellSignalStrengths()[0] is CellSignalStrengthNr -> {
                    nrData = signalStrength.getCellSignalStrengths()[0] as CellSignalStrengthNr
                    strength = nrData.csiRsrp
                    type = "CDMA"
                    level = nrData.level
                    asuLevel = nrData.asuLevel
                }
                signalStrength.getCellSignalStrengths()[0] is CellSignalStrengthTdscdma -> {
                    tscdmaData = signalStrength.getCellSignalStrengths()[0] as CellSignalStrengthTdscdma
                    strength = tscdmaData.dbm
                    type = "WCDMA"
                    level = tscdmaData.level
                    asuLevel = tscdmaData.asuLevel
                }
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