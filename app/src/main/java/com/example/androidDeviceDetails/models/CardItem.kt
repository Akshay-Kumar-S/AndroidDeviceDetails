package com.example.androidDeviceDetails.models

data class CardItem(
    var tag : Int = 0,
    var image: Int = 0,
    var title: String? = null,
    var layoutType: Int = 0,
    var mainValue: Int = 0,
    var superscript: String = "",
    var subscript: String = "",
    var progressbarFirst: Int = 0,
    var progressbarSecond: Int = 0,
    var label1: String = "",
    var label2: String = "",
    var label1Value: String = "",
    var label2Value: String = ""
)