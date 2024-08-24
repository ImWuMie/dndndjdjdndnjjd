package dev.undefinedteam.gclient.packets.c2s.login;

import dev.undefinedteam.gclient.packets.Packet;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class ReqChatC2S extends Packet {
    public String mName;
    public String mToken;

    @Override
    public void read() throws IOException {
    }

    @Override
    public void write() throws IOException {
        buf.writeString(this.mName);
        buf.writeString(this.mToken);
    }
}
