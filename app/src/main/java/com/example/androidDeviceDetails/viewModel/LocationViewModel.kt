package com.example.androidDeviceDetails.viewModel

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.LocationAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.models.locationModels.CountModel
import com.example.androidDeviceDetails.models.locationModels.LocationModel
import com.github.davidmoten.geo.GeoHash.decodeHash
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker


class LocationViewModel(private val binding: ActivityLocationBinding, val context: Context) :
    BaseViewModel() {

    private lateinit var countedData: Map<String, Int>
    private lateinit var cookedDataList: ArrayList<LocationModel>
    private val geoCoder: Geocoder = Geocoder(context)


    private fun toggleSortButton() {
        if (binding.bottomLocation.sortByCountViewArrow.tag == "down") {
            binding.bottomLocation.sortByCountViewArrow.tag = "up"
            binding.bottomLocation.sortByCountViewArrow.setImageResource(R.drawable.ic_arrow_upward)

        } else {
            binding.bottomLocation.sortByCountViewArrow.tag = "down"
            binding.bottomLocation.sortByCountViewArrow.setImageResource(R.drawable.ic_arrow_downward)
        }
    }

    private fun onNoData() =
        binding.root.post {
            Toast.makeText(
                context, "No data on selected date", Toast.LENGTH_SHORT
            ).show()
        }

    override fun sort(type: Int) {
        binding.root.post {
            (binding.bottomLocation.locationListView.adapter as LocationAdapter).sortView(type)
        }
        toggleSortButton()
    }

    override fun <T> onData(outputList: ArrayList<T>) {
        cookedDataList = outputList as ArrayList<LocationModel>
        if (cookedDataList.isEmpty())
            onNoData()
        else {
            countedData = cookedDataList.groupingBy { it.geoHash!! }.eachCount()
            focusOnMap(countedData.keys.elementAt(0))
            addPointOnMap(countedData)
            buildAdapterView(countedData)
        }
    }

    private fun buildAdapterView(data: Map<String, Int>) {
        val countList: ArrayList<CountModel> = ArrayList()
        for (geoHash in data) {
            val latLong = decodeHash(geoHash.key)
            val address =
                geoCoder.getFromLocation(latLong.lat, latLong.lon, 1)[0]?.locality?.toString()
            countList.add(CountModel(geoHash.key, geoHash.value, address ?: "cannot locate"))
        }
        binding.root.post {
            (binding.bottomLocation.locationListView.adapter as LocationAdapter).refreshList(
                countList)
        }
    }

    private fun addPointOnMap(data: Map<String, Int>) {
        for (geoHash in data) {
            val latLong = decodeHash(geoHash.key)
            val geoPoint = GeoPoint(latLong.lat, latLong.lon)
            val marker = Marker(binding.mapview)
            binding.root.post {
                marker.icon = getDrawable(context, R.drawable.ic_location)
                marker.position = geoPoint
                marker.title = "Visited ${geoHash.value} times"
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                binding.mapview.overlays.add(marker)
            }
        }
    }

    fun focusOnMap(geoHash: String) {
        val latLong = decodeHash(geoHash)
        val geoPoint = GeoPoint(latLong.lat, latLong.lon)
        binding.root.post {
            val mapController = binding.mapview.controller
            mapController.setZoom(15.0)
            mapController.setCenter(geoPoint)
        }
    }

}