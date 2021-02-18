package io.lihongbin.pool;

import io.lihongbin.model.SocketModel;

public class SocketModelPool {

    private final static ThreadLocal<SocketModel> SOCKET_MODEL_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(SocketModel socketModel) {
        SOCKET_MODEL_THREAD_LOCAL.set(socketModel);
    }

}
