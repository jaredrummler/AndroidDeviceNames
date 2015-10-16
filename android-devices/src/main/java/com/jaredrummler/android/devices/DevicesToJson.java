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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DevicesToJson {

  private static HashMap<String, List<Device>> getCodenames(List<Device> devices) {
    HashMap<String, List<Device>> codenames = new HashMap<>();
    for (Device device : devices) {
      List<Device> deviceList = codenames.get(device.codename);
      if (deviceList == null) {
        deviceList = new ArrayList<>();
      }
      deviceList.add(device);
      codenames.put(device.codename, deviceList);
    }
    return codenames;
  }

  private static List<Manufacturer> getManufacturers(List<Device> devices) {
    List<Manufacturer> manufacturers = new ArrayList<>();
    Map<String, List<Device>> map = new TreeMap<>();
    for (Device device : devices) {
      if (device.manufacturer == null || device.manufacturer.trim().length() == 0) {
        continue;
      }
      List<Device> deviceList = map.get(device.manufacturer);
      if (deviceList == null) {
        deviceList = new ArrayList<>();
      }
      deviceList.add(new Device(null, device.marketName, device.codename, device.model));
      map.put(device.manufacturer, deviceList);
    }
    for (Map.Entry<String, List<Device>> entry : map.entrySet()) {
      manufacturers.add(new Manufacturer(entry.getKey(), entry.getValue()));
    }
    return manufacturers;
  }

  private final List<Device> devices;

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public DevicesToJson(List<Device> devices) {
    this.devices = devices;
  }

  public void createCodenamesJson(File dir) throws IOException {
    HashMap<String, List<Device>> codenames = getCodenames(devices);
    dir.mkdirs();
    for (Map.Entry<String, List<Device>> entry : codenames.entrySet()) {
      File file = new File(dir, entry.getKey() + ".json");
      String json = gson.toJson(entry.getValue());
      FileUtils.write(file, json);
    }
  }

  public void createManufacturersJson(File dir) throws IOException {
    List<Manufacturer> manufacturers = getManufacturers(devices);
    dir.mkdirs();
    for (Manufacturer manufacturer : manufacturers) {
      File file = new File(dir, manufacturer.getJsonFilename());
      String json = gson.toJson(manufacturer);
      FileUtils.write(file, json);
    }
  }

  public void createPopularDevicesJson(File destination) throws IOException {
    List<Device> popularDevices = new ArrayList<>();
    for (String deviceName : Constants.POPULAR_DEVICES) {
      for (Device device : devices) {
        if (device.marketName.equalsIgnoreCase(deviceName)) {
          popularDevices.add(device);
        }
      }
    }
    FileUtils.write(destination, gson.toJson(popularDevices));
  }

  public void createDevicesJson(File destination) throws IOException {
    FileUtils.write(destination, gson.toJson(devices));
  }

}
