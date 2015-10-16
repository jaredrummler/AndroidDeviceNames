# AndroidDeviceNames
A small Android library to get the market name of an Android device.
___

Unfortunately, on many popular devices, it is not easy to get the market name of the device. For example, on the Samsung Galaxy S6 the value of [Build.MODEL](http://developer.android.com/reference/android/os/Build.html#MODEL) could be "SM-G920F", "SM-G920I", "SM-G920W8", etc.

This small library gets the market (consumer friendly) name of a device. You can use one (or both) of the following examples:

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
 
 The above code loads JSON from a generated list of device names based on Google's maintained list and contains around 10,000 devices. This needs a network connection and is run in a background thread.
 
