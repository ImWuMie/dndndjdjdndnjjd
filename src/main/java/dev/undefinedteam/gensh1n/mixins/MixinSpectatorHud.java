package dev.undefinedteam.gensh1n.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.render.Render2DAfterHotbar;
import dev.undefinedteam.gensh1n.events.render.Render2DBeforeHotbar;
import dev.undefinedteam.gensh1n.render.GL;
import dev.undefinedteam.gensh1n.render.Renderer;
import dev.undefinedteam.gensh1n.render._new.NText;
import dev.undefinedteam.gensh1n.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.SpectatorHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpectatorHud.class)
public class MixinSpectatorHud {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "renderSpectatorMenu(Lnet/minecraft/client/gui/DrawContext;)V", at = @At("HEAD"))
    private void onRenderHotbar(DrawContext context, CallbackInfo ci) {
        client.getProfiler().push(Client.LC_NAME + "_r2d_before_hotbar2");
        //MSAA.use(() -> {
        Utils.unscaledProjection();
        GL.saveRootState();
        NText.begin(context);
        Renderer.MAIN.begin();
        var window = client.getWindow();
        Render2DBeforeHotbar event = Client.EVENT_BUS.post(Render2DBeforeHotbar.get(context, window.getFramebufferWidth(), window.getFramebufferHeight(), Utils.getTickDelta()));
        Renderer.MAIN.render();
        NText.draw();
        event.postTasks.forEach(Runnable::run);
        GL.restoreRootState();
        Utils.scaledProjection();
        RenderSystem.applyModelViewMatrix();
        //});
        client.getProfiler().pop();
    }

    @Inject(method = "renderSpectatorMenu(Lnet/minecraft/client/gui/DrawContext;)V", at = @At("RETURN"))
    private void onRenderHotbarA(DrawContext context, CallbackInfo ci) {
        client.getProfiler().push(Client.LC_NAME + "_r2d_after_hotbar2");
        //MSAA.use(() -> {
        Utils.unscaledProjection();
        GL.saveRootState();
        NText.begin(context);
        Renderer.MAIN.begin();
        var window = client.getWindow();
        Render2DAfterHotbar event = Client.EVENT_BUS.post(Render2DAfterHotbar.get(context, window.getFramebufferWidth(), window.getFramebufferHeight(), Utils.getTickDelta()));
        Renderer.MAIN.render();
        NText.draw();
        event.postTasks.forEach(Runnable::run);
        GL.restoreRootState();
        Utils.scaledProjection();
        RenderSystem.applyModelViewMatrix();
        //});
        client.getProfiler().pop();
    }
}
