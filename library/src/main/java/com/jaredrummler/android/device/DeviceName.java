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

package com.jaredrummler.android.device;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>Get the consumer friendly name of an Android device.</p>
 *
 * <p>Unfortunately, on many popular devices, it is not easy to get the market name of the
 * device. For example, on the Samsung Galaxy S6 the value of {@link Build#MODEL}
 * could be "SM-G920F", "SM-G920I", "SM-G920W8", etc.</p>
 *
 * <p>To get the market (consumer friendly) name of a device you can use one (or both) of the
 * following examples:</p>
 *
 * <b>Example 1:</b>
 *
 * <br>
 * <p>{@code String deviceName = DeviceName.getDeviceName();}</p>
 * <b>Example 2:</b>
 * <br>
 * <pre>
 * <code>
 *   DeviceName.with(context).request(new DeviceName.Callback() {
 *
 *       {@literal @}Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
 *           String deviceName;
 *           if (error != null) {
 *               deviceName = info.getName();
 *           } else {
 *               deviceName = DeviceName.getDeviceName();
 *           }
 *       }
 *   });
 * </code>
 * </pre>
 *
 * <p><b>Example 1:</b> contains over 600 popular Android devices and can be run on the UI thread.
 * If the current device is not in the list then {@link Build#MODEL} will be returned as a
 * fallback.</p>
 *
 * <p><b>Example 2:</b> loads JSON from a generated list of device names based on Google's
 * maintained list and contains around 10,000 devices. This needs a network connection and is run
 * in a background thread.</p>
 *
 * @author Jared Rummler
 */
public class DeviceName {

  // JSON which is derived from Google's PDF document which contains all devices on Google Play
  // To get the URL to the JSON file which contains information about the device name:
  // String url = String.format(DEVICE_JSON_URL, Build.DEVICE);
  private static final String DEVICE_JSON_URL =
      "https://raw.githubusercontent.com/jaredrummler/AndroidDeviceNames/master/json/codenames/%s.json";

  // Preference filename for storing device info so we don't need to download it again.
  private static final String SHARED_PREF_NAME = "device_names";

  /**
   * Create a new request to get information about a device.
   *
   * @param context
   *     the application context
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
    String manufacturer = Build.MANUFACTURER;
    String model = Build.MODEL;
    String fallback;
    if (model.startsWith(manufacturer)) {
      fallback = capitalize(model);
    } else {
      fallback = capitalize(manufacturer) + " " + model;
    }
    return getDeviceName(Build.DEVICE, fallback);
  }

  /**
   * Get the consumer friendly name of a device.
   *
   * @param codename
   *     the value of the system property "ro.product.device" ({@link Build#DEVICE}).
   * @param fallback
   *     the fallback name if the device is unknown. Usually the value of the system property
   *     "ro.product.model" ({@link Build#MODEL})
   * @return the market name of a device or {@code fallback} if the device is unknown.
   */
  public static String getDeviceName(String codename, String fallback) {
    switch (codename) {
      case "quark":
        return "DROID Turbo";
      case "a3ltedd":
      case "a3ulte":
      case "a3ltezh":
      case "a3ltechn":
      case "a3lte":
      case "a33g":
      case "a3ltectc":
      case "a3lteslk":
      case "a3ltezt":
        return "Galaxy A3";
      case "a5ltectc":
      case "a5ltezh":
      case "a5ulteskt":
      case "a53g":
      case "a5ltezt":
      case "a5lte":
      case "a5ltechn":
      case "a5ulte":
      case "a5ultektt":
      case "a5ultebmc":
      case "a5ultelgt":
        return "Galaxy A5";
      case "a8elte":
      case "a8hplte":
      case "a8elteskt":
      case "a8ltechn":
        return "Galaxy A8";
      case "vivaltods5m":
        return "Galaxy Ace 4";
      case "GT-S6802B":
      case "GT-S6802":
      case "SCH-i589":
      case "SCH-i579":
      case "GT-S6352":
      case "SCH-I589":
      case "SCH-I579":
        return "Galaxy Ace Duos";
      case "GT-S7500L":
      case "GT-S7500":
      case "GT-S7500T":
      case "GT-S7508":
      case "GT-S7500W":
        return "Galaxy Ace Plus";
      case "heatqlte":
      case "heat3gtfnvzw":
      case "heatnfc3g":
        return "Galaxy Ace Style";
      case "vivaltolte":
      case "vivalto5mve3g":
      case "vivaltonfc3g":
      case "vivalto3g":
      case "vivalto3mve3g":
        return "Galaxy Ace4";
      case "sltechn":
      case "sltelgt":
      case "slteskt":
      case "sltektt":
      case "sltecan":
      case "slteatt":
      case "slte":
        return "Galaxy Alpha";
      case "rossalte":
      case "coreprimeltespr":
      case "rossaltexsa":
      case "coreprimeltelra":
      case "coreprimelte":
      case "coreprimeltevzw":
      case "coreprimeve3g":
      case "core33g":
      case "cprimeltetmo":
      case "coreprimelteaio":
      case "cprimeltemtr":
      case "rossaltectc":
      case "coreprimeltetfnvzw":
      case "coreprimevelte":
        return "Galaxy Core Prime";
      case "kanas3g":
      case "kanas3gnfc":
      case "kanas3gcmcc":
      case "kanas":
      case "kanas3gctc":
        return "Galaxy Core2";
      case "e5lte":
      case "e53g":
      case "e5ltetfnvzw":
      case "e5ltetw":
        return "Galaxy E5";
      case "e7lte":
      case "e7ltehktw":
      case "e7ltechn":
      case "e73g":
      case "e7ltectc":
        return "Galaxy E7";
      case "nevis":
      case "SCH-I629":
      case "nevis3gcmcc":
      case "nevisw":
      case "nevis3g":
      case "nevisp":
      case "nevisnvess":
      case "nevisvess":
      case "nevisds":
        return "Galaxy Fame";
      case "grandprimelteatt":
        return "Galaxy Go Prime";
      case "baffinq3g":
      case "baffinlitedtv":
      case "baffinlite":
        return "Galaxy Grand Neo";
      case "gprimelteacg":
      case "gprimelteusc":
      case "grandprimeveltezt":
      case "grandprimelte":
      case "fortunaltezh":
      case "gprimeltetfnvzw":
      case "gprimeltemtr":
      case "fortunalte":
      case "gprimeltecan":
      case "fortunaltectc":
      case "grandprimevelteltn":
      case "gprimeltespr":
      case "fortuna3gdtv":
      case "gprimeltetmo":
      case "fortuna3g":
      case "grandprimeve3g":
      case "fortunaltezt":
      case "grandprimelteaio":
      case "grandprimevelte":
      case "grandprimeve3gdtv":
        return "Galaxy Grand Prime";
      case "ms013g":
      case "ms01lte":
      case "ms013gdtv":
      case "ms01ltelgt":
      case "ms013gss":
      case "ms01lteskt":
      case "ms01ltektt":
        return "Galaxy Grand2";
      case "j75ltektt":
      case "j7ltechn":
      case "j7e3g":
      case "j7elte":
        return "Galaxy J7";
      case "toroplus":
      case "maguro":
      case "toro":
        return "Galaxy Nexus";
      case "p4noterf":
      case "p4noteltektt":
      case "p4notewifiww":
      case "p4noterfktt":
      case "lt03ltektt":
      case "p4notewifi43241any":
      case "p4noteltespr":
      case "p4noteltevzw":
      case "p4notelte":
      case "p4noteltelgt":
      case "lt03ltelgt":
      case "p4notelteskt":
      case "lt033g":
      case "lt03lteskt":
      case "p4notelteusc":
      case "p4notewifi":
      case "p4notewifiany":
      case "p4notewifiktt":
        return "Galaxy Note 10.1";
      case "tbltechn":
      case "tbltecan":
      case "tblteatt":
      case "tbeltelgt":
      case "tbltevzw":
      case "tbeltektt":
      case "tbelteskt":
      case "tbltespr":
      case "tblteusc":
      case "SCL24":
      case "tblte":
      case "tbltetmo":
        return "Galaxy Note Edge";
      case "v1awifi":
      case "v1awifikx":
      case "viennalte":
      case "viennaltevzw":
      case "v1a3g":
      case "viennaltekx":
      case "viennalteatt":
        return "Galaxy Note Pro 12.2";
      case "t0ltecmcc":
      case "SC-02E":
      case "t03gchn":
      case "t0ltevzw":
      case "t0ltetmo":
      case "t03g":
      case "t0ltespr":
      case "t0ltelgt":
      case "t0lteskt":
      case "t03gcmcc":
      case "t0lteatt":
      case "t03gchnduos":
      case "t0lteusc":
      case "t03gctc":
      case "t0lte":
      case "t0ltektt":
      case "t0ltedcm":
      case "t03gcuduos":
      case "t0ltecan":
        return "Galaxy Note2";
      case "SC-02F":
      case "hltelgt":
      case "hlteskt":
      case "hltetmo":
      case "hlte":
      case "hltespr":
      case "hlteatt":
      case "htdlte":
      case "SCL22":
      case "hltektt":
      case "hlteusc":
      case "hltevzw":
      case "ha3g":
      case "hltecan":
        return "Galaxy Note3";
      case "hl3gds":
      case "hllte":
      case "frescoltektt":
      case "hl3g":
      case "frescolteskt":
      case "frescoltelgt":
        return "Galaxy Note3 Neo";
      case "tre3caltelgt":
      case "trltechn":
      case "trlte":
      case "tre3calteskt":
      case "trlteusc":
      case "tre3g":
      case "treltektt":
      case "trlteatt":
      case "trltechnzh":
      case "tre3caltektt":
      case "treltelgt":
      case "trltevzw":
      case "trltespr":
      case "trltetmo":
      case "trelteskt":
      case "trltecan":
      case "trelte":
      case "trhplte":
        return "Galaxy Note4";
      case "noblelteacg":
      case "noblelteusc":
      case "nobleltechn":
      case "nobleltektt":
      case "nobleltecmcc":
      case "nobleltevzw":
      case "noblelte":
      case "nobleltetmo":
      case "nobleltespr":
      case "nobleltebmc":
      case "nobleltehk":
      case "nobleltelgt":
      case "noblelteskt":
      case "nobleltelra":
      case "noblelteatt":
        return "Galaxy Note5";
      case "o5lte":
      case "o5ltechn":
        return "Galaxy On5";
      case "o7ltechn":
      case "o7lte":
        return "Galaxy On7";
      case "kyletdcmcc":
      case "kylechn":
      case "kyleichn":
      case "kyleopen":
        return "Galaxy S Duos";
      case "kyleprods":
        return "Galaxy S Duos2";
      case "vivalto3gvn":
        return "Galaxy S Duos3";
      case "SHW-M250L":
      case "GT-I9108":
      case "SHW-M250K":
      case "SCH-R760X":
      case "SC-02C":
      case "SGH-T989":
      case "GT-I9100":
      case "SHW-M250S":
      case "GT-I9103":
      case "GT-I9100M":
      case "GT-I9100P":
      case "GT-I9210T":
      case "GT-I9100T":
      case "SGH-I777":
      case "t1cmcc":
      case "SHV-E110S":
      case "SGH-S959G":
        return "Galaxy S2";
      case "SC-03E":
      case "d2xar":
      case "d2mtr":
      case "d2spi":
      case "d2vmu":
      case "d2att":
      case "m0skt":
      case "d2lterefreshspr":
      case "d2tmo":
      case "d2cri":
      case "m0ctc":
      case "d2dcm":
      case "d2can":
      case "c1lgt":
      case "m0":
      case "c1skt":
      case "m3":
      case "d2lteMetroPCS":
      case "d2ltetmo":
      case "d2usc":
      case "m3dcm":
      case "m0chn":
      case "d2tfnvzw":
      case "m0apt":
      case "c1ktt":
      case "d2tfnspr":
      case "c1att":
      case "d2vzw":
      case "m0ctcduos":
      case "d2spr":
      case "m0cmcc":
        return "Galaxy S3";
      case "goldenltevzw":
      case "goldenlteatt":
      case "golden":
      case "goldenve3g":
      case "goldenltebmc":
        return "Galaxy S3 Mini";
      case "s3ve3g":
      case "s3ve3gdsdd":
      case "s3ve3gds":
      case "s3ve3gdd":
        return "Galaxy S3 Neo";
      case "jalteskt":
      case "jfltetmo":
      case "ks01ltektt":
      case "jfvelte":
      case "jaltelgt":
      case "jfltecri":
      case "jfltelra":
      case "jfltetfntmo":
      case "jflterefreshspr":
      case "jflteusc":
      case "ks01ltelgt":
      case "jflteaio":
      case "jfltecan":
      case "ks01lte":
      case "jfltespr":
      case "jfltevzwpp":
      case "SC-04E":
      case "jflte":
      case "jfltevzw":
      case "jfwifi":
      case "jfltecsp":
      case "jfltetfnatt":
      case "jaltektt":
      case "jflteatt":
      case "jsglte":
      case "ja3g":
      case "ja3gduosctc":
      case "jftdd":
        return "Galaxy S4";
      case "serranoltektt":
      case "serranolteusc":
      case "serrano3g":
      case "serranove3g":
      case "serranovolteatt":
      case "serranoltelra":
      case "serranods":
      case "serranovelte":
      case "serranoltebmc":
      case "serranoltespr":
      case "serranoltevzw":
      case "serranoltekx":
      case "serranolte":
        return "Galaxy S4 Mini";
      case "klte":
      case "kltektt":
      case "klteduoszn":
      case "kltecan":
      case "klteMetroPCS":
      case "lentisltelgt":
      case "lentislteskt":
      case "klteacg":
      case "klteusc":
      case "SCL23":
      case "klteaio":
      case "kltelra":
      case "klteskt":
      case "lentisltektt":
      case "klteatt":
      case "kltevzw":
      case "kltespr":
      case "kwifi":
      case "kltetmo":
      case "k3g":
      case "kltelgt":
        return "Galaxy S5";
      case "s5neoltecan":
      case "s5neolte":
        return "Galaxy S5 Neo";
      case "zerofltechn":
      case "zeroflteaio":
      case "zeroflte":
      case "zerofltektt":
      case "zerofltemtr":
      case "zeroflteatt":
      case "zeroflteusc":
      case "zeroflteacg":
      case "zerofltelgt":
      case "zeroflteskt":
      case "zerofltebmc":
      case "zerofltelra":
      case "zerofltectc":
      case "zerofltevzw":
      case "zerofltespr":
      case "zerofltetmo":
      case "zerofltetfnvzw":
        return "Galaxy S6";
      case "zeroltektt":
      case "zeroltechn":
      case "zerolteacg":
      case "zerolteusc":
      case "zeroltevzw":
      case "zeroltelra":
      case "zerolteskt":
      case "zeroltelgt":
      case "SCV31":
      case "zeroltebmc":
      case "zeroltetmo":
      case "zeroltespr":
      case "zerolte":
      case "zerolteatt":
      case "404SC":
        return "Galaxy S6 Edge";
      case "zenltebmc":
      case "zenltechn":
      case "zenltelgt":
      case "zenltetmo":
      case "zenltekx":
      case "zenltespr":
      case "zenltevzw":
      case "zenlte":
      case "zenlteatt":
      case "zenlteskt":
      case "zenltektt":
        return "Galaxy S6 Edge+";
      case "SC-01D":
      case "SHW-M380K":
      case "GT-P7500":
      case "SCH-I905":
      case "GT-P7510":
      case "SHW-M300W":
      case "GT-P7503":
      case "SGH-T859":
      case "SHW-M380S":
      case "GT-P7500D":
      case "SHW-M380W":
        return "Galaxy Tab 10.1";
      case "GT-P6210":
      case "SC-02D":
      case "GT-P6201":
      case "GT-P6200":
      case "GT-P6211":
      case "GT-P6200L":
      case "SGH-T869":
      case "SHW-M430W":
        return "Galaxy Tab 7.0 Plus";
      case "gtesqltespr":
      case "gtesqlteusc":
        return "Galaxy Tab E 8.0";
      case "gtelwifi":
      case "gtelwifiue":
      case "gtelwifichn":
      case "gtel3g":
      case "gtelltevzw":
        return "Galaxy Tab E 9.6";
      case "degaswifiue":
      case "403SC":
      case "degasltespr":
      case "degasveltechn":
      case "degaswifiopenbnn":
      case "degasvelte":
      case "degaswifibmwzc":
      case "degaswifidtv":
      case "degasltevzw":
      case "degaswifi":
      case "degas3g":
      case "degaslte":
        return "Galaxy Tab4 7.0";
      case "gvltexsp":
      case "gvwifiue":
      case "gvlteatt":
      case "gvlte":
        return "Galaxy View";
      case "GT-S5368":
      case "SCH-i509":
      case "GT-S5360T":
      case "SCH-I509":
      case "GT-S5369":
      case "GT-S5360":
      case "GT-S5360L":
      case "GT-S5360B":
      case "GT-S5363":
        return "Galaxy Y";
      case "m7cdug":
      case "m7cdtu":
      case "m7":
      case "m7wlv":
      case "m7cdwg":
      case "m7wls":
        return "HTC One";
      case "htc_mecul":
      case "htc_mectl":
      case "htc_mecwhl":
      case "htc_mecdwg":
        return "HTC One (E8)";
      case "htc_m8wl":
      case "htc_m8dwg":
      case "htc_m8dug":
      case "htc_m8":
      case "htc_m8whl":
        return "HTC One (M8)";
      case "htc_hiaeuhl":
      case "htc_hiaeul":
      case "htc_hiaetuhl":
      case "htc_hiaewhl":
        return "HTC One A9";
      case "htc_himauhl":
      case "htc_himawhl":
      case "htc_himaulatt":
      case "htc_himaul":
      case "htc_himawl":
        return "HTC One M9";
      case "villec2":
      case "ville":
        return "HTC One S";
      case "hwH30-T10":
      case "hwH30-U10":
      case "hwhn3-u00":
      case "hwhn3-u01":
        return "Honor3";
      case "acer_harleyfhd":
      case "acer_harley":
        return "Iconia Tab 10";
      case "acer_apriliahd":
      case "acer_aprilia":
        return "Iconia Tab 7";
      case "ducati2fhd":
      case "ducati2hd":
      case "ducati2hd3g":
        return "Iconia Tab 8";
      case "zee":
        return "LG G Flex";
      case "z2":
        return "LG G Flex2";
      case "g2":
        return "LG G2";
      case "g3":
        return "LG G3";
      case "p1":
        return "LG G4";
      case "c50ds":
      case "c50n":
      case "c50":
        return "LG Leon 4G LTE";
      case "cosmopolitan":
        return "LG Optimus 3D";
      case "geehdc":
      case "geehrc":
      case "geehrc4g":
      case "geeb":
        return "LG Optimus G";
      case "geefhd4g":
      case "geefhd":
        return "LG Optimus G Pro";
      case "u2":
        return "LG Optimus L9";
      case "A7-30GC":
        return "Lenovo A7-30GC";
      case "a1":
        return "Liquid";
      case "acer_e3n":
      case "acer_e3":
        return "Liquid E3";
      case "acer_S55":
        return "Liquid Jade";
      case "acer_S56":
        return "Liquid Jade S";
      case "s3":
        return "Liquid S3";
      case "acer_ZXL":
        return "Liquid Z5";
      case "surnia_uds":
      case "condor_cdma":
      case "condor_umts":
      case "condor_umtsds":
      case "condor_udstv":
      case "otus":
      case "otus_ds":
      case "surnia_cdma":
      case "surnia_umts":
      case "surnia_udstv":
        return "MOTO E";
      case "osprey_udstv":
      case "osprey_umts":
      case "thea_umtsds":
      case "falcon_umts":
      case "thea":
      case "titan_umtsds":
      case "peregrine":
      case "osprey_cdma":
      case "titan_udstv":
      case "titan_umts":
      case "osprey_uds":
      case "osprey_ud2":
      case "falcon_umtsds":
      case "thea_ds":
      case "falcon_cdma":
      case "osprey_u2":
        return "MOTO G";
      case "ghost":
      case "victara":
        return "MOTO X";
      case "HWCRR":
        return "Mate S";
      case "K013C":
        return "MeMO Pad 7";
      case "clark":
        return "Moto X Style";
      case "manta":
        return "Nexus 10";
      case "mako":
        return "Nexus 4";
      case "hammerhead":
        return "Nexus 5";
      case "bullhead":
        return "Nexus 5X";
      case "shamu":
        return "Nexus 6";
      case "angler":
        return "Nexus 6P";
      case "grouper":
      case "tilapia":
        return "Nexus 7 (2012)";
      case "deb":
      case "flo":
        return "Nexus 7 (2013)";
      case "flounder":
        return "Nexus 9";
      case "OnePlus":
        return "OnePlus";
      case "A0001":
        return "OnePlus One";
      case "OnePlus2":
        return "OnePlus2";
      case "p990":
      case "p990_CIS-xxx":
      case "star":
      case "star_450-05":
      case "su660":
      case "p990_EUR-xx":
      case "p990hN":
      case "p999":
      case "p990_262-xx":
        return "Optimus 2X";
      case "cosmo_450-05":
      case "p920":
      case "cosmo_EUR-XXX":
      case "su760":
      case "cosmo_MEA-XXX":
        return "Optimus 3D";
      case "cx2":
        return "Optimus 3D MAX";
      case "bproj_214-03":
      case "bproj_EUR-XXX":
      case "bproj_ARE-XXX":
      case "black":
      case "LGL85C":
      case "bproj_302-220":
      case "ku5900":
      case "bproj_262-XXX":
      case "blackg":
      case "bproj_sea-xxx":
      case "bproj_724-xxx":
      case "lgp970":
      case "bproj_334-020":
        return "Optimus Black";
      case "m4":
        return "Optimus L5";
      case "i_skt":
      case "iproj":
      case "lgp930":
      case "i_dcm":
      case "lgp935":
      case "i_u":
        return "Optimus LTE";
      case "su370":
      case "ku3700":
      case "thunder_kor-05":
      case "lu3700":
      case "thunderc":
      case "thunder_kor-08":
        return "Optimus One";
      case "l06c":
      case "v901ar":
      case "v905r":
      case "v900":
      case "v900asia":
      case "v901tr":
      case "v909mkt":
      case "v909":
      case "v901kr":
        return "Optimus Pad";
      case "thunderbird":
      case "LW":
      case "Venue7":
        return "Venue 7";
      case "BB":
      case "yellowtail":
      case "Venue8":
        return "Venue 8";
      case "wifi_hubble":
      case "umts_hubble":
      case "umts_everest":
      case "stingray":
      case "wingray":
        return "XOOM";
      case "D2104":
      case "D2105":
        return "Xperia E1 dual";
      case "D2203":
      case "D2202":
      case "D2243":
      case "D2206":
        return "Xperia E3";
      case "E5653":
      case "E5606":
      case "E5603":
        return "Xperia M5";
      case "E5643":
      case "E5633":
      case "E5663":
        return "Xperia M5 Dual";
      case "SO-02D":
      case "LT26i":
        return "Xperia S";
      case "D5316":
      case "D5303":
      case "D5322":
      case "D5316N":
      case "D5306":
        return "Xperia T2 Ultra";
      case "txs03":
        return "Xperia Tablet S";
      case "SO-03E":
      case "SGP312":
      case "SGP311":
      case "SGP321":
      case "SGP341":
      case "SGP351":
        return "Xperia Tablet Z";
      case "D6503":
      case "D6502":
      case "SO-03F":
      case "D6543":
        return "Xperia Z2";
      case "D6603":
      case "D6646":
      case "401SO":
      case "SOL26":
      case "D6643":
      case "D6653":
      case "SO-01G":
      case "leo":
      case "D6616":
        return "Xperia Z3";
      case "402SO":
      case "SO-03G":
      case "SOV31":
        return "Xperia Z4";
      case "SO-02H":
      case "E5823":
      case "E5803":
        return "Xperia Z5 Compact";
      case "ASUS_T00J":
      case "ASUS_T00F":
        return "ZenFone 5";
      default:
        return fallback;
    }
  }

  /**
   * <p>Capitalizes getAllProcesses the whitespace separated words in a String. Only the first
   * letter of each word is changed.</p>
   *
   * Whitespace is defined by {@link Character#isWhitespace(char)}.
   *
   * @param str
   *     the String to capitalize
   * @return capitalized The capitalized String
   */
  private static String capitalize(String str) {
    if (TextUtils.isEmpty(str)) {
      return str;
    }
    char[] arr = str.toCharArray();
    boolean capitalizeNext = true;
    String phrase = "";
    for (char c : arr) {
      if (capitalizeNext && Character.isLetter(c)) {
        phrase += Character.toUpperCase(c);
        capitalizeNext = false;
        continue;
      } else if (Character.isWhitespace(c)) {
        capitalizeNext = true;
      }
      phrase += c;
    }
    return phrase;
  }

  /** Download URL to String */
  private static String downloadJson(String myurl) throws IOException {
    StringBuilder sb = new StringBuilder();
    BufferedReader reader = null;
    try {
      URL url = new URL(myurl);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000);
      conn.setConnectTimeout(15000);
      conn.setRequestMethod("GET");
      conn.setDoInput(true);
      conn.connect();
      if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
          sb.append(line).append('\n');
        }
      }
      return sb.toString();
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  /**
   * Get the {@link DeviceInfo} for the current device. Do not run on the UI thread, as this may
   * download JSON to retrieve the {@link DeviceInfo}. JSON is only downloaded once and then
   * stored to {@link SharedPreferences}.
   *
   * @param context
   *     the application context.
   * @return {@link DeviceInfo} for the current device.
   */
  public static DeviceInfo getDeviceInfo(Context context) {
    return getDeviceInfo(context.getApplicationContext(), Build.DEVICE, Build.MODEL);
  }

  /** Get the device name from the generated JSON files created from Google's device list. */
  private static DeviceInfo getDeviceInfo(Context context, String codename, String model) {
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

    // check if we have an internet connection
    int ret = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);
    boolean isConnectedToNetwork = false;
    if (ret == PackageManager.PERMISSION_GRANTED) {
      ConnectivityManager connMgr = (ConnectivityManager)
          context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
      if (networkInfo != null && networkInfo.isConnected()) {
        isConnectedToNetwork = true;
      }
    } else {
      // assume we are connected.
      isConnectedToNetwork = true;
    }

    if (isConnectedToNetwork) {
      try {
        String url = String.format(DEVICE_JSON_URL, codename);
        String jsonString = downloadJson(url);
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0, len = jsonArray.length(); i < len; i++) {
          JSONObject json = jsonArray.getJSONObject(i);
          DeviceInfo info = new DeviceInfo(json);
          if (codename.equals(info.codename) && model.equals(info.model)) {
            // Save to SharedPreferences so we don't need to make another request.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, json.toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
              editor.apply();
            } else {
              editor.commit();
            }
            return info;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (codename.equals(Build.DEVICE) && model.equals(Build.MODEL)) {
      // current device
      return new DeviceInfo(
          Build.MANUFACTURER,
          getDeviceName(),
          codename,
          model);
    }

    // unknown device
    return new DeviceInfo(
        null,
        null,
        codename,
        model);
  }

  public static final class Request {

    private final Context context;

    private final Handler handler;

    private String codename;

    private String model;

    private Request(Context ctx) {
      context = ctx;
      handler = new Handler(ctx.getMainLooper());
      codename = Build.DEVICE;
      model = Build.MODEL;
    }

    /**
     * Set the device codename to query. You should also set the model.
     *
     * @param codename
     *     the value of the system property "ro.product.device"
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
     * @param model
     *     the value of the system property "ro.product.model"
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
     * @param callback
     *     the callback to retrieve the {@link DeviceName.DeviceInfo}
     */
    public void request(Callback callback) {
      if (Looper.myLooper() == Looper.getMainLooper()) {
        new Thread(runnable(callback)).start();
      } else {
        runnable(callback).run(); // already running in background thread.
      }
    }

    private Runnable runnable(final Callback callback) {
      return new Runnable() {

        DeviceInfo deviceInfo;

        Exception error;

        @Override
        public void run() {
          try {
            deviceInfo = getDeviceInfo(context, codename, model);
          } catch (Exception e) {
            error = e;
          }
          handler.post(new Runnable() {

            @Override public void run() {
              callback.onFinished(deviceInfo, error);
            }
          });
        }
      };
    }

  }

  /**
   * Callback which is invoked when the {@link DeviceName.DeviceInfo} is finished loading.
   */
  public interface Callback {

    /**
     * Callback to get the device info. This is run on the UI thread.
     *
     * @param info
     *     the requested {@link DeviceName.DeviceInfo}
     * @param error
     *     {@code null} if nothing went wrong.
     */
    void onFinished(DeviceInfo info, Exception error);
  }

  /**
   * Device information based on
   * <a href="https://support.google.com/googleplay/answer/1727131">Google's maintained list</a>.
   */
  public static final class DeviceInfo {

    /** Retail branding */
    public final String manufacturer;

    /** Marketing name */
    public final String marketName;

    /** the value of the system property "ro.product.device" */
    public final String codename;

    /** the value of the system property "ro.product.model" */
    public final String model;

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

}
