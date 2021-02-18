package io.lihongbin;

import io.lihongbin.config.SecurityConfig;
import io.lihongbin.config.ServerConfig;
import io.lihongbin.core.ErrorProcessor;
import io.lihongbin.core.Proxy;
import io.lihongbin.core.ReadTarget;
import io.lihongbin.core.ServerSecurity;
import io.lihongbin.model.SocketModel;
import io.lihongbin.model.ThreadType;
import io.lihongbin.pool.SocketModelPool;
import io.lihongbin.utils.IOUtil;
import io.lihongbin.utils.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        Server.run();
    }

    /**
     * 启动服务器
     *  1. 创建服务器
     *  2. 监听连接
     *  3. 获取连接 ID
     *  4. 校验当前连接数量
     *  5. 处理连接
     * @throws IOException 创建服务器异常
     */
    private static void run() throws IOException {
        // 启动服务器
        ServerSocket serverSocket = new ServerSocket(SecurityConfig.LISTENER_PORT);
        // 循环监听连接
        while (!Thread.interrupted()) {
            try {
                // 获取新连接
                Socket socket = serverSocket.accept();
                // 获取连接次数, 用于标识
                long count = SecurityConfig.NEW_CONNECT_COUNT.incrementAndGet();
                // 线程数连接限制
                int threadCount = ThreadPool.count(ThreadPool.SOCKET_GROUP);
                if (threadCount >= ServerConfig.MAX_THREAD_COUNT) {
                    IOUtil.close(socket);
                }
                // 处理连接
                String threadName = String.valueOf(count);
                Thread thread = new Thread(() -> proxy(socket), threadName);
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
     *  3. 校验连接合法性
     *  4. 获取代理地址
     *  5. 代理传输数据
     *  6. 连接的后置处理
     * @param socket 连接
     */
    private static void proxy(Socket socket) {
        // 前置处理
        ThreadPool.put(ThreadPool.SOCKET_GROUP, Thread.currentThread().getName(), Thread.currentThread());

        SocketModel socketModel;
        try {
            // 创建对象
            SocketModelPool.set(socketModel = new SocketModel(socket));
            socketModel.setThreadType(ThreadType.CREATE_SUCCESS);
        } catch (Exception e) {
            IOUtil.close(socket);
            return;
        }

        try {
            // 权限验证
            boolean flag = ServerSecurity.start(socketModel);
            if (flag) {
                // 获取代理地址
                ReadTarget.initTarget(socketModel);
                // 代理传输数据
                Proxy.doProxy(socketModel);
            } else {
                // 校验失败后的处理
                ErrorProcessor.doError(socketModel);
            }
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
