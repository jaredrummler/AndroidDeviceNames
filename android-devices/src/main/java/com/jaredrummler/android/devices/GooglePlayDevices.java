/*
 * Copyright (C) 2015. Jared Rummler <jared.rummler@gmail.com>
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
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class GooglePlayDevices {

  private static final String CSV_URL = "http://storage.googleapis.com/play_public/supported_devices.csv";

  private static final File JSON_DIR = new File("json");
  private static final File DEVICES_JSON = new File(JSON_DIR, "devices.json");
  private static final File POPULAR_JSON = new File(JSON_DIR, "popular-devices.json");
  private static final File DEVICES_DIR = new File(JSON_DIR, "devices");
  private static final File MANUFACTURERS_DIR = new File(JSON_DIR, "manufacturers");

  public static final String[] POPULAR_DEVICES = {
      ////////////////////////////////////////////
      // Acer
      "Iconia Tab 10",
      "Iconia Tab 7",
      "Iconia Tab 8",
      "Liquid",
      "Liquid E3",
      "Liquid Jade",
      "Liquid Jade S",
      "Liquid S3",
      "Liquid Z5",
      ////////////////////////////////////////////
      // Asus
      "MeMO Pad 7",
      "Nexus 7 (2012)",
      "Nexus 7 (2013)",
      "ZenFone 5",
      ////////////////////////////////////////////
      // Dell
      "Venue 7",
      "Venue 8",
      ////////////////////////////////////////////
      // Google
      "Pixel",
      "Pixel XL",
      "Pixel C",
      ////////////////////////////////////////////
      // HTC
      "HTC One",
      "HTC One (E8)",
      "HTC One (M8)",
      "HTC One A9",
      "HTC One M9",
      "HTC One S",
      "Nexus 9",
      ////////////////////////////////////////////
      // Huawei
      "Honor3",
      "Mate S",
      "Nexus 6P",
      ////////////////////////////////////////////
      // LGE
      "LG G Flex",
      "LG G Flex2",
      "LG G2",
      "LG G3",
      "LG G4",
      "LG Leon 4G LTE",
      "LG M1",
      "LG Optimus 3D",
      "LG Optimus G",
      "LG Optimus G Pro",
      "LG Optimus L9",
      "Nexus 4",
      "Nexus 5",
      "Nexus 5X",
      "Optimus 2X",
      "Optimus 3D",
      "Optimus 3D MAX",
      "Optimus Black",
      "Optimus L5",
      "Optimus LTE",
      "Optimus One",
      "Optimus Pad",
      ////////////////////////////////////////////
      // Lenovo
      "Lenovo A7-30GC",
      ////////////////////////////////////////////
      // Motorola
      "DROID Turbo",
      "MOTO E",
      "MOTO G",
      "Moto G (1st Gen)",
      "Moto G (2nd Gen)",
      "MOTO X",
      "Moto X Style",
      "Nexus 6",
      ////////////////////////////////////////////
      // OnePlus
      "OnePlus",
      "OnePlus One",
      "OnePlus2",
      ////////////////////////////////////////////
      // Samsung
      "Galaxy A5",
      "Galaxy A8",
      "Galaxy Ace 4",
      "Galaxy Ace Duos",
      "Galaxy Ace Plus",
      "Galaxy Ace Style",
      "Galaxy Ace4",
      "Galaxy Core Prime",
      "Galaxy Core2",
      "Galaxy E5",
      "Galaxy E7",
      "Galaxy Fame",
      "Galaxy Go Prime",
      "Galaxy Grand Neo",
      "Galaxy Grand Prime",
      "Galaxy Grand2",
      "Galaxy J1",
      "Galaxy J1 Ace",
      "Galaxy J5",
      "Galaxy J7",
      "Galaxy Nexus",
      "Galaxy Note 10.1",
      "Galaxy Note Edge",
      "Galaxy Note Pro 12.2",
      "Galaxy Note4",
      "Galaxy Note5",
      "Galaxy Note6",
      "Galaxy Note7",
      "Galaxy On5",
      "Galaxy On7",
      "Galaxy S Duos",
      "Galaxy S Duos2",
      "Galaxy S Duos3",
      "Galaxy S3",
      "Galaxy S3 Mini",
      "Galaxy S3 Neo",
      "Galaxy S4",
      "Galaxy S4 Mini",
      "Galaxy S5",
      "Galaxy S5 Neo",
      "Galaxy S6",
      "Galaxy S6 Edge",
      "Galaxy S6 Edge+",
      "Galaxy S7",
      "Galaxy S7 Edge",
      "Galaxy Tab 10.1",
      "Galaxy Tab 7.0 Plus",
      "Galaxy Tab E 8.0",
      "Galaxy Tab E 9.6",
      "Galaxy Tab4 7.0",
      "Galaxy View",
      "Galaxy Y",
      "Nexus 10",
      ////////////////////////////////////////////
      // Sony
      "Xperia E1 dual",
      "Xperia E3",
      "Xperia M5",
      "Xperia M5 Dual",
      "Xperia S",
      "Xperia T2 Ultra",
      "Xperia Tablet S",
      "Xperia Tablet Z",
      "Xperia Z2",
      "Xperia Z3",
      "Xperia Z4",
      "Xperia Z5 Compact",
      ////////////////////////////////////////////
      // Sony Ericsson
      "Xperia S",
      "Xperia Tablet Z",
  };

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public final List<Device> devices;

  public GooglePlayDevices() throws IOException {
    devices = new ArrayList<>();
    URL url = new URL(CSV_URL);
    URLConnection conn = url.openConnection();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-16"))) {
      reader.readLine(); // skip header
      String line;
      while ((line = reader.readLine()) != null) {
        String[] records = line.split(",");
        if (records.length == 4) {
          String manufacturer = records[0];
          String name = records[1];
          String code = records[2];
          String model = records[3];
          devices.add(new Device(manufacturer, name, code, model));
        }
      }
    }
  }

  public void createJsonFiles() throws IOException {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // devices.json
    write(DEVICES_JSON, gson.toJson(devices));

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // popular-devices.json
    List<Device> popularDevices = new ArrayList<>();
    for (String name : POPULAR_DEVICES) {
      popularDevices.addAll(
          devices.stream().filter(device -> device.marketName.equalsIgnoreCase(name)).collect(Collectors.toList()));
    }
    write(POPULAR_JSON, gson.toJson(popularDevices));

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // devices
    HashMap<String, List<Device>> codenames = new HashMap<>();
    for (Device device : devices) {
      List<Device> deviceList = codenames.get(device.codename.toLowerCase());
      if (deviceList == null) {
        deviceList = new ArrayList<>();
      }
      deviceList.add(device);
      codenames.put(device.codename.toLowerCase(), deviceList);
    }
    for (Map.Entry<String, List<Device>> entry : codenames.entrySet()) {
      String filename = entry.getKey() + ".json";
      File file = new File(DEVICES_DIR, filename);
      String json = gson.toJson(entry.getValue());
      write(file, json);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // manufacturers
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
    manufacturers.addAll(map.entrySet().stream().map(entry -> new Manufacturer(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList()));
    for (Manufacturer manufacturer : manufacturers) {
      File file = new File(MANUFACTURERS_DIR, manufacturer.getJsonFilename());
      String json = gson.toJson(manufacturer);
      write(file, json);
    }
  }

  public String createGetDeviceNameMethod() {
    StringBuilder method = new StringBuilder();

    List<Device> devices = new ArrayList<>();
    devices.addAll(this.devices);

    // sort by manufacturer name
    Collections.sort(devices, (o1, o2) -> {
      String str1 = o1.manufacturer + o1.marketName + o1.model;
      String str2 = o2.manufacturer + o2.marketName + o2.model;
      return str1.compareToIgnoreCase(str2);
    });

    // get a list of devices for all popular manufacturers
    Map<String, List<Device>> manufacturers = new TreeMap<>();
    for (String name : POPULAR_DEVICES) {
      for (Device device : devices) {
        if (name.equals(device.marketName)) {
          List<Device> list = manufacturers.get(device.manufacturer);
          if (list == null) {
            list = new ArrayList<>();
          }
          list.add(device);
          manufacturers.put(device.manufacturer, list);
        }
      }
    }

    // method signature
    method.append("public static String getDeviceName(String codename, String model, String fallback) {\n");

    // loop through each manufacturer and print an if statement for each device
    String manufactor = "";
    for (Map.Entry<String, List<Device>> entry : manufacturers.entrySet()) {
      // Java comment indicating the manufacturer
      if (!manufactor.equals(entry.getKey())) {
        manufactor = entry.getKey();
        method.append("  // ----------------------------------------------------------------------------\n");
        method.append("  // ").append(manufactor).append('\n');
      }

      // get the device list for this manufacturer
      List<Device> deviceList = entry.getValue();
      Collections.sort(deviceList, (o1, o2) -> o1.marketName.compareToIgnoreCase(o2.marketName));
      Map<String, List<Device>> map = new TreeMap<>();
      for (Device device : deviceList) {
        List<Device> list = map.get(device.marketName);
        if (list == null) {
          list = new ArrayList<>();
        }
        list.add(device);
        map.put(device.marketName, list);
      }

      // loop through each device (may contain more than one codename/model) and print an if statement
      for (Map.Entry<String, List<Device>> e : map.entrySet()) {
        List<Device> list = e.getValue();
        String deviceName = e.getKey();
        StringBuilder ifstatement = new StringBuilder();
        ifstatement.append("  if ((codename != null && ");
        if (list.size() > 1) {
          ifstatement.append("(");
        }
        String s = "";
        Set<String> codenames = new TreeSet<>();
        Set<String> models = new TreeSet<>();
        for (Device device : list) {
          codenames.add(device.codename);
          if (!device.model.equals(deviceName) && !device.model.equals("Nexus 7")) {
            models.add(device.model);
          }
        }
        for (String codename : codenames) {
          ifstatement.append(s).append("codename.equals(\"").append(codename).append("\")");
          s = " \n      || ";
        }
        ifstatement.append(")");
        if (list.size() > 1) {
          ifstatement.append(")");
        }
        if (models.size() > 0) {
          ifstatement.append(" \n      || (model != null && ");
          if (list.size() > 1) {
            ifstatement.append("(");
          }
          s = "";
          for (String model : models) {
            ifstatement.append(s).append("model.equals(\"").append(model).append("\")");
            s = " \n      || ";
          }
          ifstatement.append("))");
          if (list.size() > 1) {
            ifstatement.append(")");
          }
        } else {
          ifstatement.append(")");
        }
        ifstatement.append(" { \n      return \"").append(deviceName).append("\";\n  }");
        method.append(ifstatement.toString()).append('\n');
      }
    }

    method.append("  return fallback;\n}");
    return method.toString();
  }

  public void printGetDeviceNameMethod() {
    System.out.println(createGetDeviceNameMethod());
  }

  private void write(File file, String content) throws IOException {
    File parent = file.getParentFile();
    if (parent != null && !parent.exists()) {
      parent.mkdirs();
    }
    Files.write(Paths.get(file.getAbsolutePath()), content.getBytes());
  }

  public static class Device {

    /** Retail branding */
    public final String manufacturer;

    /** The consumer friendly name of the device */
    @SerializedName("market_name")
    public final String marketName;

    /** The value of the system property "ro.product.device" */
    public final String codename;

    /** The value of the system property "ro.product.model" */
    public final String model;

    public Device(String manufacturer, String marketName, String codename, String model) {
      this.manufacturer = manufacturer;
      this.marketName = marketName;
      this.codename = codename;
      this.model = model;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Device device = (Device) o;

      if (manufacturer != null ? !manufacturer.equals(device.manufacturer) :
          device.manufacturer != null) return false;
      if (marketName != null ? !marketName.equals(device.marketName) : device.marketName != null)
        return false;
      if (codename != null ? !codename.equals(device.codename) : device.codename != null)
        return false;
      return !(model != null ? !model.equals(device.model) : device.model != null);

    }

    @Override public int hashCode() {
      int result = manufacturer != null ? manufacturer.hashCode() : 0;
      result = 31 * result + (marketName != null ? marketName.hashCode() : 0);
      result = 31 * result + (codename != null ? codename.hashCode() : 0);
      result = 31 * result + (model != null ? model.hashCode() : 0);
      return result;
    }

    @Override public String toString() {
      return "Device{" +
          "manufacturer='" + manufacturer + '\'' +
          ", marketName='" + marketName + '\'' +
          ", codename='" + codename + '\'' +
          ", model='" + model + '\'' +
          '}';
    }

  }

  public static class Manufacturer {

    /** Retail branding */
    public final String manufacturer;

    /** List of devices the manufacturer has that support Google Play */
    public final List<Device> devices;

    public Manufacturer(String manufacturer, List<Device> devices) {
      this.manufacturer = manufacturer;
      this.devices = devices;
    }

    public String getJsonFilename() {
      return manufacturer.toUpperCase(Locale.US)
          .replaceAll(" ", "_")
          .replaceAll("\\.", "")
          .replaceAll("-", "") + ".json";

    }

  }

}
