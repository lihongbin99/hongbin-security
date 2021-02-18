package io.lihongbin.core;

import io.lihongbin.config.SecurityConfig;
import io.lihongbin.config.ServerConfig;
import io.lihongbin.model.SocketModel;
import io.lihongbin.utils.IOUtil;
import io.lihongbin.utils.RSAUtil;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSecurity {

    // 存储验证线程
    private final static Map<String, Boolean> NEW_CONNECT_MAP = new ConcurrentHashMap<>();

    public static boolean start(final SocketModel socketModel) {
        final String threadName = socketModel.threadName;
        final Thread thread = Thread.currentThread();

        // 存储到检测新连接, 用于后面取出来停止
        ServerSecurity.NEW_CONNECT_MAP.put(threadName, true);

        // 定时取消连接
        Thread detectionNewConnectThread = new Thread(() -> {
            try {
                // 睡眠
                Thread.sleep(ServerConfig.NEW_CONNECT_TIME);
                Boolean flag = NEW_CONNECT_MAP.get(threadName);
                if (null != flag) {
                    ServerSecurity.NEW_CONNECT_MAP.put(threadName, false);
                    // 关流 (停止线程的读取状态)
                    socketModel.close();
                    // 停止线程的睡眠状态
                    thread.interrupt();
                }
            } catch (InterruptedException ignored) {
                // 如果线程被唤醒则代表连接正常, 不用做任何处理, 直接退出线程
            }
        });
        detectionNewConnectThread.start();

        try {
            // 校验第一次密码
            security1(socketModel);
            // 校验第二次密码
            security2(socketModel);
            detectionNewConnectThread.interrupt();
        } catch (InterruptedException e) {
            // 检验失败
        } catch (IOException e) {
            // 检验失败
            if (!e.getMessage().contains("Socket closed")) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ServerSecurity.NEW_CONNECT_MAP.remove(threadName);
    }

    private static void security1(SocketModel socketModel) throws IOException, InterruptedException {
        boolean flag = true;
        InputStream inputStream = socketModel.fromInputStream;
        byte[] bytes = new byte[1];
        int i = -1;
        // 读取数据
        while (i < SecurityConfig.ONE_THE_PASSWORD.length - 1) {
            if (inputStream.read(bytes) == -1 || bytes[0] != SecurityConfig.ONE_THE_PASSWORD[++i]) {
                flag = false;
                break;
            }
        }
        error(flag);
    }

    private static void security2(SocketModel socketModel) throws IOException, InterruptedException, NoSuchAlgorithmException {
        boolean flag = false;
        // 生成公私钥
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        // 传输公钥
        byte[] publicKeyEncoded = publicKey.getEncoded();
        IOUtil.write(socketModel.fromOutputStream, publicKeyEncoded);
        // 获取密文
        byte[] context = IOUtil.read(socketModel.fromInputStream);
        // 解密
        byte[] password = RSAUtil.decrypt(context, privateKey.getEncoded());
        // 校验
        if (null != password && password.length == SecurityConfig.TWO_THE_PASSWORD.length) {
            flag = true;
            for (int i = 0; i < password.length; i++) {
                if (password[i] != SecurityConfig.TWO_THE_PASSWORD[i]) {
                    flag = false;
                    break;
                }
            }
        }
        error(flag);
    }

    private static void error(boolean flag) throws InterruptedException {
        if (!flag) {
            // 等待强制断开连接
            Thread.sleep(ServerConfig.NEW_CONNECT_TIME * 2);
        }
    }

}
