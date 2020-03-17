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

package com.jaredrummler.android.device;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import androidx.annotation.WorkerThread;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>Get the consumer friendly name of an Android device.</p>
 *
 * <p>On many popular devices the market name of the device is not available. For example, on the
 * Samsung Galaxy S6 the value of {@link Build#MODEL} could be "SM-G920F", "SM-G920I", "SM-G920W8",
 * etc.</p>
 *
 * <p>See the usages below to get the consumer friends name of a device:</p>
 *
 * <p><b>Get the name of the current device:</b></p>
 *
 * <pre>
 * String deviceName = DeviceName.getDeviceName();
 * </pre>
 *
 * <p>The above code will get the correct device name for the top 600 Android devices. If the
 * device is unrecognized, then Build.MODEL is returned.</p>
 *
 * <p><b>Get the name of a device using the device's codename:</b></p>
 *
 * <pre>
 * // Retruns "Moto X Style"
 * DeviceName.getDeviceName("clark", "Unknown device");
 * </pre>
 *
 * <p><b>Get information about the device:</b></p>
 *
 * <pre>
 * DeviceName.with(context).request(new DeviceName.Callback() {
 *
 *   &#64;Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
 *     String manufacturer = info.manufacturer;  // "Samsung"
 *     String name = info.marketName;            // "Galaxy S6 Edge"
 *     String model = info.model;                // "SM-G925I"
 *     String codename = info.codename;          // "zerolte"
 *     String deviceName = info.getName();       // "Galaxy S6 Edge"
 *     // FYI: We are on the UI thread.
 *   }
 * });
 * </pre>
 *
 * <p>The above code loads JSON from a generated list of device names based on Google's maintained
 * list. It will be up-to-date with Google's supported device list so that you will get the correct
 * name for new or unknown devices. This supports over 10,000 devices.</p>
 *
 * <p>This will only make a network call once. The value is saved to SharedPreferences for future
 * calls.</p>
 */
public class DeviceName {

  // Preference filename for storing device info so we don't need to download it again.
  private static final String SHARED_PREF_NAME = "device_names";

  @SuppressLint("StaticFieldLeak") // application context is safe
  private static Context context;

  /**
   * Initialize DeviceName. This should be done in the application class.
   */
  public static void init(Context context) {
    DeviceName.context = context.getApplicationContext();
  }

  /**
   * Create a new request to get information about a device.
   *
   * @param context the application context
   * @return a new Request instance.
   */
  public static Request with(Context context) {
    return new Request(context.getApplicationContext());
  }

  /**
   * Get the consumer friendly name of the device.
   *
   * @return the market name of the current device.
   * @see #getDeviceName(String, String)
   */
  public static String getDeviceName() {
    return getDeviceName(Build.DEVICE, Build.MODEL, capitalize(Build.MODEL));
  }

  /**
   * Get the consumer friendly name of a device.
   *
   * @param codename the value of the system property "ro.product.device" ({@link Build#DEVICE})
   * <i>or</i>
   * the value of the system property "ro.product.model" ({@link Build#MODEL})
   * @param fallback the fallback name if the device is unknown. Usually the value of the system
   * property "ro.product.model" ({@link Build#MODEL})
   * @return the market name of a device or {@code fallback} if the device is unknown.
   */
  public static String getDeviceName(String codename, String fallback) {
    return getDeviceName(codename, codename, fallback);
  }

  /**
   * Get the consumer friendly name of a device.
   *
   * @param codename the value of the system property "ro.product.device" ({@link Build#DEVICE}).
   * @param model the value of the system property "ro.product.model" ({@link Build#MODEL}).
   * @param fallback the fallback name if the device is unknown. Usually the value of the system
   * property "ro.product.model" ({@link Build#MODEL})
   * @return the market name of a device or {@code fallback} if the device is unknown.
   */
  public static String getDeviceName(String codename, String model, String fallback) {
    String marketName = getDeviceInfo(context(), codename, model).marketName;
    return marketName == null ? fallback : marketName;
  }

  /**
   * Get the {@link DeviceInfo} for the current device. Do not run on the UI thread, as this may
   * download JSON to retrieve the {@link DeviceInfo}. JSON is only downloaded once and then
   * stored to {@link SharedPreferences}.
   *
   * @param context the application context.
   * @return {@link DeviceInfo} for the current device.
   */
  @WorkerThread
  public static DeviceInfo getDeviceInfo(Context context) {
    return getDeviceInfo(context.getApplicationContext(), Build.DEVICE, Build.MODEL);
  }

  /**
   * Get the {@link DeviceInfo} for the current device. Do not run on the UI thread, as this may
   * download JSON to retrieve the {@link DeviceInfo}. JSON is only downloaded once and then
   * stored to {@link SharedPreferences}.
   *
   * @param context the application context.
   * @param codename the codename of the device
   * @return {@link DeviceInfo} for the current device.
   */
  @WorkerThread
  public static DeviceInfo getDeviceInfo(Context context, String codename) {
    return getDeviceInfo(context, codename, null);
  }

  /**
   * Get the {@link DeviceInfo} for the current device. Do not run on the UI thread, as this may
   * download JSON to retrieve the {@link DeviceInfo}. JSON is only downloaded once and then
   * stored to {@link SharedPreferences}.
   *
   * @param context the application context.
   * @param codename the codename of the device
   * @param model the model of the device
   * @return {@link DeviceInfo} for the current device.
   */
  @WorkerThread
  public static DeviceInfo getDeviceInfo(Context context, String codename, String model) {
    SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    String key = String.format("%s:%s", codename, model);
    String savedJson = prefs.getString(key, null);
    if (savedJson != null) {
      try {
        return new DeviceInfo(new JSONObject(savedJson));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    try (DeviceDatabase database = new DeviceDatabase(context)) {
      DeviceInfo info = database.queryToDevice(codename, model);
      if (info != null) {
        JSONObject json = new JSONObject();
        json.put("manufacturer", info.manufacturer);
        json.put("codename", info.codename);
        json.put("model", info.model);
        json.put("market_name", info.marketName);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, json.toString());
        editor.apply();
        return info;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (codename.equals(Build.DEVICE) && Build.MODEL.equals(model)) {
      return new DeviceInfo(Build.MANUFACTURER, codename, codename, model); // current device
    }

    return new DeviceInfo(null, null, codename, model); // unknown device
  }

  /**
   * <p>Capitalizes getAllProcesses the whitespace separated words in a String. Only the first
   * letter of each word is changed.</p>
   *
   * Whitespace is defined by {@link Character#isWhitespace(char)}.
   *
   * @param str the String to capitalize
   * @return capitalized The capitalized String
   */
  private static String capitalize(String str) {
    if (TextUtils.isEmpty(str)) {
      return str;
    }
    char[] arr = str.toCharArray();
    boolean capitalizeNext = true;
    StringBuilder phrase = new StringBuilder();
    for (char c : arr) {
      if (capitalizeNext && Character.isLetter(c)) {
        phrase.append(Character.toUpperCase(c));
        capitalizeNext = false;
        continue;
      } else if (Character.isWhitespace(c)) {
        capitalizeNext = true;
      }
      phrase.append(c);
    }
    return phrase.toString();
  }

  public static final class Request {

    final Context context;
    final Handler handler;
    String codename;
    String model;

    private Request(Context ctx) {
      context = ctx;
      handler = new Handler(ctx.getMainLooper());
    }

    /**
     * Set the device codename to query. You should also set the model.
     *
     * @param codename the value of the system property "ro.product.device"
     * @return This Request object to allow for chaining of calls to set methods.
     * @see Build#DEVICE
     */
    public Request setCodename(String codename) {
      this.codename = codename;
      return this;
    }

    /**
     * Set the device model to query. You should also set the codename.
     *
     * @param model the value of the system property "ro.product.model"
     * @return This Request object to allow for chaining of calls to set methods.
     * @see Build#MODEL
     */
    public Request setModel(String model) {
      this.model = model;
      return this;
    }

    /**
     * Download information about the device. This saves the results in shared-preferences so
     * future requests will not need a network connection.
     *
     * @param callback the callback to retrieve the {@link DeviceName.DeviceInfo}
     */
    public void request(Callback callback) {
      if (codename == null && model == null) {
        codename = Build.DEVICE;
        model = Build.MODEL;
      }
      GetDeviceRunnable runnable = new GetDeviceRunnable(callback);
      if (Looper.myLooper() == Looper.getMainLooper()) {
        new Thread(runnable).start();
      } else {
        runnable.run(); // already running in background thread.
      }
    }

    private final class GetDeviceRunnable implements Runnable {

      final Callback callback;
      DeviceInfo deviceInfo;
      Exception error;

      GetDeviceRunnable(Callback callback) {
        this.callback = callback;
      }

      @Override
      public void run() {
        try {
          deviceInfo = getDeviceInfo(context, codename, model);
        } catch (Exception e) {
          error = e;
        }
        handler.post(new Runnable() {

          @Override
          public void run() {
            callback.onFinished(deviceInfo, error);
          }
        });
      }
    }
  }

  /**
   * Callback which is invoked when the {@link DeviceName.DeviceInfo} is finished loading.
   */
  public interface Callback {

    /**
     * Callback to get the device info. This is run on the UI thread.
     *
     * @param info the requested {@link DeviceName.DeviceInfo}
     * @param error {@code null} if nothing went wrong.
     */
    void onFinished(DeviceInfo info, Exception error);
  }

  /**
   * Device information based on
   * <a href="https://support.google.com/googleplay/answer/1727131">Google's maintained list</a>.
   */
  public static final class DeviceInfo {

    /** Retail branding */
    @Deprecated
    public final String manufacturer;

    /** Marketing name */
    public final String marketName;

    /** the value of the system property "ro.product.device" */
    public final String codename;

    /** the value of the system property "ro.product.model" */
    public final String model;

    public DeviceInfo(String marketName, String codename, String model) {
      this(null, marketName, codename, model);
    }

    public DeviceInfo(String manufacturer, String marketName, String codename, String model) {
      this.manufacturer = manufacturer;
      this.marketName = marketName;
      this.codename = codename;
      this.model = model;
    }

    private DeviceInfo(JSONObject jsonObject) throws JSONException {
      manufacturer = jsonObject.getString("manufacturer");
      marketName = jsonObject.getString("market_name");
      codename = jsonObject.getString("codename");
      model = jsonObject.getString("model");
    }

    /**
     * @return the consumer friendly name of the device.
     */
    public String getName() {
      if (!TextUtils.isEmpty(marketName)) {
        return marketName;
      }
      return capitalize(model);
    }
  }

  @SuppressLint("PrivateApi")
  private static Context context() {
    if (context != null) return context;

    // We didn't use to require holding onto the application context so let's cheat a little.
    try {
      return (Application) Class.forName("android.app.ActivityThread")
          .getMethod("currentApplication")
          .invoke(null, (Object[]) null);
    } catch (Exception ignored) {
    }

    // Last attempt at hackery
    try {
      return (Application) Class.forName("android.app.AppGlobals")
          .getMethod("getInitialApplication")
          .invoke(null, (Object[]) null);
    } catch (Exception ignored) {
    }

    throw new RuntimeException("DeviceName must be initialized before usage.");
  }
}
