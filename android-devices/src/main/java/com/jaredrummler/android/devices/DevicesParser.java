/*
 * Copyright (C) 2015. JRummy Apps, Inc. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 * File created on 10/15/15 11:30 PM by Jared Rummler.
 */

package com.jaredrummler.android.devices;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Google releases a PDF that contains all the devices which have Google Play. The PDF is
 * converted to an excel document and can be found in the root directory of this project. This
 * class parses the excel document to retrieve information about all known Android devices.
 *
 * <p>Excel document last updated: 9/23/2015</p>
 *
 * See: https://support.google.com/googleplay/answer/1727131?hl=en
 */
public class DevicesParser {

  private static final String HEADER_RETAIL_BRANDING = "Retail Branding";

  public List<Device> getDevices(InputStream inputStream) throws IOException {
    List<Device> devices = new ArrayList<>();
    HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
    HSSFSheet sheet = workbook.getSheetAt(0);
    int numRows = sheet.getLastRowNum();
    for (int i = 1; i < numRows; i++) {
      Row row = sheet.getRow(i);
      String manufacturer = cellToString(row.getCell(0));
      String marketName = cellToString(row.getCell(1));
      String codename = cellToString(row.getCell(2));
      String model = cellToString(row.getCell(3));
      if (manufacturer.equals(HEADER_RETAIL_BRANDING)) {
        // skip headers
        continue;
      }
      devices.add(new Device(manufacturer, marketName, codename, model));
    }
    inputStream.close();
    return devices;
  }

  private String cellToString(Cell cell) {
    switch (cell.getCellType()) {
      case Cell.CELL_TYPE_BOOLEAN:
        return cell.getBooleanCellValue() ? "true" : "false";
      case Cell.CELL_TYPE_NUMERIC:
        return Double.toString(cell.getNumericCellValue());
      case Cell.CELL_TYPE_STRING:
      default:
        return cell.getStringCellValue();
    }
  }
}
