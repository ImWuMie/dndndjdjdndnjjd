package dev.undefinedteam.gensh1n.events.network;

import dev.undefinedteam.gensh1n.events.Cancellable;
import net.minecraft.network.packet.Packet;

public class PacketEvent extends Cancellable {
    public TransferOrigin origin;
    public Packet<?> packet;
    public boolean original;

    public PacketEvent(TransferOrigin origin, Packet<?> packet) {
        this(origin,packet,true);
    }

    public PacketEvent(TransferOrigin origin, Packet<?> packet, boolean original) {
        this.origin = origin;
        this.packet = packet;
        this.original = original;
    }

    public enum TransferOrigin {
        SEND, RECEIVE
    }
}
