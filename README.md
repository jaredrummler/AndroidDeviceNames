# Android Device Names [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jaredrummler/android-device-names/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jaredrummler/android-device-names) [![License](http://img.shields.io/:license-apache-blue.svg)](LICENSE.txt)

A small Android library to get the market name of an Android device.

Unfortunately, on many popular devices, the market name of the device is not available. For example, on the Samsung Galaxy S6 the value of [`Build.MODEL`](http://developer.android.com/reference/android/os/Build.html#MODEL) could be `"SM-G920F"`, `"SM-G920I"`, or `"SM-G920W8"`.

This small library gets the market (consumer friendly) name of a device.

Usage
-----

**Get the name of the current device:**

```java
String deviceName = DeviceName.getDeviceName();
```

The above code will get the correct device name for the top 600 Android devices. If the device is unrecognized, then [`Build.MODEL`](http://developer.android.com/reference/android/os/Build.html#MODEL) is returned. This can be executed from the UI thread.

**Get the name of a device using the device's codename:**

```java
// Retruns "Moto X Style"
DeviceName.getDeviceName("clark", "Unknown device");
```

**Get information about the device:**

```java
DeviceName.with(this).request(new DeviceName.Callback() {

  @Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
    String manufacturer = info.manufacturer;  // "Samsung"
    String name = info.marketName;            // "Galaxy S6 Edge"
    String model = info.model;                // "SM-G925I"
    String codename = info.codename;          // "zerolte"
    String deviceName = info.getName();       // "Galaxy S6 Edge"
  }
});
 ```

The above code loads [JSON from a generated list](https://github.com/jaredrummler/AndroidDeviceNames/tree/master/json) of device names based on [Google's maintained list](https://support.google.com/googleplay/answer/1727131?hl=en). It will be up-to-date with Google's supported device list so that you will get the correct name for new or unknown devices. This supports *over 10,000* devices.

This will only make a network call once. The value is saved to SharedPreferences for future calls.

Download
--------

Download [the latest AAR](https://repo1.maven.org/maven2/com/jaredrummler/android-device-names/1.0.2/android-device-names-1.0.2.aar) or grab via Gradle:

```groovy
compile 'com.jaredrummler:android-device-names:1.0.2'
```

Or simply copy the [DeviceName](https://raw.githubusercontent.com/jaredrummler/AndroidDeviceNames/master/library/src/main/java/com/jaredrummler/android/device/DeviceName.java) class intro your project, update the package declaration, and you are good to go.

License
--------

    Copyright (C) 2015. Jared Rummler

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
