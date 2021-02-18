package io.lihongbin.core;

import io.lihongbin.config.ClientConfig;
import io.lihongbin.model.SocketModel;
import io.lihongbin.model.ThreadType;

import java.io.IOException;

public class WriteTarget {

    public static void initTarget(SocketModel socketModel, ClientConfig clientConfig) throws IOException {
        socketModel.setThreadType(ThreadType.TARGET_IP_AND_PORT);
        // 获取目标 Ip 和 Port
        String[] split = clientConfig.targetIp.split("\\.");
        byte[] ipByte = new byte[4];
        for(int i = 0; i < 4; i++){
            ipByte[i] = (byte) Integer.parseInt(split[i]);
        }
        byte[] portByte = new byte[2];
        portByte[0] = (byte)((clientConfig.targetPort >> 8) & 0x00ff);
        portByte[1] = (byte)(clientConfig.targetPort & 0x00ff);
        byte[] ipAndPort = new byte[6];
        System.arraycopy(ipByte, 0, ipAndPort, 0, ipByte.length);
        System.arraycopy(portByte, 0, ipAndPort, ipByte.length, portByte.length);
        // 传输目标 Ip 和 Port
        socketModel.toOutputStream.write(ipAndPort);
        socketModel.toOutputStream.flush();
        socketModel.setThreadType(ThreadType.TARGET_IP_AND_PORT_SUCCESS);
    }

}
