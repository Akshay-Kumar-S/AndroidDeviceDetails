package com.example.androidDeviceDetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.nitri.gauge.Gauge

class SignalStrengthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signal_strength)
        val gaugeWifi: Gauge = findViewById(R.id.gaugeWifi)
        val gaugeCellular: Gauge = findViewById(R.id.gaugeCellular)
    }
}