package io.lihongbin.utils;

import java.io.InputStream;
import java.io.OutputStream;

public class TransferData {

    // 传输数据, 如果 where 结束则代表结束传输数据, 需要关流
    public static void transferData(InputStream inputSteam, OutputStream outputStream, Thread stopThread) {
        byte[] bytes = new byte[1024 * 64];
        int len;
        try {
            while (!Thread.currentThread().isInterrupted() && (len = inputSteam.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
        } catch (Exception e) {
            stopThread.interrupt();
        }
    }

}
