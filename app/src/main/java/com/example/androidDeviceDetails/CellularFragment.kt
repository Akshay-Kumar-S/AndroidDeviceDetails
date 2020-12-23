package com.example.androidDeviceDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.androidDeviceDetails.models.CellularRaw
import com.example.androidDeviceDetails.models.RoomDB
import de.nitri.gauge.Gauge

class CellularFragment : Fragment() {
    private var db = RoomDB.getDatabase()!!
    private var cellStrength: Int = -100
    private var cellInfoType: String = "LTE"
    private lateinit var type: TextView
    private lateinit var strength: TextView
    private lateinit var gauge: Gauge
    lateinit var view: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ViewGroup {
        view = inflater.inflate(R.layout.fragment_cellular_strength, container, false) as ViewGroup

        gauge = view.findViewById(R.id.cellularGauge)
        strength = view.findViewById(R.id.cellularStrengthText)
        type = view.findViewById(R.id.cellularType)
        updateGauge()
        db.cellularDao().getLastLive().observe(viewLifecycleOwner) {
            updateCellularGauge(it)
        }
        Log.d("psy", "onCreateView: ")
        return view
    }


    private fun updateGauge() {
        Log.d("psy", "updateGauge: ")
        gauge.moveToValue(cellStrength.toFloat())
        gauge.setLowerText(cellInfoType)
        gauge.setUpperText(cellStrength.toString())
        strength.text = (cellStrength.toString() + R.string.strength)
        type.text = cellInfoType
    }

    private fun updateCellularGauge(cellularRaw: CellularRaw) {
        Log.d("psy", "updateCellularGauge: ")
        cellStrength = cellularRaw.strength!!
        cellInfoType = cellularRaw.type.toString()
        updateGauge()
    }
}