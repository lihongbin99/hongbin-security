package io.lihongbin.model;

import io.lihongbin.utils.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class SocketModel {

    public Socket fromSocket;
    public InputStream fromInputStream;
    public OutputStream fromOutputStream;

    public Socket toSocket;
    public InputStream toInputStream;
    public OutputStream toOutputStream;

    public Thread formToTo;
    public Thread toToFrom;

    public String threadName;
    public ThreadType threadType;

    public SocketModel(Socket socket) throws IOException {
        this.threadName = Thread.currentThread().getName();
        this.fromSocket = socket;
        this.fromInputStream = this.fromSocket.getInputStream();
        this.fromOutputStream = this.fromSocket.getOutputStream();
    }

    public void init(InetAddress ip, int port) throws IOException {
        this.toSocket = new Socket(ip, port);
        this.toInputStream = this.toSocket.getInputStream();
        this.toOutputStream = this.toSocket.getOutputStream();
    }

    public void initThread(Thread formToTo, Thread toToFrom) {
        this.formToTo = formToTo;
        this.toToFrom = toToFrom;
    }

    public void close() {
        IOUtil.close(
                this.toOutputStream,
                this.toInputStream,
                this.toSocket,
                this.fromOutputStream,
                this.fromInputStream,
                this.fromSocket
        );
    }

    public void setThreadType(ThreadType threadType) {
        this.threadType = threadType;
    }

}
