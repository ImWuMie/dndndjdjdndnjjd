package dev.undefinedteam.gclient.codec;

import dev.undefinedteam.gclient.GEncrypt;
import dev.undefinedteam.gclient.packets.NetworkPacketsManager;
import dev.undefinedteam.gclient.packets.Packet;
import dev.undefinedteam.gclient.packets.PacketBuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LogManager.getLogger("PacketDecoder");

    public boolean isClient;

    public PacketDecoder(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int len = in.readableBytes();
        if (len == 0) return;

        PacketBuf packetByteBuf = new PacketBuf(in);
        int pid = packetByteBuf.readVarInt();
        Class<? extends Packet> pKlass = isClient ? NetworkPacketsManager.INSTANCE.getS2C(pid) :  NetworkPacketsManager.INSTANCE.getC2S(pid);
        if (pKlass == null) {
            LOGGER.error("{} Bad packet id: " + pid,isClient ? "[Client]" : "[Server]");
            return;
        }
        Packet packet = pKlass.getConstructor().newInstance();
        packet.buf = packetByteBuf;
        packet.read();
        //packet.buf.clear();
        packet.buf = null;
        out.add(packet);
    }
}
