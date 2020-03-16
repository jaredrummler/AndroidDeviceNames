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
import com.google.gson.annotations.SerializedName
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Locale
import java.util.TreeMap
import java.util.stream.Collectors

class JsonGenerator(private val devices: List<Device>, directory: String = "json") {

  private val gson = GsonBuilder().setPrettyPrinting().create()
  private val rootDir = File(directory)
  private val devicesDir = File(rootDir, "devices")
  private val oemDir = File(rootDir, "manufacturers")
  private val devicesJson = File(rootDir, "devices.json")
  private val popularJson = File(rootDir, "popular-devices.json")

  /**
   * Create all the JSON files from the devices list
   */
  fun generate() {
    createMainJsonFile()
    createPopularDevicesJsonFile()
    createDeviceJsonFiles()
    createOemJsonFiles()
  }

  private fun createMainJsonFile() = createJsonFile(devicesJson, gson.toJson(devices))

  private fun createDeviceJsonFiles() {
    val codenames = hashMapOf<String, MutableList<Device>>()
    for (device in devices) {
      if (!device.codename.isBlank()) {
        val key = device.codename.toLowerCase(Locale.US)
        val list = codenames[key] ?: mutableListOf()
        list.add(device)
        codenames[key] = list
      }
    }
    for ((key, value) in codenames) {
      createJsonFile(File(devicesDir, "$key.json"), gson.toJson(value))
    }
  }

  private fun createPopularDevicesJsonFile() {
    val popular = mutableListOf<Device>()
    Devices.popular.forEach { name ->
      devices.forEach { device ->
        if (device.marketName.toLowerCase(Locale.US) == name.toLowerCase(Locale.US)) {
          popular.add(device)
        }
      }
    }
    createJsonFile(popularJson, gson.toJson(popular))
  }

  private fun createOemJsonFiles() {
    val manufacturers = mutableListOf<OEM>()
    val map = TreeMap<String, MutableList<Device>>()
    devices.forEach { device ->
      if (!device.manufacturer.isBlank()) {
        val deviceList = map[device.manufacturer] ?: mutableListOf()
        deviceList.add(Device("", device.marketName, device.codename,
            device.model))
        map[device.manufacturer] = deviceList
      }
    }
    manufacturers.addAll(map.entries.stream()
        .map { entry -> OEM(entry.key, entry.value) }
        .collect(Collectors.toList<OEM>()))
    manufacturers.forEach { oem ->
      val filename = oem.manufacturer.toUpperCase(Locale.US)
          .replace(" ", "_")
          .replace("\\.", "")
          .replace("-", "") + ".json"
      createJsonFile(File(oemDir, filename), gson.toJson(oem))
    }
  }

  private fun createJsonFile(file: File, json: String) {
    file.parentFile?.mkdirs()
    Files.write(Paths.get(file.absolutePath), json.toByteArray())
  }

}

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

class OEM(
    /** Retail branding  */
    val manufacturer: String,
    /** List of devices the manufacturer has that support Google Play  */
    val devices: List<Device>
)