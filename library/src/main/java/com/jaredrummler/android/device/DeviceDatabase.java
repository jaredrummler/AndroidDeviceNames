package com.jaredrummler.android.device;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.jaredrummler.android.device.DeviceName.DeviceInfo;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Database helper to access the list of all known Android devices with their market, code, and
 * model name.
 */
public class DeviceDatabase extends SQLiteOpenHelper {

  private static final String TABLE_DEVICES = "devices";

  private static final String COLUMN_NAME = "name";
  private static final String COLUMN_CODENAME = "codename";
  private static final String COLUMN_MODEL = "model";

  private static final String NAME = "android-devices.db";
  private static final int VERSION = 1;

  private final File file;
  private final Context context;

  @SuppressWarnings("WeakerAccess")
  public DeviceDatabase(Context context) {
    super(context, NAME, null, VERSION);
    this.context = context.getApplicationContext();
    this.file = context.getDatabasePath(NAME);
    if (!file.exists()) {
      create();
    }
  }

  /**
   * Query the market name given the codename and/or model of a device.
   *
   * @param codename the value of the system property "ro.product.device" ({@link Build#DEVICE}).
   * @param model the value of the system property "ro.product.model" ({@link Build#MODEL}).
   * @return The market name of the device if it is found in the database, otherwise null.
   */
  public String query(@Nullable String codename, @Nullable String model) {
    SQLiteDatabase database = getReadableDatabase();

    String[] columns = new String[] { COLUMN_NAME };
    String selection;
    String[] selectionArgs;
    if (codename != null && model != null) {
      selection = COLUMN_CODENAME + " LIKE ? OR " + COLUMN_MODEL + " LIKE ?";
      selectionArgs = new String[] { codename, model };
    } else if (codename != null) {
      selection = COLUMN_CODENAME + " LIKE ?";
      selectionArgs = new String[] { codename };
    } else if (model != null) {
      selection = COLUMN_MODEL + " LIKE ?";
      selectionArgs = new String[] { model };
    } else {
      return null;
    }

    Cursor cursor =
        database.query(TABLE_DEVICES, columns, selection, selectionArgs, null, null, null);

    String name = null;
    if (cursor.moveToFirst()) {
      name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
    }

    close(cursor);
    close(database);

    return name;
  }

  /**
   * Query the device info given the codename and/or model of a device.
   *
   * @param codename the value of the system property "ro.product.device" ({@link Build#DEVICE}).
   * @param model the value of the system property "ro.product.model" ({@link Build#MODEL}).
   * @return The {@link DeviceInfo} if it is found in the database, otherwise null.
   */
  @SuppressWarnings("WeakerAccess")
  public DeviceInfo queryToDevice(@Nullable String codename, @Nullable String model) {
    SQLiteDatabase database = getReadableDatabase();

    String[] columns = new String[] { COLUMN_NAME, COLUMN_CODENAME, COLUMN_MODEL };
    String selection;
    String[] selectionArgs;

    if (!TextUtils.isEmpty(codename) && !TextUtils.isEmpty(model)) {
      selection = COLUMN_CODENAME + " LIKE ? OR " + COLUMN_MODEL + " LIKE ?";
      selectionArgs = new String[] { codename, model };
    } else if (!TextUtils.isEmpty(codename)) {
      selection = COLUMN_CODENAME + " LIKE ?";
      selectionArgs = new String[] { codename };
    } else if (TextUtils.isEmpty(model)) {
      selection = COLUMN_MODEL + " LIKE ?";
      selectionArgs = new String[] { model };
    } else {
      return null;
    }

    Cursor cursor =
        database.query(TABLE_DEVICES, columns, selection, selectionArgs, null, null, null);

    DeviceInfo deviceInfo = null;

    if (cursor.moveToFirst()) {
      String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
      codename = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODENAME));
      model = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODEL));
      deviceInfo = new DeviceInfo(name, codename, model);
    }

    close(cursor);
    close(database);

    return deviceInfo;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    // no-op
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (newVersion > oldVersion) {
      if (context.deleteDatabase(NAME) || file.delete() || !file.exists()) {
        create();
      }
    }
  }

  private void create() throws SQLException {
    try {
      getReadableDatabase();    // Create an empty database that we will overwrite.
      close();                  // Close the empty database
      transferDatabaseAsset();  // Copy the database from assets to the application's database dir
    } catch (IOException e) {
      throw new SQLException("Error creating " + NAME + " database", e);
    }
  }

  private void transferDatabaseAsset() throws IOException {
    InputStream input = context.getAssets().open(NAME);
    OutputStream output = new FileOutputStream(file);
    byte[] buffer = new byte[2048];
    int length;
    while ((length = input.read(buffer)) > 0) {
      output.write(buffer, 0, length);
    }
    output.flush();
    close(output);
    close(input);
  }

  private void close(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException ignored) {
      }
    }
  }
}
