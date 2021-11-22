package com.jaredrummler.androiddevicenames

import com.google.gson.annotations.SerializedName

data class Device(
    /** Retail branding  */
    val manufacturer: String,
    /** The consumer friendly name of the device  */
    @SerializedName("market_name")
    val marketName: String,
    /** The value of the system property "ro.product.device"  */
    val codename: String,
    /** The value of the system property "ro.product.model"  */
    val model: String
)