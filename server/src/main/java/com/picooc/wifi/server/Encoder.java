package com.picooc.wifi.server.socket;

import com.picooc.wifi.server.backend.Message;
import com.picooc.wifi.server.backend.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Shawn Tien on 3/15/16.
 */
public class Encoder extends MessageToByteEncoder {
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Message message = (Message) msg;
        String hexString = Utils.byteArrayToHexString(message.getBytesB());
        logger.info("Reply hex: " + hexString);
        out.writeBytes(message.getBytes());
    }
}
