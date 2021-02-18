package io.lihongbin.utils;

import io.lihongbin.config.SecurityConfig;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class IOUtil {

    public static void close(Closeable... closeables) {
        if (null != closeables && closeables.length > 0) {
            for (Closeable closeable : closeables) {
                if (null != closeable) {
                    try {
                        closeable.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static byte[] read(InputStream inputStream) throws IOException {
        byte[] lengthByte = getByteArray(inputStream, 4);
        int length = ByteBuffer.wrap(lengthByte).getInt();
        if (length > 0 && length <= SecurityConfig.MAX_LENGTH) {
            return getByteArray(inputStream, length);
        } else {
            return new byte[]{};
        }
    }

    public static byte[] getByteArray(InputStream inputStream, int length) throws IOException {
        byte[] result = new byte[length];
        int totalLen = 0;
        do {
            byte[] bytes = new byte[result.length - totalLen];
            int len = inputStream.read(bytes);
            for (int i = 0; i < len; i++) {
                result[totalLen++] = bytes[i];
            }
        } while (totalLen < result.length);
        return result;
    }

    public static void write(OutputStream outputStream, byte[] context) throws IOException {
        outputStream.write(ByteBuffer.allocate(4).putInt(context.length).array());
        outputStream.flush();
        outputStream.write(context);
        outputStream.flush();
    }

}
