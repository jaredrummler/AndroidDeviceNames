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

package com.jaredrummler.androiddevicenames;

import android.database.Cursor;
import android.os.Build;

public class AndroidDeviceNames {

  private final DeviceNamesDatabase database;

  public AndroidDeviceNames(DeviceNamesDatabase database) {
    this.database = database;
  }

  public String getDeviceName() {
    return getDeviceName(Build.MODEL);
  }

  public String getDeviceName(String fallback) {
    return getDeviceName(Build.DEVICE, Build.MODEL, fallback);
  }

  public String getDeviceName(String codename, String model, String fallback) {
    if (codename == null) codename = "";
    if (model == null) model = "";

    Cursor query = database.getReadableDatabase().rawQuery("SELECT name FROM devices WHERE model = ? OR codename = ?",
        new String[] { model, codename });
    String deviceName = fallback;
    boolean duplicates = false;
    if (query != null) {
      try {
        duplicates = query.getCount() > 1;
        if (query.moveToFirst()) {
          deviceName = query.getString(0);
        }
      } finally {
        query.close();
      }
    }

    if (duplicates) {
      // check codename AND model
      query = database.getReadableDatabase().rawQuery("SELECT name FROM devices WHERE model = ? AND codename = ?",
          new String[] { model, codename });
      if (query != null) {
        try {
          if (query.moveToFirst()) {
            deviceName = query.getString(0);
          }
        } finally {
          query.close();
        }
      }
    }

    return deviceName;
  }

}
