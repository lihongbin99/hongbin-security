package io.lihongbin.core;

import io.lihongbin.model.SocketModel;
import io.lihongbin.model.ThreadType;

import java.io.IOException;
import java.net.InetAddress;

public class ReadTarget {

    public static void initTarget(SocketModel socketModel) throws IOException {
        socketModel.setThreadType(ThreadType.TARGET_IP_AND_PORT);
        // 读取数据
        byte[] ipAndPort = new byte[6];
        int totalLen = 0;
        do {
            byte[] bytes = new byte[ipAndPort.length - totalLen];
            int len = socketModel.fromInputStream.read(bytes);
            for (int i = 0; i < len; i++) {
                ipAndPort[totalLen++] = bytes[i];
            }
        } while (totalLen < ipAndPort.length);

        // 解析数据
        InetAddress targetIp = InetAddress.getByAddress(new byte[]{ipAndPort[0], ipAndPort[1], ipAndPort[2], ipAndPort[3]});
        int targetPort = (ipAndPort[4] & 0xff) << 8 | (ipAndPort[5] & 0xff);
        socketModel.setThreadType(ThreadType.TARGET_IP_AND_PORT_SUCCESS);
        // 连接代理服务器
        socketModel.init(targetIp, targetPort);
    }

}
