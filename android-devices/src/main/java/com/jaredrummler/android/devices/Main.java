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
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class Main {

  // https://support.google.com/googleplay/answer/1727131?hl=en
  private static final String LATEST_DEVICE_LIST_XLS = "supported_devices_11-30-2015.xls";

  public static void main(String[] args) throws Exception {
    Main main = new Main();
    List<Device> devices = main.getLatestDevices();
    //main.createJsonFiles(devices);
    DevicesToJava.printGetDeviceNameMethod(devices);
  }

  private final Gson GSON = new Gson();

  private final Type DEVICE_TYPE = new TypeToken<List<Device>>() {

  }.getType();

  public List<Device> getDevicesFromJson(File file) throws FileNotFoundException {
    return GSON.fromJson(new JsonReader(new FileReader(file)), DEVICE_TYPE);
  }

  public List<Device> getDeviceFromXls(InputStream inputStream) throws IOException {
    return new DevicesParser().getDevices(inputStream);
  }

  public List<Device> getLatestDevices() throws IOException {
    return getDeviceFromXls(ClassLoader.getSystemResourceAsStream(LATEST_DEVICE_LIST_XLS));
  }

  public void createJsonFiles(List<Device> devices) throws IOException {
    DevicesToJson devicesToJson = new DevicesToJson(devices);
    devicesToJson.createDevicesJson(Constants.DEVICES_JSON);
    devicesToJson.createCodenamesJson(Constants.CODENAMES_DIR);
    devicesToJson.createManufacturersJson(Constants.MANUFACTURERS_DIR);
    devicesToJson.createPopularDevicesJson(Constants.POPULAR_DEVICES_JSON);
  }

}
