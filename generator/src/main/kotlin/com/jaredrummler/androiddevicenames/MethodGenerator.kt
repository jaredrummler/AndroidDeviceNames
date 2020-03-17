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

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.TreeMap
import java.util.TreeSet

class MethodGenerator(private val devices: MutableList<Device>, private val file: File = File("method.tmp")) {

  fun generate() {
    Files.write(Paths.get(file.absolutePath), create().toByteArray())
  }

  private fun create(): String {
    val method = StringBuilder()

    // sort by manufacturer name
    devices.sortWith(Comparator { o1, o2 ->
      val str1 = o1.manufacturer + o1.marketName + o1.model
      val str2 = o2.manufacturer + o2.marketName + o2.model
      str1.compareTo(str2, ignoreCase = true)
    })

    // get a list of devices for all popular manufacturers
    val manufacturers = TreeMap<String, MutableList<Device>>()

    Devices.popular.forEach { name ->
      devices.forEach { device ->
        if (name == device.marketName) {
          val list = manufacturers[device.manufacturer] ?: mutableListOf()
          list.add(device)
          manufacturers[device.manufacturer] = list
        }
      }
    }

    // method signature
    method.append("public static String getDeviceName(String codename, String model, String fallback) {\n")

    // loop through each manufacturer and print an if statement for each device
    var manufactor = ""
    for ((key, deviceList) in manufacturers) {
      // Java comment indicating the manufacturer
      if (manufactor != key) {
        manufactor = key
        method.append("  // ----------------------------------------------------------------------------\n")
        method.append("  // ").append(manufactor).append('\n')
      }

      // get the device list for this manufacturer
      deviceList.sortWith(Comparator { o1, o2 -> o1.marketName.compareTo(o2.marketName, ignoreCase = true) })
      val map = TreeMap<String, MutableList<Device>>()
      for (device in deviceList) {
        val list = map[device.marketName] ?: mutableListOf()
        list.add(device)
        map[device.marketName] = list
      }

      // loop through each device (may contain more than one codename/model) and print an if statement
      for ((deviceName, list) in map) {
        val ifstatement = StringBuilder()
        ifstatement.append("  if ((codename != null && ")
        if (list.size > 1) {
          ifstatement.append("(")
        }
        var s = ""
        val codenames = TreeSet<String>()
        val models = TreeSet<String>()
        for (device in list) {
          codenames.add(device.codename)
          if (device.model != deviceName && device.model != "Nexus 7") {
            models.add(device.model)
          }
        }
        for (codename in codenames) {
          ifstatement.append(s).append("codename.equals(\"").append(codename).append("\")")
          s = " \n      || "
        }
        ifstatement.append(")")
        if (list.size > 1) {
          ifstatement.append(")")
        }
        if (models.size > 0) {
          ifstatement.append(" \n      || (model != null && ")
          if (list.size > 1) {
            ifstatement.append("(")
          }
          s = ""
          for (model in models) {
            ifstatement.append(s).append("model.equals(\"").append(model).append("\")")
            s = " \n      || "
          }
          ifstatement.append("))")
          if (list.size > 1) {
            ifstatement.append(")")
          }
        } else {
          ifstatement.append(")")
        }
        ifstatement.append(" { \n      return \"").append(deviceName).append("\";\n  }")
        method.append(ifstatement.toString()).append('\n')
      }
    }

    method.append("  return fallback;\n}")
    return method.toString()
  }

}