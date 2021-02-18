package io.lihongbin.config;

import java.util.concurrent.atomic.AtomicLong;

public class SecurityConfig {

    // 服务器监听端口
    public final static int LISTENER_PORT = 13520;

    // 第一次验证的密码
    public final static byte[] ONE_THE_PASSWORD = "Li Hong Bin".getBytes();
    // 第二次验证的密码
    public final static byte[] TWO_THE_PASSWORD = "Li Hong Bin".getBytes();

    // 最大接受大小
    public final static int MAX_LENGTH = 1/*MB*/ * 1024/*KB*/ * 1024/*B*/;

    // 原子整型, 用于生成线程 ID
    public final static AtomicLong NEW_CONNECT_COUNT = new AtomicLong();

}
