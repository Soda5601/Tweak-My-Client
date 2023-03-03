package top.hendrixshen.tweakmyclient.mixin.feature.featureAutoReconnect;

import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.tweakmyclient.config.Configs;
import top.hendrixshen.tweakmyclient.interfaces.IAutoReconnectScreen;
import top.hendrixshen.tweakmyclient.util.AutoReconnectUtil;

@Mixin(value = DisconnectedScreen.class)
public class MixinDisconnectedScreen extends Screen implements IAutoReconnectScreen {
    @Shadow
    @Final
    private Screen parent;

    @Shadow
    private int textHeight;

    @Shadow
    @Final
    private Component reason;

    protected MixinDisconnectedScreen(Component component) {
        super(component);
    }

    @Inject(
            method = "init",
            at = @At(
                    value = "TAIL"
            ),
            cancellable = true
    )
    private void onInitDisconnectedScreen(CallbackInfo ci) {
        if (Configs.featureReconnectButtons) {
            AutoReconnectUtil.getInstance().initDisconnectedScreen(this, this.parent, this.width, this.height, this.textHeight, this.reason);
            if (Configs.compatReconnectButtons) ci.cancel();
        }
    }

    @Override
    public Screen getParent() {
        return parent;
    }
}
