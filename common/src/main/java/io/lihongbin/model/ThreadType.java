package io.lihongbin.model;

public enum ThreadType {
    // 线程创建成功
    CREATE_SUCCESS,
    // 客户端连接服务器成功
    LINK_SUCCESS,
    // 开始处理目标地址和目标端口
    TARGET_IP_AND_PORT,
    // 处理目标地址和目标端口成功
    TARGET_IP_AND_PORT_SUCCESS,
    // 开始传输数据
    PROXY,
    // 开始关流
    CLOSE,
    // 线程完成
    SUCCESS,
    ;
}
