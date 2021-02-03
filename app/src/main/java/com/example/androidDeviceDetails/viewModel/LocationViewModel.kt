package com.example.androidDeviceDetails.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View.GONE
import android.widget.Toast
import androidx.core.content.ContextCompat.getDrawable
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.adapters.LocationAdapter
import com.example.androidDeviceDetails.base.BaseViewModel
import com.example.androidDeviceDetails.databinding.ActivityLocationBinding
import com.example.androidDeviceDetails.models.location.LocationData
import com.example.androidDeviceDetails.utils.CustomMarkerCluster
import com.google.maps.android.ui.IconGenerator
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker


class LocationViewModel(private val binding: ActivityLocationBinding, val context: Context) :
    BaseViewModel() {

    private fun toggleSortButton() {
        if (binding.locationBottomSheet.sortButton.tag == "down") {
            binding.locationBottomSheet.sortButton.tag = "up"
            binding.locationBottomSheet.sortButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null, null, getDrawable(context, R.drawable.ic_arrow_upward), null
            )

        } else {
            binding.locationBottomSheet.sortButton.tag = "down"
            binding.locationBottomSheet.sortButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null, null, getDrawable(context, R.drawable.ic_arrow_downward), null
            )
        }
    }

    private fun onNoData() =
        binding.root.post {
            Toast.makeText(context, "No data on selected date", Toast.LENGTH_SHORT).show()
        }


    override fun sort(type: Int) {
        binding.root.post {
            (binding.locationBottomSheet.locationListView.adapter as LocationAdapter).sortView(type)
        }
        toggleSortButton()
    }

    private fun buildAdapterView(cookedDataList: ArrayList<LocationData>) {
        binding.root.post {
            (binding.locationBottomSheet.locationListView.adapter as LocationAdapter).refreshList(
                cookedDataList
            )
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable)
            return drawable.bitmap
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun addPointOnMap(cookedDataList: ArrayList<LocationData>) {
        val overlays = binding.mapView.overlays
        overlays.clear()
        val cluster = CustomMarkerCluster(context)
        cluster.setIcon(
            getDrawable(context, R.drawable.location_bubble)?.let { drawableToBitmap(it) })
        cluster.setRadius(90)
        cluster.textPaint.textSize = 26F
        for (location in cookedDataList) {
            val marker = Marker(binding.mapView)
            binding.root.post {
                marker.icon = generateMarker(location.count)
                marker.position = GeoPoint(location.avgLatitude, location.avgLongitude)
                marker.infoWindow.view.visibility = GONE
                marker.title = location.count.toString()
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                cluster.add(marker)
                overlays.add(cluster)
            }
        }
    }

    private fun generateMarker(count: Int): Drawable {
        val countString = if (count < 10) "0$count" else count.toString()
        val icg = IconGenerator(context)
        icg.setBackground(getDrawable(context, R.drawable.location_bubble))
        icg.setTextAppearance(R.style.LocationBubble)
        icg.setContentPadding(30, 15, 0, 0)
        return BitmapDrawable(context.resources, icg.makeIcon(countString))
    }

    fun focusMapTo(avgLatitude: Double, avgLongitude: Double) {
        val geoPoint = GeoPoint(avgLatitude, avgLongitude)
        binding.root.post {
            val mapController = binding.mapView.controller
            mapController.setZoom(15.0)
            mapController.setCenter(geoPoint)
        }
    }

    override fun <T> onDone(outputList: ArrayList<T>) {
        val cookedDataList = outputList.filterIsInstance<LocationData>() as ArrayList<LocationData>
        if (cookedDataList.isEmpty())
            onNoData()
        else {
            focusMapTo(cookedDataList.first().avgLatitude, cookedDataList.first().avgLongitude)
            addPointOnMap(cookedDataList)
            buildAdapterView(cookedDataList)
        }
    }

}