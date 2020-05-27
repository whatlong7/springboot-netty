package com.picooc.wifi.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author Gjing
 * <p>
 * 服务启动监听器
 **/
@Slf4j
public class NettyServer {
    static final int PORT = 999;//Integer.parseInt(System.getProperty("port", PropUtil.getValue("nettyServer.port")));


    public void start(InetSocketAddress socketAddress) {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
        //new 一个主线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //new 一个工作线程组
//        EventLoopGroup workGroup = new NioEventLoopGroup(200);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("encoder", new Encoder());
                        p.addLast("decoder", new Decoder());
                        p.addLast("logic", new ObjectHandler());
                    }
                });

//        b.localAddress(socketAddress)
//                //设置队列大小
//                .option(ChannelOption.SO_BACKLOG, 1024)
//                // 两小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
//                .childOption(ChannelOption.SO_KEEPALIVE, true);


        try {
            ChannelFuture future = b.bind(PORT).sync();
            log.info("服务器启动开始监听端口: {}", socketAddress.getPort());
            future.channel().closeFuture().sync();

//            ChannelFuture future = bootstrap.bind(socketAddress).sync();
//            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //关闭主线程组
            bossGroup.shutdownGracefully();
            //关闭工作线程组
            workGroup.shutdownGracefully();
        }
    }
}
