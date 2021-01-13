package com.example.androidDeviceDetails.models

data class CardItem (
    var image: Int = 0,
    var title: String? = null,
    var layoutType : Int,
    var mainValue :  Int,
    var superscript : String,
    var subscript : String,
    var progressbarFirst : Int,
    var progressbarSecond : Int,
    var label1 : String,
    var label2 : String,
    var label1Value : String,
    var label2Value : String
)