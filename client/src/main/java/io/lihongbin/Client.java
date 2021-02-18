package io.lihongbin;

import io.lihongbin.config.ClientConfig;
import io.lihongbin.config.SecurityConfig;
import io.lihongbin.core.ClientSecurity;
import io.lihongbin.core.Proxy;
import io.lihongbin.core.WriteTarget;
import io.lihongbin.model.SocketModel;
import io.lihongbin.model.ThreadType;
import io.lihongbin.pool.SocketModelPool;
import io.lihongbin.utils.IOUtil;
import io.lihongbin.utils.ThreadPool;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {
        Client.run(new ClientConfig(InetAddress.getByName("127.0.0.1"), SecurityConfig.LISTENER_PORT, 13521, "192.168.23.129", 3306));
    }

    /**
     * 启动服务器
     *  1. 创建服务器
     *  2. 监听连接
     *  3. 获取连接 ID
     *  4. 处理连接
     * @throws IOException 创建服务器异常
     * @param clientConfig 配置
     */
    private static void run(final ClientConfig clientConfig) throws IOException {
        ServerSocket serverSocket = new ServerSocket(clientConfig.localPort);
        // 循环监听连接
        while (!Thread.interrupted()) {
            try {
                // 获取新连接
                Socket socket = serverSocket.accept();
                // 获取连接次数, 用于标识
                long count = SecurityConfig.NEW_CONNECT_COUNT.incrementAndGet();
                // 处理连接
                Thread thread = new Thread(() -> link(socket, clientConfig), String.valueOf(count));
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理连接
     *  1. 处理连接的前置处理
     *  2. 创建全局对象
     *  3. 连接服务器
     *  4. 校验连接
     *  5. 传输代理地址
     *  6. 代理传输数据
     *  7. 连接的后置处理
     * @param socket 连接
     * @param clientConfig 配置
     */
    private static void link(Socket socket, ClientConfig clientConfig) {
        // 前置处理
        ThreadPool.put(ThreadPool.SOCKET_GROUP, Thread.currentThread().getName(), Thread.currentThread());

        SocketModel socketModel;
        try {
            // 创建对象
            SocketModelPool.set(socketModel = new SocketModel(socket));
            socketModel.setThreadType(ThreadType.CREATE_SUCCESS);
            // 连接服务器
            socketModel.init(clientConfig.serverIp, clientConfig.serverPort);
            socketModel.setThreadType(ThreadType.LINK_SUCCESS);
        } catch (Exception e) {
            IOUtil.close(socket);
            return;
        }

        try {
            // 权限验证
            ClientSecurity.start(socketModel);
            // 传输代理地址
            WriteTarget.initTarget(socketModel, clientConfig);
            // 代理传输数据
            Proxy.doProxy(socketModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        socketModel.setThreadType(ThreadType.CLOSE);
        // 关流
        socketModel.close();
        // 后置处理
        ThreadPool.remove(ThreadPool.SOCKET_GROUP, Thread.currentThread().getName());
        socketModel.setThreadType(ThreadType.SUCCESS);
    }

}
