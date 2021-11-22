package com.jaredrummler.androiddevicenames

import java.io.BufferedReader
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
          add(Device(manufacturer, name, code, model))
        }
      }
    }
  }
}