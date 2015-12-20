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
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class Main {

  public static void main(String[] args) throws Exception {
    if (!isLatest()) {
      System.out.println(
          "There is an updated device list at http://storage.googleapis.com/play_public/supported_devices.csv");
      return;
    }
    Main main = new Main();
    List<Device> devices = main.getLatestDevices();
    main.createJsonFiles(devices);
    DevicesToJava.printGetDeviceNameMethod(devices);
  }

  public List<Device> getDevicesFromJson(File file) throws FileNotFoundException {
    final Gson GSON = new Gson();
    final Type DEVICE_TYPE = new TypeToken<List<Device>>() {

    }.getType();
    return GSON.fromJson(new JsonReader(new FileReader(file)), DEVICE_TYPE);
  }

  public List<Device> getLatestDevices() throws IOException {
    return DevicesParser.parse(Constants.LATEST_XLS);
  }

  public void createJsonFiles(List<Device> devices) throws IOException {
    DevicesToJson toJson = new DevicesToJson(devices);
    toJson.createLegacyJson();
    toJson.createDevicesJson(Constants.DEVICES_JSON);
    toJson.createCodenamesJson(Constants.CODENAMES_DIR);
    toJson.createManufacturersJson(Constants.MANUFACTURERS_DIR);
    toJson.createPopularDevicesJson(Constants.POPULAR_DEVICES_JSON);
  }

  public static boolean isLatest() throws IOException {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url("http://storage.googleapis.com/play_public/supported_devices.csv")
        .build();
    Response response = client.newCall(request).execute();
    int length = Integer.parseInt(response.header("Content-Length"));
    InputStream latestCsv = ClassLoader.getSystemResourceAsStream("supported_devices-12-20-2015.csv");
    int available = latestCsv.available();
    return available == length;
  }

}
