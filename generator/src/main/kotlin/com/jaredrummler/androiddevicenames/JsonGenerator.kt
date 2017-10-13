/*
 * Copyright (C) 2017 Jared Rummler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jaredrummler.androiddevicenames

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.TreeMap
import java.util.stream.Collectors

private val URL = "https://storage.googleapis.com/play_public/supported_devices.csv"

fun getDevices(): List<Device> {
  val devices = mutableListOf<Device>()
  val url = java.net.URL(URL)
  val conn = url.openConnection()
  BufferedReader(InputStreamReader(conn.getInputStream(), "UTF-16")).use { reader ->
    reader.readLine() // skip header
    reader.forEachLine { line ->
      val records = line.split(",").dropLastWhile(String::isEmpty).toTypedArray()
      if (records.size == 4) {
        val manufacturer = records[0]
        val name = records[1]
        val code = records[2]
        val model = records[3]
        devices.add(Device(manufacturer, name, code, model))
      }
    }
  }
  return devices
}

fun getPopularDeviceNames(): List<String> {
  val names = mutableListOf<String>()
  JsonParser().parse(File("POPULAR.json").reader()).asJsonObject.entrySet().forEach {
    (it.value as JsonArray).forEach {
      names.add(it.asString)
    }
  }
  return names
}

class JsonGenerator(private val devices: List<Device>, directory: String = "json") {

  /**
   * Create all the JSON files from the devices list
   */
  fun generate() {
    createMainJsonFile()
    createPopularDevicesJsonFile()
    createDeviceJsonFiles()
    createOemJsonFiles()
  }

  private fun createMainJsonFile() = createJsonFile(DEVICES_JSON, GSON.toJson(devices))

  private fun createDeviceJsonFiles() {
    val codenames = hashMapOf<String, MutableList<Device>>()
    for (device in devices) {
      if (!device.codename.isBlank()) {
        val key = device.codename.toLowerCase()
        val list = codenames[key] ?: mutableListOf()
        list.add(device)
        codenames.put(key, list)
      }
    }
    for ((key, value) in codenames) {
      createJsonFile(File(DEVICES_DIR, key + ".json"), GSON.toJson(value))
    }
  }

  private fun createPopularDevicesJsonFile() {
    val popular = mutableListOf<Device>()
    getPopularDeviceNames().forEach { name ->
      devices.forEach { device ->
        if (device.marketName == name.toLowerCase()) popular.add(device)
      }
    }
    createJsonFile(POPULAR_JSON, GSON.toJson(popular))
  }

  private fun createOemJsonFiles() {
    val manufacturers = mutableListOf<OEM>()
    val map = TreeMap<String, MutableList<Device>>()
    devices.forEach { device ->
      if (!device.manufacturer.isBlank()) {
        val deviceList = map[device.manufacturer] ?: mutableListOf()
        deviceList.add(Device("", device.marketName, device.codename,
            device.model))
        map.put(device.manufacturer, deviceList)
      }
    }
    manufacturers.addAll(map.entries.stream()
        .map { entry -> OEM(entry.key, entry.value) }
        .collect(Collectors.toList<OEM>()))
    manufacturers.forEach { oem ->
      val filename = oem.manufacturer.toUpperCase().replace(" ", "_").replace("\\.", "").replace("-", "") + ".json"
      createJsonFile(File(OEM_DIR, filename), GSON.toJson(oem))
    }
  }

  private fun createJsonFile(file: File, json: String) {
    file.parentFile?.mkdirs()
    Files.write(Paths.get(file.absolutePath), json.toByteArray())
  }

  private val GSON = GsonBuilder().setPrettyPrinting().create()
  private val ROOT_DIR = File(directory)
  private val DEVICES_DIR = File(ROOT_DIR, "devices")
  private val OEM_DIR = File(ROOT_DIR, "manufacturers")
  private val DEVICES_JSON = File(ROOT_DIR, "devices.json")
  private val POPULAR_JSON = File(ROOT_DIR, "popular-devices.json")

}

class Device(
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

class OEM(
    /** Retail branding  */
    val manufacturer: String,
    /** List of devices the manufacturer has that support Google Play  */
    val devices: List<Device>
)