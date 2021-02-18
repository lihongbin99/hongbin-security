package io.lihongbin.core;

import io.lihongbin.config.SecurityConfig;
import io.lihongbin.model.SocketModel;
import io.lihongbin.utils.IOUtil;
import io.lihongbin.utils.RSAUtil;

import java.io.IOException;

public class ClientSecurity {

    public static void start(SocketModel socketModel) throws IOException {
        // 校验第一次密码
        security1(socketModel);
        // 校验第二次密码
        security2(socketModel);
    }

    private static void security1(SocketModel socketModel) throws IOException {
        socketModel.toOutputStream.write(SecurityConfig.ONE_THE_PASSWORD);
        socketModel.toOutputStream.flush();
    }

    private static void security2(SocketModel socketModel) throws IOException {
        // 读取公钥
        byte[] publicKey = IOUtil.read(socketModel.toInputStream);
        // 加密
        byte[] password = RSAUtil.encrypt(SecurityConfig.TWO_THE_PASSWORD, publicKey);
        // 发送密文
        IOUtil.write(socketModel.toOutputStream, password);
    }

}
