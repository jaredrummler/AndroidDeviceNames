# AndroidDeviceNames
A small Android library to get the market name of an Android device.
___
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-AndroidDeviceNames-green.svg?style=flat)](https://android-arsenal.com/details/1/2663)


Unfortunately, on many popular devices, the market name of the device is not available. For example, on the Samsung Galaxy S6 the value of [Build.MODEL](http://developer.android.com/reference/android/os/Build.html#MODEL) could be "SM-G920F", "SM-G920I", "SM-G920W8", etc.

This small library gets the market (consumer friendly) name of a device. You can use one (or both) of the following examples:

Download
--------

Download [the latest AAR](https://repo1.maven.org/maven2/com/jaredrummler/android-device-names/1.0.1/android-device-names-1.0.1.aar) or grab via Gradle:

```groovy
compile 'com.jaredrummler:android-device-names:1.0.1'
```

Or simply copy the [DeviceName](https://raw.githubusercontent.com/jaredrummler/AndroidDeviceNames/master/library/src/main/java/com/jaredrummler/android/device/DeviceName.java) class intro your project, update the package declaration, and you are good to go.


Examples
--------

<b>Example 1</b>

```java
String deviceName = DeviceName.getDeviceName();
```

`getDeviceName()` contains over 600 popular Android devices and can be run on the UI thread. If the current device is not in the list then [Build.MODEL](http://developer.android.com/reference/android/os/Build.html#MODEL) will be returned as a fallback.

<b>Example 2</b>

```java
DeviceName.with(context).request(new DeviceName.Callback() {

  @Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
    String deviceName;
    if (error != null) {
      deviceName = info.getName();
    } else {
      deviceName = DeviceName.getDeviceName();
    }
  }
 });
 ```
 
The above code loads [JSON from a generated list](https://github.com/jaredrummler/AndroidDeviceNames/tree/master/json) of device names based on [Google's maintained list](https://support.google.com/googleplay/answer/1727131?hl=en). It will be up-to-date with Google's supported device list so that you will get the correct name for new or unknown devices.

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
