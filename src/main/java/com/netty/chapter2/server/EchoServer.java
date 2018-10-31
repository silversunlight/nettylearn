package com.netty.chapter2.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @Author ：Lance Sun
 * @Date ：Created in 20:35 2018/9/25
 * @Description：
 * @ModifiedBy：
 * @Version: 1.0.0
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        //创建eventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建serverBootStrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    //指定所使用的nio传输channel
                    .channel(NioServerSocketChannel.class)
                    //使用指定的端口设置套接字地址
                    .localAddress(new InetSocketAddress(port))
                    //添加一个echoServerHandler到子Channel的ChannelPipeLine
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //EchoServerHanlder被标注为@sharable,所以我们总是可以使用同样的实例
                            socketChannel.pipeline().addLast(serverHandler);
                        }
                    });
            //异步的绑定服务器,调用sync()方法阻塞直到绑定完成
            ChannelFuture f = b.bind().sync();
            //获取channel的closeFuture,并且阻塞当前线程直到它完成
            f.channel().closeFuture().sync();
        } finally {
            //关闭eventGroup,释放所有资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.out.println("wrong port");
            return;
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }
}
