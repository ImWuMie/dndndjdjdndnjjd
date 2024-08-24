package dev.undefinedteam.gensh1n.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.render.Render2DBeforeChat;
import dev.undefinedteam.gensh1n.mixin_interface.IChatHud;
import dev.undefinedteam.gensh1n.mixin_interface.IChatHudLine;
import dev.undefinedteam.gensh1n.render.GL;
import dev.undefinedteam.gensh1n.render.MSAA;
import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class MixinChatHud implements IChatHud {
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    @Final
    private List<ChatHudLine.Visible> visibleMessages;
    @Shadow
    @Final
    private List<ChatHudLine> messages;
    @Unique
    private int nextId;
    @Unique
    private boolean skipOnAddMessage;

    @Shadow
    protected abstract void addMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator);

    @Shadow
    public abstract void addMessage(Text message);

    @Override
    public void gensh1n$add(Text message, int id) {
        nextId = id;
        addMessage(message);
        nextId = 0;
    }

    @Inject(method = "addVisibleMessage", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", shift = At.Shift.AFTER))
    private void onAddMessageAfterNewChatHudLineVisible(ChatHudLine message, CallbackInfo ci) {
        ((IChatHudLine) (Object) visibleMessages.get(0)).gensh1n$setId(nextId);
    }

    @Inject(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", shift = At.Shift.AFTER))
    private void onAddMessageAfterNewChatHudLine(ChatHudLine message, CallbackInfo ci) {
        ((IChatHudLine) (Object) messages.get(0)).gensh1n$setId(nextId);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderBefore(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        client.getProfiler().push(Client.LC_NAME + "_r2d_chat_before");
        MSAA.use(() -> {
            Utils.unscaledProjection();
            GL.saveRootState();
            NText.begin(context);
            Renderer.MAIN.begin();
            var window = client.getWindow();
            var event = Client.EVENT_BUS.post(Render2DBeforeChat.get(context, window.getFramebufferWidth(), window.getFramebufferHeight(), Utils.getTickDelta()));
            Renderer.MAIN.render();
            NText.draw();
            event.postTasks.forEach(Runnable::run);
            GL.restoreRootState();
            Utils.scaledProjection();
            RenderSystem.applyModelViewMatrix();
        });
        client.getProfiler().pop();
    }
}
