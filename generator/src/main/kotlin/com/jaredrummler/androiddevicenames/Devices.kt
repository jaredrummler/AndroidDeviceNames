package com.jaredrummler.androiddevicenames

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

object Devices {

  private const val URL = "https://storage.googleapis.com/play_public/supported_devices.csv"

  fun get(url: String = URL) = mutableListOf<Device>().apply {
    val conn = java.net.URL(url).openConnection()
    BufferedReader(InputStreamReader(conn.getInputStream(), "UTF-16")).use { reader ->
      reader.readLine() // skip header
      reader.forEachLine { line ->
        val records = line.split(",").dropLastWhile(String::isEmpty).toTypedArray()
        if (records.size == 4) {
          val manufacturer = records[0]
          val name = records[1]
          val code = records[2]
          val model = records[3]
          add(Device(manufacturer, getPreferredDeviceName(name), code, model))
        }
      }
    }
  }

  val popular: List<String>
    get() = mutableListOf<String>().apply {
      JsonParser.parseReader(
          File("POPULAR.json").reader()).asJsonObject.entrySet().forEach { entry ->
        (entry.value as JsonArray).forEach { element ->
          add(element.asString)
        }
      }
    }

  private fun getPreferredDeviceName(deviceName: String): String {
    return when (deviceName) {
      "OnePlus3" -> "OnePlus 3"
      "OnePlus3T" -> "OnePlus 3T"
      "OnePlus5" -> "OnePlus 5"
      "OnePlus5T" -> "OnePlus 5T"
      "OnePlus6" -> "OnePlus 6"
      "OnePlus6T" -> "OnePlus 6T"
      else -> deviceName
    }
  }

}