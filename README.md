# Android Device Names

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jaredrummler/android-device-names/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jaredrummler/android-device-names) [![License](http://img.shields.io/:license-apache-blue.svg)](LICENSE.txt) [![API](https://img.shields.io/badge/API-7%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=7) <a href="http://www.methodscount.com/?lib=com.jaredrummler%3Aandroid-device-names%3A2.1.0" target="_blank"><img src="https://img.shields.io/badge/method count-86-e91e63.svg"></img></a> <a href="http://www.methodscount.com/?lib=com.jaredrummler%3Aandroid-device-names%3A2.1.0" target="_blank"><img src="https://img.shields.io/badge/size-34 KB-e91e63.svg"></img></a> [![Twitter Follow](https://img.shields.io/twitter/follow/jaredrummler.svg?style=social)](https://twitter.com/jaredrummler)

A small Android library to get the market name of an Android device.

On many popular devices the market name of the device is not available. For example, on the Samsung Galaxy S7 the value of [`Build.MODEL`](http://developer.android.com/reference/android/os/Build.html#MODEL) could be `"SAMSUNG-SM-G930A"`, `"SM-G930F"`, `"SM-G930K"`, `"SM-G930L"`, etc.

This small library gets the market (consumer friendly) name of a device.

Usage
-----

**Setup the library**

```java
DeviceName.init(this);
```

**Get the name of the current device:**

```java
String deviceName = DeviceName.getDeviceName();
```

The above code will get the correct device name for the top 600 Android devices. If the device is unrecognized, then [`Build.MODEL`](http://developer.android.com/reference/android/os/Build.html#MODEL) is returned. This can be executed from the UI thread.

**Get the name of a device using the device's codename:**

```java
// Returns "Moto X Style"
DeviceName.getDeviceName("clark", "Unknown device");
```

**Get information about the device:**

```java
DeviceName.with(context).request(new DeviceName.Callback() {

  @Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
    String manufacturer = info.manufacturer;  // "Samsung"
    String name = info.marketName;            // "Galaxy S8+"
    String model = info.model;                // "SM-G955W"
    String codename = info.codename;          // "dream2qltecan"
    String deviceName = info.getName();       // "Galaxy S8+"
    // FYI: We are on the UI thread.
  }
});
 ```

The above code queries a database included in the library based on [Google's maintained list
](https://support.google.com/googleplay/answer/1727131?hl=en). This supports *over 27,000* devices.

Download
--------

Download [the latest AAR](https://repo1.maven.org/maven2/com/jaredrummler/android-device-names/2.1.0/android-device-names-2.1.0.aar) or grab via Gradle:

```groovy
implementation('com.jaredrummler:android-device-names:2.1.0') 
```


License
--------

    Copyright (C) 2015 Jared Rummler

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
