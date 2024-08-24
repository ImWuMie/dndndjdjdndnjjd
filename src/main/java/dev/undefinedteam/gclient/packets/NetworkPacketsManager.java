package dev.undefinedteam.gclient.packets;

import dev.undefinedteam.gclient.packets.c2s.gui.GuiActionC2S;
import dev.undefinedteam.gclient.packets.c2s.play.PongC2S;
import dev.undefinedteam.gclient.GCClient;
import dev.undefinedteam.gclient.packets.c2s.login.ReqChatC2S;
import dev.undefinedteam.gclient.packets.c2s.resource.ReqResourceC2S;
import dev.undefinedteam.gclient.packets.c2s.play.ChatMessageC2S;
import dev.undefinedteam.gclient.packets.c2s.play.DisconnectC2S;
import dev.undefinedteam.gclient.packets.c2s.verify.HandshakeC2S;
import dev.undefinedteam.gclient.packets.c2s.verify.ReqClientC2S;
import dev.undefinedteam.gclient.packets.s2c.resource.ResourceDataS2C;
import dev.undefinedteam.gclient.packets.s2c.resource.ResourceListS2C;
import dev.undefinedteam.gclient.packets.s2c.gui.GuiDataS2C;
import dev.undefinedteam.gclient.packets.s2c.login.UserInfoS2C;
import dev.undefinedteam.gclient.packets.s2c.play.ChatMessageS2C;
import dev.undefinedteam.gclient.packets.s2c.play.DisconnectS2C;
import dev.undefinedteam.gclient.packets.s2c.play.MessageS2C;
import dev.undefinedteam.gclient.packets.s2c.play.PingS2C;
import dev.undefinedteam.gclient.packets.s2c.verify.ClientDataS2C;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkPacketsManager {
    private final Logger LOG = GCClient.INSTANCE.LOGGER;

    private static final int OFFSET = 0x1145;
    public static NetworkPacketsManager INSTANCE;
    public final HashMap<Integer, Class<? extends Packet>> s2c = new HashMap<>();
    public final HashMap<Integer, Class<? extends Packet>> c2s = new HashMap<>();

    public NetworkPacketsManager() {
        INSTANCE = this;
    }

    public void init() {
        registerC2S(HandshakeC2S.class);
        registerC2S(ChatMessageC2S.class);
        registerC2S(PongC2S.class);
        registerC2S(DisconnectC2S.class);
        registerC2S(ReqChatC2S.class);
        registerC2S(ReqClientC2S.class);
        registerC2S(ReqResourceC2S.class);
        registerC2S(GuiActionC2S.class);

        registerS2C(ResourceDataS2C.class);
        registerS2C(ResourceListS2C.class);
        registerS2C(ClientDataS2C.class);
        registerS2C(ChatMessageS2C.class);
        registerS2C(MessageS2C.class);
        registerS2C(DisconnectS2C.class);
        registerS2C(PingS2C.class);
        registerS2C(UserInfoS2C.class);
        registerS2C(GuiDataS2C.class);

        LOG.info("Registered {} s2c packets.", s2c.size());
        LOG.info("Registered {} c2s packets.", c2s.size());
    }
    public Class<? extends Packet> getS2C(int pid) {
        return s2c.getOrDefault(pid, null);
    }

    public Class<? extends Packet> getC2S(int pid) {
        return c2s.getOrDefault(pid, null);
    }

    private void registerS2C(Class<? extends Packet> packetClass) {
        s2c.put(s2c.size() + OFFSET, packetClass);
    }

    private void registerC2S(Class<? extends Packet> packetClass) {
        c2s.put(c2s.size() + OFFSET, packetClass);
    }

    public int getS2CPid(Class<? extends Packet> packetClass) {
        AtomicInteger pid = new AtomicInteger(-1);
        s2c.forEach((id, p) -> {
            if (p == packetClass) {
                pid.set(id);
            }
        });
        return pid.get();
    }

    public int getC2SPid(Class<? extends Packet> packetClass) {
        AtomicInteger pid = new AtomicInteger(-1);
        c2s.forEach((id, p) -> {
            if (p == packetClass) {
                pid.set(id);
            }
        });
        return pid.get();
    }
}
