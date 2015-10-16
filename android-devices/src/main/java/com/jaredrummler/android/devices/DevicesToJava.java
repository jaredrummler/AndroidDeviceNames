/*
 * Copyright (C) 2015. Jared Rummler <me@jaredrummler.com>
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
 *
 */

package com.jaredrummler.android.devices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DevicesToJava {

  public static void printMethod(List<Device> devices) throws IOException {
    List<Device> popularDevices = new ArrayList<>();
    for (String deviceName : Constants.POPULAR_DEVICES) {
      for (Device device : devices) {
        if (device.marketName.equalsIgnoreCase(deviceName)) {
          popularDevices.add(device);
        }
      }
    }

    StringBuilder sb = new StringBuilder();
    Map<String, Set<String>> deviceMap = new TreeMap<>();

    for (String name : Constants.POPULAR_DEVICES) {
      List<String> list = new ArrayList<>();
      Set<String> codenames = new HashSet<>();
      for (Device device : devices) {
        if (device.marketName.equalsIgnoreCase(name)) {
          list.add(device.codename);
        }
      }
      Collections.sort(list);
      codenames.addAll(list);
      deviceMap.put(name, codenames);
    }

    // TODO: Use JavaPoet and create a working class.
    sb.append("public static String getDeviceName(String codename, String fallback) {\n");
    sb.append("  switch (codename) {\n");
    for (Map.Entry<String, Set<String>> entry : deviceMap.entrySet()) {
      Set<String> codenames = entry.getValue();
      for (String codename : codenames) {
        sb.append("    case \"" + codename + "\":\n");
      }
      sb.append("      return \"" + entry.getKey() + "\";\n");
    }
    sb.append("    default:\n");
    sb.append("      return fallback;\n\t}\n}");

    System.out.println(sb.toString());
  }

}
