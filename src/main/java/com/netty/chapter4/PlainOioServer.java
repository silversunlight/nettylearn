package com.netty.chapter4;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @author Lance Sun
 * @date Created in 16:38 2018/10/27
 * @description 不使用netty的oio编程 oio：阻塞传输
 */
public class PlainOioServer {
    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    public void serve(int port) throws IOException {
        //将服务器绑定到指定端口
        final ServerSocket socket = new ServerSocket(port);
        try {
            for (; ; ) {
                //接受连接
                final Socket clientSocket = socket.accept();
                System.out.println("Accepted connection from " + clientSocket);
                //创建一个新的线程来处理这个连接
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //将消息写给已连接的客户端
                        OutputStream out;
                        try {
                            out = clientSocket.getOutputStream();
                            out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
                            out.flush();
                            //关闭连接
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException ex) {

                            }
                        }
                    }
                    //启动线程
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
