package dev.undefinedteam.gensh1n.system.modules;

import com.google.gson.JsonObject;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.Genshin;
import dev.undefinedteam.gensh1n.settings.*;
import dev.undefinedteam.gensh1n.system.ChatAdapter;
import dev.undefinedteam.gensh1n.system.Config;
import dev.undefinedteam.gensh1n.system.SettingAdapter;
import dev.undefinedteam.gensh1n.utils.RandomUtils;
import dev.undefinedteam.gensh1n.utils.Utils;
import dev.undefinedteam.gensh1n.utils.chat.ChatUtils;
import dev.undefinedteam.gensh1n.utils.input.Keybind;
import dev.undefinedteam.gensh1n.utils.misc.ISerializable;
import dev.undefinedteam.gensh1n.utils.render.color.Color;
import meteordevelopment.orbit.IEventBus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Module extends ChatAdapter implements ISerializable<Module>, Comparable<Module>, SettingAdapter {
    protected final MinecraftClient mc;

    public final Category category;
    public final String name;
    public final String description;

    public final Settings settings = new Settings();

    private boolean active;
    private boolean toggleMessage = true;
    private boolean toggleToast = false;
    public boolean serialize = true;

    public final Keybind keybind = Keybind.none();
    public boolean toggleOnBindRelease = false;
    public boolean favorite = false;
    public final String COLOR = "Color is the visual perception of different wavelengths of light as hue, saturation, and brightness.";
    public final Color color;

    protected final IEventBus EVENT_BUS = Client.EVENT_BUS;

    public Module(Category category, String name, String description) {
        super(Utils.nameToTitle(name));
        this.mc = MinecraftClient.getInstance();
        this.category = category;
        this.name = name;
        this.description = description;
        this.color = Color.fromHsv(RandomUtils.nextDouble(0.0, 360.0), 0.35, 1);
    }

    public void onActivate() {

    }

    public void onDeactivate() {

    }

    public void onSubscribe() {
    }

    public void onUnSubscribe() {
    }

    public void sendToggledMsg() {
        if (Config.get().chatFeedback.get()) {
            // ChatUtils.forceNextPrefixClass(getClass());
            // ChatUtils.sendMsg(this.hashCode(), Formatting.GRAY, "Toggled (highlight)%s(default) %s(default).", title, isActive() ? Formatting.GREEN + "on" : Formatting.RED + "off");
        }
    }

    public void toggle() {
        if (!active) {
            active = true;
            Modules.get().addActive(this);

            settings.onActivated();

            if (Utils.canUpdate()) {
                Client.EVENT_BUS.subscribe(this);
                Genshin.LOG.info("sub: " + title);
                onSubscribe();
                onActivate();
                sendToggledMsg();
            }
        } else {
            if (Utils.canUpdate()) {
                Client.EVENT_BUS.unsubscribe(this);
                Genshin.LOG.info("unsub: " + title);
                onUnSubscribe();
                onDeactivate();
                sendToggledMsg();
            }

            active = false;
            Modules.get().removeActive(this);
        }
    }

    public void forceToggle(boolean toggle) {
        active = toggle;

        if (toggle) {
            Modules.get().addActive(this);

            settings.onActivated();

            if (Utils.canUpdate()) {
                Client.EVENT_BUS.subscribe(this);
                onActivate();
            }
        } else {
            if (Utils.canUpdate()) {
                Client.EVENT_BUS.unsubscribe(this);
                onDeactivate();
            }

            Modules.get().removeActive(this);
        }
    }

    //  Packets
    public void sendPacket(Packet<?> packet) {
        if (mc.getNetworkHandler() == null) return;
        mc.getNetworkHandler().sendPacket(packet);
    }

    /*public void sendSequenced(SequencedPacketCreator packetCreator) {
        if (mc.interactionManager == null || mc.world == null || mc.getNetworkHandler() == null) return;

        PendingUpdateManager sequence = mc.world.getPendingUpdateManager().incrementSequence();
        Packet<?> packet = packetCreator.predict(sequence.getSequence());

        mc.getNetworkHandler().sendPacket(packet);

        sequence.close();
    }*/

    public void setToggleMessage(boolean toggleMessage) {
        this.toggleMessage = toggleMessage;
    }

    public boolean isMessageEnabled() {
        return toggleMessage;
    }

    public void setToggleToast(boolean toggleToast) {
        this.toggleToast = toggleToast;
    }

    public boolean isToastEnabled() {
        return toggleToast;
    }

    public boolean isActive() {
        return active;
    }

    public String getInfoString() {
        return null;
    }

    @Override
    public JsonObject toTag() {
        if (!serialize) return null;
        JsonObject tag = new JsonObject();

        tag.addProperty("name", name);
        tag.add("keybind", keybind.toTag());
        tag.addProperty("toggleOnKeyRelease", toggleOnBindRelease);
        tag.add("settings", settings.toTag());

        tag.addProperty("toggleMessage", toggleMessage);
        tag.addProperty("toggleToast", toggleToast);
        tag.addProperty("favorite", favorite);
        tag.addProperty("active", active);

        return tag;
    }

    @Override
    public Module fromTag(JsonObject tag) {
        // General
        if (tag.has("key")) keybind.set(true, tag.get("key").getAsInt());
        else keybind.fromTag(tag.getAsJsonObject("keybind"));

        toggleOnBindRelease = tag.get("toggleOnKeyRelease").getAsBoolean();

        // Settings
        settings.fromTag(tag.getAsJsonObject("settings"));

        toggleMessage = tag.get("toggleMessage").getAsBoolean();
        toggleToast = tag.get("toggleToast").getAsBoolean();
        favorite = tag.get("favorite").getAsBoolean();
        boolean active = tag.get("active").getAsBoolean();
        if (active != isActive()) toggle();

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Module module = (Module) o;
        return Objects.equals(name, module.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(@NotNull Module o) {
        return name.compareTo(o.name);
    }
}
