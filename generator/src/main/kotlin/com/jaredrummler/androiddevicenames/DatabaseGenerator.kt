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

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.sql.DriverManager
import java.sql.SQLException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DatabaseGenerator(
    private val devices: List<Device>,
    private val databasename: String = "database/android-devices.db",
    private val zipname: String = "database/android-devices.zip"
) {

  fun generate() {
    val url = "jdbc:sqlite:$databasename"

    try {
      File(databasename).parentFile?.mkdirs()

      DriverManager.getConnection(url)?.let { conn ->
        conn.createStatement().execute(SQL_DROP)
        conn.createStatement().execute(SQL_CREATE)
        val statement = conn.prepareStatement(SQL_INSERT)
        devices.forEach { device ->
          statement.setString(1, device.marketName)
          statement.setString(2, device.codename)
          statement.setString(3, device.model)
          statement.addBatch()
        }
        statement.executeBatch()
        conn.close()
      }

      ZipOutputStream(BufferedOutputStream(FileOutputStream(zipname))).use { out ->
        FileInputStream(databasename).use { fi ->
          BufferedInputStream(fi).use { origin ->
            val entry = ZipEntry(databasename)
            out.putNextEntry(entry)
            origin.copyTo(out, 1024)
          }
        }
      }
    } catch (e: SQLException) {
      e.printStackTrace()
    }
  }

  companion object {
    private const val SQL_INSERT = "INSERT INTO devices (name, codename, model) VALUES (?, ?, ?)"
    private const val SQL_DROP = "DROP TABLE IF EXISTS devices;"
    private const val SQL_CREATE = "CREATE TABLE devices (\n" +
        "_id INTEGER PRIMARY KEY,\n" +
        "name TEXT,\n" +
        "codename TEXT,\n" +
        "model TEXT\n" +
        ");"
  }

}