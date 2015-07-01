package com.acme.handlers;

import com.google.common.io.ByteStreams;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class Base64BytesSerializer {

    public static void compress(InputStream content, OutputStream os) throws IOException {
        try (GZIPOutputStream zos = new GZIPOutputStream(os)) {
            ByteStreams.copy(content, zos);
        }
    }

    public static void decompress(InputStream content, OutputStream os) throws IOException {
        try (InputStream zis = new GZIPInputStream(content)) {
            ByteStreams.copy(zis, os);
        }
    }

    public static String serialize(InputStream content) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            compress(content, os);
            return DatatypeConverter.printBase64Binary(os.toByteArray());
        }
    }

    public static byte[] deserialize(String object) throws IOException {
        if (object == null) {
            return null;
        }
        try (InputStream is = new ByteArrayInputStream(
                DatatypeConverter.parseBase64Binary(object));
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            decompress(is, bos);
            return bos.toByteArray();
        }
    }

    public static String serialize(byte[] content) throws IOException {
        if (content == null) {
            return null;
        }
        try (InputStream is = new ByteArrayInputStream(content)) {
            return serialize(is);
        }
    }

    public static void deserialize(String object, OutputStream os)
            throws IOException {
        os.write(deserialize(object));
    }

}
