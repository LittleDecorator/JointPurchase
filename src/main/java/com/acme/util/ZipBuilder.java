package com.acme.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipBuilder {

  public static final int NO_COMPRESSION_LEVEL = Deflater.NO_COMPRESSION;

  private ByteArrayOutputStream byteArrayOutputStream;
  private BufferedOutputStream bufferedOutputStream;
  private ZipOutputStream zipOutputStream;

  private ZipBuilder() {
    byteArrayOutputStream = new ByteArrayOutputStream();
    bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
    zipOutputStream = new ZipOutputStream(bufferedOutputStream);
  }

  private ZipBuilder(OutputStream stream) {
    zipOutputStream = new ZipOutputStream(stream);
  }

  public static ZipBuilder create() {
    return new ZipBuilder();
  }

  public static ZipBuilder create(OutputStream stream) {
    return new ZipBuilder(stream);
  }

  public ZipBuilder setCompressionLevel(int compressionLevel) {
    zipOutputStream.setLevel(compressionLevel);
    return this;
  }

  public ZipBuilder addEntry(String fileName, byte[] content) throws IOException {
    ZipEntry zipEntry = new ZipEntry(fileName);
    zipOutputStream.putNextEntry(zipEntry);
    write(new ByteArrayInputStream(content), zipOutputStream);
    zipOutputStream.closeEntry();
    return this;
  }

  public ZipBuilder addEntry(String folderName, String fileName, byte[] content) throws IOException {
    return addEntry(buildEntryName(folderName, fileName), content);
  }

  private String buildEntryName(String folderName, String fileName) {
    return escapeSpecialChars(folderName) + "/" + escapeSpecialChars(fileName);
  }

  private String escapeSpecialChars(String name) {
    return name.replaceAll("[/\\\\?%*:|\"<>]", "_");
  }

  private void write(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int len;
    while ((len = in.read(buffer)) >= 0) {
      out.write(buffer, 0, len);
    }
    in.close();
  }

  public ZipBuilder close() throws IOException {
    zipOutputStream.finish();
    zipOutputStream.flush();
    zipOutputStream.close();
    return this;
  }

  public byte[] toByteArray() {
    return byteArrayOutputStream.toByteArray();
  }
}
