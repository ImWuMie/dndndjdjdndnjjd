package dev.undefinedteam.gclient;

import dev.undefinedteam.gclient.assets.AssetsManager;
import dev.undefinedteam.gclient.codec.CompressionDecoder;
import dev.undefinedteam.gclient.packets.c2s.play.PongC2S;
import dev.undefinedteam.gclient.data.AssetData;
import dev.undefinedteam.gclient.data.UserData;
import dev.undefinedteam.gclient.packets.Packet;
import dev.undefinedteam.gclient.packets.c2s.login.ReqChatC2S;
import dev.undefinedteam.gclient.packets.c2s.play.DisconnectC2S;
import dev.undefinedteam.gclient.packets.c2s.resource.ReqResourceC2S;
import dev.undefinedteam.gclient.packets.c2s.verify.HandshakeC2S;
import dev.undefinedteam.gclient.packets.c2s.verify.ReqClientC2S;
import dev.undefinedteam.gclient.packets.s2c.login.UserInfoS2C;
import dev.undefinedteam.gclient.packets.s2c.play.ChatMessageS2C;
import dev.undefinedteam.gclient.packets.s2c.play.DisconnectS2C;
import dev.undefinedteam.gclient.packets.s2c.play.MessageS2C;
import dev.undefinedteam.gclient.packets.s2c.resource.ResourceDataS2C;
import dev.undefinedteam.gclient.packets.s2c.resource.ResourceListS2C;
import dev.undefinedteam.gclient.packets.s2c.play.PingS2C;
import dev.undefinedteam.gclient.text.Style;
import dev.undefinedteam.gclient.text.Text;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Logger;

import static dev.undefinedteam.gclient.GCClient.setupCompression;
import static dev.undefinedteam.gclient.GChat.LOG_PREFIX;

public class GCUser extends SimpleChannelInboundHandler<Packet> {
    private final Logger LOG = GCClient.INSTANCE.LOGGER;

    public Channel channel;

    private long ping;

    private UserData mUserInfo;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channel = ctx.channel();

        channel.config().setAutoRead(true);

        GCClient.INSTANCE.session = this;
        setupCompression(channel, CompressionDecoder.MAXIMUM_COMPRESSED_LENGTH, true);

        LOG.info("Your HWID: {}", hwid());
        send(new HandshakeC2S(GChat.VERSION_ID, hwid()));
        //clientRequest();
        // Test token
        //51fd5a9bad06908bf3df04bb5fdf4f75

        String name = GChat.INSTANCE.username;
        String token = GChat.INSTANCE.token;
        if (!GChat.get().isQuit && name != null && token != null && !name.isEmpty() && !token.isEmpty()) {
            loginInChat(name, token);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        GCClient.INSTANCE.session = null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        var mc = MinecraftClient.getInstance();

        if (packet instanceof UserInfoS2C userInfoS2C) {
            this.mUserInfo = userInfoS2C.data;

            var missingAssets = AssetsManager.INSTANCE.missingData;
            String[] missings = new String[missingAssets.size()];
            for (int i = 0; i < missings.length; i++) {
                missings[i] = missingAssets.get(i).location;
            }

            send(new ReqResourceC2S(missings));
        } else if (packet instanceof PingS2C s2c) {
            long time = System.currentTimeMillis();
            this.ping = time - s2c.ping;
            send(new PongC2S(time));
        } else if (packet instanceof ResourceListS2C res) {
            for (AssetData assetDatum : res.assets.assetData) {
                LOG.info("Receive Res: {}", assetDatum.location);
                if (AssetsManager.INSTANCE.find(assetDatum.location) == null) {
                    send(new ReqResourceC2S(assetDatum.location));
                }
            }
        } else if (packet instanceof ResourceDataS2C dataS2C) {
            LOG.info("Download Res: {} md5: {},{} bytes", dataS2C.location, dataS2C.md5, dataS2C.data.length);
            AssetsManager.INSTANCE.add(dataS2C.location, dataS2C.md5, dataS2C.data);
        } else if (packet instanceof MessageS2C p) {
            var text = LOG_PREFIX.copy();
            text.append(Text.of(" [").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)))
                .append(p.title)
                .append(Text.of("]: ").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)))
                .append(p.message);
            if (mc != null && mc.player != null && mc.world != null) {
                sendMessage(mc, text);
            } else LOG.info(text);
        } else if (packet instanceof DisconnectS2C p) {
            //netHandler.sendMessage("{}[{}Lemon{}Chat{}]{} Disconnect: {}", ChatFormatting.GRAY, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.GRAY, ChatFormatting.RESET, p.reason);
            if (mc != null && mc.player != null && mc.world != null) {
                var text = LOG_PREFIX.copy();
                text.append(Text.of(" Disconnect: ").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)));
                text.append(p.reason).setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                sendMessage(mc, text);
            } else LOG.info("Disconnect: " + p.reason);
        } else if (packet instanceof ChatMessageS2C p) {
            Text msg = LOG_PREFIX.copy().append(" ");
            var sender = p.sender;

            if (!p.isCommand) {
                var l = Text.of("[").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                var r = Text.of("]").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                var name = Text.of(sender.group).setStyle(Style.EMPTY.withFormatting(sender.name_color.formatting));

                msg.append(l).append(name).append(r);

                if (sender.mNameTag != null) {
                    var l1 = Text.of("[").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                    var r1 = Text.of("]").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                    var tag = Text.of(sender.mNameTag).setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                    msg.append(l1).append(tag).append(r1);
                }

                var nick = Text.of(" " + sender.mNickName + ": ").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                msg.append(nick);
            }

            msg.append(p.message);

            if (mc != null && mc.player != null && mc.world != null) {
                sendMessage(mc, msg);
            } else LOG.info(msg);
        }
    }

    public void sendMessage(MinecraftClient mc, Text text, Object... args) {
        mc.inGameHud.getChatHud().addMessage(text.vanilla());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }

    public void loginInChat(String name, String token) {
        send(new ReqChatC2S(name, token));
        GChat.get().isQuit = false;
    }

    public void clientRequest() {
        send(new ReqClientC2S("yUAN神"));
    }

    public UserData user_info() {
        return this.mUserInfo;
    }

    public boolean logged() {
        return user_info() != null;
    }

    public long getPing() {
        return this.ping;
    }

    public void tick() {
        if (this.channel != null) {
            this.channel.flush();
        }
    }

    public void disconnect(String reason, Object... args) {
        reason = getReplaced(reason, args);
        if (this.channel.isOpen()) {
            this.send(new DisconnectC2S(reason));
            LOG.info("Disconnect: {}", reason);
            this.channel.close().awaitUninterruptibly();
        }
    }

    public void send(Packet packet) {
        if (packet != null) this.channel.writeAndFlush(packet);
    }

    public boolean isConnected() {
        return this.channel != null && this.channel.isOpen() && this.channel.isActive();
    }

    private String hwid() {
        return DigestUtils.sha256Hex(
            System.getenv("os")
                + System.getProperty("os.name")
                + System.getProperty("os.arch")
                + System.getProperty("user.name")
                + System.getenv("PROCESSOR_LEVEL")
                + System.getenv("PROCESSOR_REVISION")
                + System.getenv("PROCESSOR_IDENTIFIER")
                + System.getenv("PROCESSOR_ARCHITEW6432")
        );
    }

    private String getReplaced(String str, Object... args) {
        String s = str;
        for (Object a : args) {
            s = s.replaceFirst("\\{}", a == null ? "null" : a.toString());
        }
        return s;
    }
}
