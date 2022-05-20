package com.hd123.baas.sop.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author W.J.H.7
 */
public final class CsvWriter implements AutoCloseable {

  private String dateFormat;

  private CSVPrinter csvPrinter;

  public static CsvWriter newWriter(File csvFile) throws IOException {
    return newWriter(null, csvFile, null);
  }

  public static CsvWriter newWriter(File csvFile, List<String> header) throws IOException {
    return newWriter(null, csvFile, header);
  }

  public static CsvWriter newWriter(String dateFormat, File csvFile) throws IOException {
    return newWriter(dateFormat, csvFile, null);
  }

  public static CsvWriter newWriter(String dateFormat, File csvFile, List<String> header) throws IOException {
    CsvWriter csvWriter = new CsvWriter();
    csvWriter.dateFormat = dateFormat == null ? "yyyy-MM-dd HH:mm:ss" : dateFormat;
    if (!csvFile.exists()) {
      csvFile.getParentFile().mkdirs();
      csvFile.createNewFile();
    }
    PrintWriter printWriter = new PrintWriter(csvFile, StandardCharsets.UTF_8.name());
    if (CollectionUtils.isEmpty(header)) {
      csvWriter.csvPrinter = new CSVPrinter(printWriter, CSVFormat.DEFAULT.withRecordSeparator("\n"));
    } else {
      csvWriter.csvPrinter = new CSVPrinter(printWriter,
          CSVFormat.DEFAULT.withRecordSeparator("\n").withHeader(header.toArray(new String[0])));
    }
    return csvWriter;
  }

  private CsvWriter() {

  }

  public final void writeNext(Object[] nextLine) throws Exception {
    if (nextLine != null) {
      List<String> values = new ArrayList<>();
      for (int i = 0; i < nextLine.length; ++i) {
        values.add(StringUtils.trimToNull(toString(nextLine[i])));
      }
      csvPrinter.printRecord(values);
    }
  }

  private String toString(Object value) {
    if (value == null) {
      return null;
    } else if (value instanceof String) {
      return (String) value;
    } else if (value instanceof Date) {
      return new SimpleDateFormat(dateFormat).format(value);
    } else {
      return value.toString();
    }
  }

  public void flush() throws IOException {
    csvPrinter.flush();
  }

  @Override
  public void close() {
    IOUtils.closeQuietly(csvPrinter);
  }


  public static void main(String[] args) {
    System.out.println(1);
  }
}
