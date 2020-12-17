package com.example.androidDeviceDetails

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import de.nitri.gauge.Gauge

class SignalStrengthActivity : AppCompatActivity()  {
   lateinit var gaugeWifi:Gauge
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signal_strength)


        val ha = Handler()
        ha.postDelayed(object : Runnable {
            override fun run() {
                updateStrength()
                ha.postDelayed(this, 1000)
            }
        }, 1000)

    }
     fun updateStrength() {
        gaugeWifi = findViewById(R.id.gaugeWifi)
        val gaugeCellular: Gauge = findViewById(R.id.gaugeCellular)
        gaugeWifi.moveToValue(80F)
    }

}