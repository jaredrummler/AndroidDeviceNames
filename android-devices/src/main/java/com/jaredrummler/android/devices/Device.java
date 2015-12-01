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

import com.google.gson.annotations.SerializedName;

public class Device {

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
