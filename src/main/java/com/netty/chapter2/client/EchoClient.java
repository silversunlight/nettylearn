package com.netty.chapter2.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @Author ：Lance Sun
 * @Date ：Created in 21:10 2018/9/25
 * @Description：
 * @ModifiedBy：
 * @Version: 1.0.0
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建bootstrap
            Bootstrap b = new Bootstrap();
            //指定eventLoopGroup以处理客户端事件,需要适用于nio实现
            b.group(group)
                    //适用于nio传输的channel类型
                    .channel(NioSocketChannel.class)
                    //设置服务器的address
                    .remoteAddress(new InetSocketAddress(host, port))
                    //在创建channel时,向channelPipeLine中添加一个echoClientHandler实例
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            //连接到远程节点,阻塞等待直到连接完成
            ChannelFuture f = b.connect().sync();
            //阻塞,直到channel关闭
            f.channel().closeFuture().sync();
        } finally {
            //关闭线程池并释放所有资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.out.println("wrong intput");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        new EchoClient(host, port).start();
    }
}
