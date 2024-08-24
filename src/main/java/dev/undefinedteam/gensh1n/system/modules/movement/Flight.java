package dev.undefinedteam.gensh1n.system.modules.movement;

import dev.undefinedteam.gensh1n.events.client.TickEvent;
import dev.undefinedteam.gensh1n.settings.Setting;
import dev.undefinedteam.gensh1n.settings.SettingGroup;
import dev.undefinedteam.gensh1n.system.modules.Module;
import dev.undefinedteam.gensh1n.system.modules.Categories;
import meteordevelopment.orbit.EventHandler;

public class Flight extends Module {
    public Flight(){
        super(Categories.Movement, "flight", "Fly in survival mode");
        Setting<Double> speed = doubleN(sgGeneral, "mode", 2, 0.1, 30);
    }
    private final SettingGroup sgAntiKick = settings.createGroup("anti-kick");
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<FlightMode> mode = choice(sgGeneral, "mode", FlightMode.Vanilla);
    private final Setting<AntiKickMode> antiKickMode = choice(sgAntiKick, "mode", AntiKickMode.None);


    @EventHandler
    public void onTickEvent(TickEvent.Pre e){
        switch(mode.get()){
            case Vanilla:
                if (mc.player != null && !mc.player.isSpectator() && !mc.player.getAbilities().flying){
                    mc.player.getAbilities().flying = true;
                    mc.player.getAbilities().allowFlying = true;

                }

        }
    }
    @EventHandler
    public void onTickPostEvent(TickEvent.Post e){
        switch(antiKickMode.get()){
            case None: return;
            case TestOnGround:
                // shit
                mc.player.setOnGround(true);


        }
    }
    @Override
    public void onDeactivate(){
        mc.player.getAbilities().flying = false;
        mc.player.getAbilities().allowFlying = false;
    }
    private enum FlightMode{
        Vanilla,
    }
    private enum AntiKickMode{
        None,
        TestOnGround,
    }
}
