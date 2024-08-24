
package icyllis.modernui.mc.fabric;

import icyllis.modernui.ModernUI;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.mc.MuiModApi;
import icyllis.modernui.mc.MuiScreen;
import icyllis.modernui.mc.ScreenCallback;
import icyllis.modernui.mc.UIManager;
import icyllis.modernui.mc.mixin.AccessGameRenderer;
import net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

import static dev.undefinedteam.gensh1n.Client.mc;

public final class MuiFabricApi extends MuiModApi {
    public static final MuiFabricApi INSTANCE = new MuiFabricApi();


    public MuiFabricApi() {
        ModernUI.LOGGER.info(ModernUI.MARKER, "Created MuiFabricApi");
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public <T extends Screen & MuiScreen> T createScreen(@Nonnull Fragment fragment,
                                                         @Nullable ScreenCallback callback,
                                                         @Nullable Screen previousScreen,
                                                         @Nullable CharSequence title) {
        return (T) new SimpleScreen(UIManager.getInstance(),
                fragment, callback, previousScreen, title);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T extends ScreenHandler, U extends Screen & ScreenHandlerProvider<T> & MuiScreen> U createMenuScreen(@NotNull Fragment fragment, @org.jetbrains.annotations.Nullable ScreenCallback callback, @NotNull T menu, @NotNull PlayerInventory inventory, @NotNull Text title) {
        return (U) new MenuScreen<>(UIManager.getInstance(),
            fragment, callback, menu, inventory, title);
    }

    @Override
    public boolean isGLVersionPromoted() {
        // we are unknown about this
        return false;
    }

    @Override
    public void loadEffect(GameRenderer gr, Identifier effect) {
        //gr.loadPostProcessor(effect);
    }

    @Override
    public ShaderProgram makeShaderInstance(ResourceFactory resourceProvider, Identifier resourceLocation, VertexFormat vertexFormat) throws IOException {
        return new FabricShaderProgram(resourceProvider, resourceLocation, vertexFormat);
    }

    @Override
    public boolean isKeyBindingMatches(KeyBinding keyMapping, InputUtil.Key key) {
        return key.getCategory() == InputUtil.Type.KEYSYM
            ? keyMapping.matchesKey(key.getCode(), InputUtil.UNKNOWN_KEY.getCode())
            : keyMapping.matchesKey(InputUtil.UNKNOWN_KEY.getCode(), key.getCode());
    }

    @Override
    public Style applyRarityTo(Rarity rarity, Style baseStyle) {
        return baseStyle.withColor(rarity.getFormatting());
    }
}

