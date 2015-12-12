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

import java.io.File;

public class Constants {

  // https://support.google.com/googleplay/answer/1727131?hl=en
  public static final String LATEST_XLS = "supported_devices_12-12-15.xls";

  public static final File JSON_DIR = new File("json");

  public static final File CODENAMES_DIR = new File(JSON_DIR, "codenames");

  public static final File MANUFACTURERS_DIR = new File(JSON_DIR, "manufacturers");

  public static final File POPULAR_DEVICES_JSON = new File(JSON_DIR, "popular-devices.json");

  public static final File DEVICES_JSON = new File(JSON_DIR, "devices.json");

  public static final String[] POPULAR_DEVICES = {
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
      // Asus
      "MeMO Pad 7",
      "Nexus 7 (2012)",
      "Nexus 7 (2013)",
      "ZenFone 5",
      // Dell
      "Venue 7",
      "Venue 8",
      // HTC
      "HTC One",
      "HTC One (E8)",
      "HTC One (M8)",
      "HTC One A9",
      "HTC One M9",
      "HTC One S",
      "Nexus 9",
      // Huawei
      "Honor3",
      "Mate S",
      "Nexus 6P",
      // LGE
      "LG G Flex",
      "LG G Flex2",
      "LG G2",
      "LG G3",
      "LG G4",
      "LG Leon 4G LTE",
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
      // Lenovo
      "Lenovo A7-30GC",
      // Motorola
      "DROID Turbo",
      "MOTO E",
      "MOTO G",
      "MOTO X",
      "Moto X Style",
      "Nexus 6",
      "XOOM",
      // OnePlus
      "OnePlus",
      "OnePlus One",
      "OnePlus2",
      // Samsung
      "Galaxy A3",
      "Galaxy A5",
      "Galaxy A8",
      "Galaxy Ace 4",
      "Galaxy Ace Duos",
      "Galaxy Ace Plus",
      "Galaxy Ace Style",
      "Galaxy Ace4",
      "Galaxy Alpha",
      "Galaxy Core Prime",
      "Galaxy Core2",
      "Galaxy E5",
      "Galaxy E7",
      "Galaxy Fame",
      "Galaxy Go Prime",
      "Galaxy Grand Neo",
      "Galaxy Grand Prime",
      "Galaxy Grand2",
      "Galaxy J7",
      "Galaxy Nexus",
      "Galaxy Note 10.1",
      "Galaxy Note Edge",
      "Galaxy Note Pro 12.2",
      "Galaxy Note2",
      "Galaxy Note3",
      "Galaxy Note3 Neo",
      "Galaxy Note4",
      "Galaxy Note5",
      "Galaxy On5",
      "Galaxy On7",
      "Galaxy S Duos",
      "Galaxy S Duos2",
      "Galaxy S Duos3",
      "Galaxy S2",
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
      "Galaxy Tab 10.1",
      "Galaxy Tab 7.0 Plus",
      "Galaxy Tab E 8.0",
      "Galaxy Tab E 9.6",
      "Galaxy Tab4 7.0",
      "Galaxy View",
      "Galaxy Y",
      "Nexus 10",
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
      // Sony Ericsson
      "Xperia S",
      "Xperia Tablet Z",
  };

}
