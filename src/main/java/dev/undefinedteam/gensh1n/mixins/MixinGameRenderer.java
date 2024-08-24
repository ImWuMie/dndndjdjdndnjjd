package dev.undefinedteam.gensh1n.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.undefinedteam.gensh1n.Client;
import dev.undefinedteam.gensh1n.events.render.Render3DEvent;
import dev.undefinedteam.gensh1n.render.MSAA;
import dev.undefinedteam.gensh1n.render.world.NR3D;
import dev.undefinedteam.gensh1n.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow
    @Final
    MinecraftClient client;


    @Shadow
    @Final
    private Camera camera;

    @Shadow
    protected abstract void tiltViewWhenHurt(MatrixStack matrices, float tickDelta);

    @Shadow
    protected abstract void bobView(MatrixStack matrices, float tickDelta);

    @Unique
    private final MatrixStack matrices = new MatrixStack();

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=hand"}), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci, @Local(ordinal = 1) Matrix4f matrix4f2, @Local(ordinal = 1) float tickDelta, @Local MatrixStack matrixStack) {
        if (!Utils.canUpdate()) return;
        RenderSystem.getModelViewStack().pushMatrix().mul(matrix4f2);
        matrices.push();
        tiltViewWhenHurt(matrices, camera.getLastTickDelta());
        if (client.options.getBobView().getValue()) bobView(matrices, camera.getLastTickDelta());

        RenderSystem.getModelViewStack().mul(matrices.peek().getPositionMatrix().invert());
        matrices.pop();

        RenderSystem.applyModelViewMatrix();

        client.getProfiler().swap(Client.ASSETS_LOCATION + "_r3d");
        MSAA.use(() -> {
            Render3DEvent event = Render3DEvent.get(matrixStack, tickCounter.getTickDelta(true));
            event.renderer = NR3D.REGULAR;
            NR3D.REGULAR.begin();
            Client.EVENT_BUS.post(event);
            NR3D.REGULAR.render(matrixStack, 1f);
            event.postTasks.forEach(Runnable::run);
        });

        // Update model view matrix
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.applyModelViewMatrix();
    }
}
