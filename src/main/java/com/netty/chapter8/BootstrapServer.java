package com.netty.chapter8;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author Lance Sun
 * @date Created in 19:06 2018/10/30
 * @description 引导服务器
 */
public class BootstrapServer {
    /**
     * 正常引导服务器
     */
    public void server1() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        //设置EventLoopGroup，其提供了用于处理Channel时间的EventLoop
        bootstrap.group(group)
                //指定要使用的Channel实现
                .channel(NioServerSocketChannel.class)
                //设置用于处理已被接受的子Channel的I/O及数据的ChannelInboundHandler
                .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                        System.out.println("received data");
                    }
                });
        //通过配置好的ServerBootstrap绑定该Channel
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("Server bound");
                } else {
                    System.out.println("Bound attempt failed");
                    future.cause().printStackTrace();
                }
            }
        });
    }

    /***
     * 服务端在子Channel中引导一个客户端
     * EventLoopGroup用服务端的同一个
     * 编写netty程序的时候要尽可能重用EventLoopGroup，减少线程创建的开销
     */
    public void server2() {
        //创建ServerBootstrap以创建ServerSocketChannel，并绑定它
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                //指定要使用的Channel实现
                .channel(NioServerSocketChannel.class)
                .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                    ChannelFuture connectFuture;

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        //创建一个Bootstrap类的实例以连接到远程主机
                        Bootstrap bootstrap = new Bootstrap();
                        //指定Channel的实现
                        bootstrap.channel(NioSocketChannel.class)
                                //为入站I/O设置ChannelInboundHandler
                                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                        System.out.println("received data");
                                    }
                                });
                        //使用与分配给已被接受的子Channel相同的EventLoop
                        bootstrap.group(ctx.channel().eventLoop());
                        //连接到远程节点
                        connectFuture = bootstrap.connect(new InetSocketAddress("www.manning.com", 80));
                    }

                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                        if (connectFuture.isDone()) {
                            //do something with the data
                        }
                    }
                });
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("server bound");
                } else {
                    System.out.println("bind attempt failed");
                    future.cause().printStackTrace();
                }
            }
        });
    }
}
