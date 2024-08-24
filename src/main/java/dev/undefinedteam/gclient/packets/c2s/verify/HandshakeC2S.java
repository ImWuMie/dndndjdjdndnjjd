package dev.undefinedteam.gclient.packets.c2s.verify;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class HandshakeC2S extends Packet {
    public int mVersion;
    public String mHwid;

    @Override
    public void read() throws IOException {
    }

    @Override
    public void write() throws IOException {
        buf.writeVarInt(this.mVersion);
        buf.writeString(this.mHwid);
    }
}
