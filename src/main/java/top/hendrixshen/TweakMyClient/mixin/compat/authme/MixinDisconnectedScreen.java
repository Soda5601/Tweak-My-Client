package top.hendrixshen.TweakMyClient.mixin.compat.authme;

import me.axieum.mcmod.authme.impl.gui.AuthMethodScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.TweakMyClient.TweakMyClientReference;
import top.hendrixshen.TweakMyClient.config.Configs;
import top.hendrixshen.TweakMyClient.util.AutoReconnectUtils;
import top.hendrixshen.magiclib.untils.dependency.Dependencies;
import top.hendrixshen.magiclib.untils.dependency.Dependency;
import top.hendrixshen.magiclib.untils.language.I18n;

@Dependencies(dependencyList = @Dependency(modid = "authme", version = "*"))
@Mixin(value = DisconnectedScreen.class, priority = 897)
public class MixinDisconnectedScreen extends Screen {
    @Shadow
    private int textHeight;
    @Shadow
    @Final
    private Component reason;
    @Shadow
    @Final
    private Screen parent;

    protected MixinDisconnectedScreen(Component component) {
        super(component);
    }

    @Inject(
            method = "init",
            at = @At(
                    value = "TAIL"
            )
    )
    private void onInitDisconnectedScreen(CallbackInfo ci) {
        int backButtonX = width / 2 - 100;
        int backButtonY = Math.min(height / 2 + textHeight / 2 + 9, height - 30);
        if (reason == null || AutoReconnectUtils.getTranslationKey(reason).startsWith("disconnect.loginFailed")) {
            Configs.Feature.FEATURE_AUTO_RECONNECT.setBooleanValue(false);
            addRenderableWidget(new Button(backButtonX, 72 + backButtonY + AutoReconnectUtils.reAuthenticateButtonOffsetY, 200, 20,
                    new TextComponent(I18n.translate(String.format("%s.message.autoReconnect.reAuthenticateWithAuthMe", TweakMyClientReference.getModId()))), button -> {
                assert this.minecraft != null;
                this.minecraft.setScreen(new AuthMethodScreen(parent));
            }));
            AutoReconnectUtils.reAuthenticateButtonOffsetY += 24;
        }
    }
}
