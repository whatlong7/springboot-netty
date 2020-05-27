package com.picooc.wifi.server.socket;

import com.picooc.wifi.server.backend.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Shawn Tien on 3/15/16.
 */
public class ObjectHandler extends ChannelInboundHandlerAdapter {
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("New client connected.");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Echo back the received object to the client.
        logger.debug("Read ONE message.");
        logger.info("Client IP Address: " + ctx.channel().remoteAddress());
        Message message = (Message) msg;
        logger.debug("Message Type" + message.header);
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.info("IP Address: " + ctx.channel().remoteAddress());
        logger.info("message replied.");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
