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

package com.jaredrummler.android.devicenames;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jaredrummler.android.device.DeviceName;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private EditText editTextCodename;
  private EditText editTextModel;
  private TextView result;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    setDeviceNameText();

    editTextCodename = (EditText) findViewById(R.id.input_codename);
    editTextModel = (EditText) findViewById(R.id.input_model);
    result = (TextView) findViewById(R.id.result);

    editTextCodename.setText(Build.DEVICE);
    editTextModel.setText(Build.MODEL);

    findViewById(R.id.btn).setOnClickListener(this);
  }

  @Override public void onClick(final View v) {
    String codename = editTextCodename.getText().toString();
    String model = editTextModel.getText().toString();

    if (TextUtils.isEmpty(codename)) {
      Snackbar.make(findViewById(R.id.main), "Please enter a codename", Snackbar.LENGTH_LONG)
          .show();
      return;
    }

    DeviceName.Request request = DeviceName.with(this)
        .setCodename(codename);
    if (!TextUtils.isEmpty(model)) {
      request.setModel(model);
    }

    request.request(new DeviceName.Callback() {

      @Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
        if (error != null) {
          result.setText(error.getLocalizedMessage());
          return;
        }

        result.setText(Html.fromHtml("<b>Codename</b>: " + info.codename + "<br>"
            + "<b>Model</b>: " + info.model + "<br>"
            + "<b>Manufacturer</b>: " + info.manufacturer + "<br>"
            + "<b>Name</b>: " + info.getName()));
      }
    });
  }

  private void setDeviceNameText() {
    final TextView textView = (TextView) findViewById(R.id.my_device);

    String deviceName = DeviceName.getDeviceName();
    if (deviceName != null) {
      textView.setText(Html.fromHtml("<b>THIS DEVICE</b>: " + deviceName));
      return;
    }

    // This device is not in the popular device list. Request the device info:
    DeviceName.with(this).request(new DeviceName.Callback() {

      @Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
        textView.setText(Html.fromHtml("<b>THIS DEVICE</b>: " + info.getName()));
      }
    });
  }

}
