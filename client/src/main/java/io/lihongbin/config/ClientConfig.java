package io.lihongbin.config;

import java.net.InetAddress;

public class ClientConfig {

    // 服务器地址
    public InetAddress serverIp;

    // 服务器端口
    public int serverPort;

    // 本地监听端口
    public int localPort;

    // 目标地址
    public String targetIp;

    // 目标端口
    public int targetPort;

    public ClientConfig(InetAddress serverIp, int serverPort, int localPort, String targetIp, int targetPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.localPort = localPort;
        this.targetIp = targetIp;
        this.targetPort = targetPort;
    }

}
