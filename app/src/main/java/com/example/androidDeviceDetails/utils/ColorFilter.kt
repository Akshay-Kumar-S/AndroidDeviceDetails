package com.example.androidDeviceDetails.utils

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter

object ColorFilter {
    val darkModeFilter: ColorMatrixColorFilter
        get() = mapColorFilter()

    private fun mapColorFilter(): ColorMatrixColorFilter {
        val inverseMatrix = ColorMatrix(
            floatArrayOf(
                -1.0f, 0.0f, 0.0f, 0.0f, 255f,
                0.0f, -1.0f, 0.0f, 0.0f, 255f,
                0.0f, 0.0f, -1.0f, 0.0f, 255f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
            )
        )
        val destinationColor = Color.parseColor("#FF2A2A2A")
        val lr = (255.0f - Color.red(destinationColor)) / 255.0f
        val lg = (255.0f - Color.green(destinationColor)) / 255.0f
        val lb = (255.0f - Color.blue(destinationColor)) / 255.0f
        val grayscaleMatrix = ColorMatrix(
            floatArrayOf(
                lr, lg, lb, 0F, 0F,
                lr, lg, lb, 0F, 0F,
                lr, lg, lb, 0F, 0F,
                0F, 0F, 0F, 0F, 255F,
            )
        )
        grayscaleMatrix.preConcat(inverseMatrix)
        val dr = Color.red(destinationColor)
        val dg = Color.green(destinationColor)
        val db = Color.blue(destinationColor)
        val drf = dr / 255f
        val dgf = dg / 255f
        val dbf = db / 255f
        val tintMatrix = ColorMatrix(
            floatArrayOf(
                drf, 0F, 0F, 0F, 0F,
                0F, dgf, 0F, 0F, 0F,
                0F, 0F, dbf, 0F, 0F,
                0F, 0F, 0F, 1F, 0F,
            )
        )
        tintMatrix.preConcat(grayscaleMatrix)
        val lDestination = drf * lr + dgf * lg + dbf * lb
        val scale = 1f - lDestination
        val translate = 1 - scale * 0.5f
        val scaleMatrix = ColorMatrix(
            floatArrayOf(
                scale, 0F, 0F, 0F, dr * translate,
                0F, scale, 0F, 0F, dg * translate,
                0F, 0F, scale, 0F, db * translate,
                0F, 0F, 0F, 1F, 0F,
            )
        )
        scaleMatrix.preConcat(tintMatrix)
        return ColorMatrixColorFilter(scaleMatrix)
    }
}