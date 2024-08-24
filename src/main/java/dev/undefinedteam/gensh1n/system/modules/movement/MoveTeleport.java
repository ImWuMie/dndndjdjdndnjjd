package dev.undefinedteam.gensh1n.system.modules.movement;

import dev.undefinedteam.gensh1n.events.player.PlayerTickEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import dev.undefinedteam.gensh1n.system.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class MoveTeleport extends Module {
    public MoveTeleport() {
        super(Categories.Movement, "move-teleport", "Teleports you to the place you are looking at.");
    }

    private final SettingGroup sgDefault = settings.getDefaultGroup();

    private final Setting<MoveMode> mode = choice(sgDefault, "mode", MoveMode.Both);

    private final Setting<Double> bps = doubleN(sgDefault,"bps",100,3,300);

    @EventHandler
    private void onPlayerTick(PlayerTickEvent e) {
        if (mode.get().equals(MoveMode.OnlyMove) || mode.get().equals(MoveMode.Both)) {

        }
    }

    public enum MoveMode {
        OnlyMove, OnlyBoat, Both
    }
}
