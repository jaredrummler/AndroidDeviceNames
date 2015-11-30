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
import java.io.InputStream;
import java.util.List;

public class Main {

  // https://support.google.com/googleplay/answer/1727131?hl=en
  private static final String LATEST_DEVICE_LIST_XLS = "supported_devices_11-30-2015.xls";

  public static void main(String[] args) throws IOException {
    InputStream inputStream = ClassLoader.getSystemResourceAsStream(LATEST_DEVICE_LIST_XLS);
    DevicesParser parser = new DevicesParser();
    List<Device> devices = parser.getDevices(inputStream);
    DevicesToJson devicesToJson = new DevicesToJson(devices);
    devicesToJson.createDevicesJson(Constants.DEVICES_JSON);
    devicesToJson.createCodenamesJson(Constants.CODENAMES_DIR);
    devicesToJson.createManufacturersJson(Constants.MANUFACTURERS_DIR);
    devicesToJson.createPopularDevicesJson(Constants.POPULAR_DEVICES_JSON);
    DevicesToJava.printMethod(devices);
  }

}
