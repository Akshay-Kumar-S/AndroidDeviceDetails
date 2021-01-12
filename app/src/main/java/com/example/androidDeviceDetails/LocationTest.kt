package com.example.androidDeviceDetails

import android.util.Log
import com.example.androidDeviceDetails.models.locationModels.test
import com.github.davidmoten.geo.GeoHash
import com.github.davidmoten.geo.GeoHash.encodeHash
import com.fonfon.kgeohash.GeoHash as GeoHashOld


class LocationTest {
    private val geoHashLength = 6

    private fun baseConversion(
        number: String,
        sBase: Int, dBase: Int
    ): String {
        return number.toInt(sBase).toString(dBase)
    }

    fun test(res: ArrayList<test>) {
//        showInLog(res)
        testConversion(res)
    }

    private fun showInLog(res: ArrayList<test>) {
        val widthInMeters=GeoHash.widthDegrees(geoHashLength)*111139
        Log.d("WIDTH", "width:${GeoHash.widthDegrees(geoHashLength)*111139} ")
        for (location in res) {
            val hashNew =
                encodeHash(location.latitude, location.longitude, geoHashLength).toString()
            val hashOld =
                GeoHashOld(location.latitude, location.longitude, geoHashLength).toString()
            if (hashNew != hashOld)
                Log.d("GEOHASH", "NEW:$hashNew OLD:$hashOld")
        }
    }

    private fun testConversion(res: ArrayList<test>) {
        Log.d("data", "testConversion: $res")
        for (location in res) {
            val hashNew =
                encodeHash(location.latitude, location.longitude, geoHashLength).toString()
            val hashInLong = baseConversion(hashNew, 36, 10)
            val hashLongToString = baseConversion(hashInLong, 10, 36)
            Log.d("GeoHash", "testConversion: $hashNew")
            Log.d("convert", "long:$hashInLong string:$hashLongToString")
            if (hashNew != hashLongToString)
                Log.d("check", "NEW:$hashNew OLD:$hashLongToString")
        }
    }
}
