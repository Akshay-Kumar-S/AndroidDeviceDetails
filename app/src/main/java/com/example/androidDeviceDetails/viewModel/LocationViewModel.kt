package com.example.androidDeviceDetails.viewModel

import android.content.Context
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.LocationAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.models.location.LocationDisplayModel
import com.github.davidmoten.geo.GeoHash.decodeHash
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker


class LocationViewModel(private val binding: ActivityLocationBinding, val context: Context) :
    BaseViewModel() {

    private lateinit var cookedDataList: ArrayList<LocationDisplayModel>

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
            Toast.makeText(context, "No data on selected date", Toast.LENGTH_SHORT).show()
        }


    override fun sort(type: Int) {
        binding.root.post {
            (binding.bottomLocation.locationListView.adapter as LocationAdapter).sortView(type)
        }
        toggleSortButton()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> onDone(outputList: ArrayList<T>) {
        cookedDataList = outputList as ArrayList<LocationDisplayModel>
        if (cookedDataList.isEmpty())
            onNoData()
        else {
            focusMapTo(cookedDataList[0].geoHash)
            addPointOnMap()
            buildAdapterView()
        }
    }

    private fun buildAdapterView() {
        binding.root.post {
            (binding.bottomLocation.locationListView.adapter as LocationAdapter).refreshList(
                cookedDataList)
        }
    }

    private fun addPointOnMap() {
        for (location in cookedDataList) {
            val latLong = decodeHash(location.geoHash)
            val geoPoint = GeoPoint(latLong.lat, latLong.lon)
            val marker = Marker(binding.mapView)
            binding.root.post {
                marker.icon = getDrawable(context, R.drawable.ic_location)
                marker.position = geoPoint
                marker.title = "Visited ${location.count} times"
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                binding.mapView.overlays.add(marker)
            }
        }
    }

    fun focusMapTo(geoHash: String) {
        val latLong = decodeHash(geoHash)
        val geoPoint = GeoPoint(latLong.lat, latLong.lon)
        binding.root.post {
            val mapController = binding.mapView.controller
            mapController.setZoom(15.0)
            mapController.setCenter(geoPoint)
        }
    }

}