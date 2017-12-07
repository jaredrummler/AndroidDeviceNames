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

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.WorkerThread;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class DeviceNamesDatabase extends SQLiteOpenHelper {

  private static final String PREF_NAME = "AndroidDeviceNames";
  private static final String ASSET_NAME = "android-devices.zip";
  private static final String DATABASE_NAME = "android-devices.db";
  private static final int DATABASE_VERSION = 1;

  @WorkerThread
  public static DeviceNamesDatabase open(Context context) throws IOException {
    SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    File destination = context.getDatabasePath(DATABASE_NAME);
    if (!destination.exists() || preferences.getInt(DATABASE_NAME, DATABASE_VERSION) < DATABASE_VERSION) {
      File parentFile = destination.getParentFile();
      if (parentFile != null && !parentFile.exists()) {
        //noinspection ResultOfMethodCallIgnored
        parentFile.mkdirs();
      }

      InputStream is = context.getResources().getAssets().open(ASSET_NAME);
      ZipInputStream zis = new ZipInputStream(is);
      FileOutputStream fos = new FileOutputStream(destination);
      zis.getNextEntry();
      byte[] buffer = new byte[2048];
      int count;
      while ((count = zis.read(buffer)) != -1) {
        fos.write(buffer, 0, count);
      }
      zis.closeEntry();
      fos.close();

      preferences.edit().putInt(DATABASE_NAME, DATABASE_VERSION).apply();
    }

    return new DeviceNamesDatabase(context);
  }

  private DeviceNamesDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {

  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }

}
