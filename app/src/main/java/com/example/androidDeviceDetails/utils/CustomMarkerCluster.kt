package com.example.androidDeviceDetails.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.bonuspack.clustering.StaticCluster
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class CustomMarkerCluster(context: Context) : RadiusMarkerClusterer(context) {
    override fun buildClusterMarker(cluster: StaticCluster?, mapView: MapView?): Marker {
        val m = Marker(mapView)
        m.position = cluster!!.position
        m.setInfoWindow(null)
        m.setAnchor(mAnchorU, mAnchorV)
        val finalIcon =
            Bitmap.createBitmap(mClusterIcon.width, mClusterIcon.height, mClusterIcon.config)
        val iconCanvas = Canvas(finalIcon)
        iconCanvas.drawBitmap(mClusterIcon, 0f, 0f, null)
        var count = 0
        for(i in 0 until cluster.size) count+=cluster.getItem(i).title.toInt()
        val textHeight = (mTextPaint.descent() + mTextPaint.ascent()).toInt()
        iconCanvas.drawText(
            if (count > 999) "999+" else count.toString(),
            mTextAnchorU * finalIcon.width,
            mTextAnchorV * finalIcon.height - textHeight / 2,
            mTextPaint
        )
        m.icon = BitmapDrawable(mapView!!.context.resources, finalIcon)
        return m
    }
}