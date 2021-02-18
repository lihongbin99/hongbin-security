package io.lihongbin.core;

import io.lihongbin.model.SocketModel;
import io.lihongbin.model.ThreadType;
import io.lihongbin.utils.TransferData;

public class Proxy {

    public static void doProxy(SocketModel socketModel) {
        // 开始代理传输数据
        socketModel.initThread(
                Thread.currentThread(),
                new Thread(() ->
                        TransferData.transferData(socketModel.toInputStream, socketModel.fromOutputStream, socketModel.formToTo)
                )
        );
        socketModel.setThreadType(ThreadType.PROXY);
        socketModel.toToFrom.start();
        TransferData.transferData(socketModel.fromInputStream, socketModel.toOutputStream, socketModel.toToFrom);
    }

}
