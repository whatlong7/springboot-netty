package com.picooc.wifi.server.socket;

import com.picooc.wifi.server.backend.Message;
import com.picooc.wifi.server.backend.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created by Shawn Tien on 3/15/16.
 */
public class Decoder extends ByteToMessageDecoder {
    protected final Log logger = LogFactory.getLog(this.getClass());

    Decoder() {
        setSingleDecode(true);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() < 3) {
            logger.warn("Invalid ByteBuf length " + in.readableBytes());
            return;
        }

        try {
            String header = readFixedLengthByte(in, 1);
            String length = readFixedLengthByte(in, 1);
            String content = readFixedLengthByte(in, Integer.parseInt(length, 16) - 3);
            String checksum = readFixedLengthByte(in, 1);

            Message message = new Message(header, length, content, checksum);
            out.add(message);
        } catch (Exception e) {
            logger.error("Decode message error", e);
        }

    }

    private String readFixedLengthByte(ByteBuf in, int length) {
        Byte[] byteArray = new Byte[length];
        int c;
        int i = 0;

        while (in.readableBytes() != 0 && i < length) {
            c = in.readUnsignedByte();
            byteArray[i] = (byte) c;
            i++;
        }

        return Utils.byteArrayToHexString(byteArray);
    }
}
