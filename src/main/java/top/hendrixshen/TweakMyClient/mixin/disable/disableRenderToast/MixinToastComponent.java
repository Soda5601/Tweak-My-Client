package top.hendrixshen.tweakmyclient.mixin.disable.disableRenderToast;

import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.hendrixshen.tweakmyclient.config.Configs;

import java.util.Deque;

@Mixin(ToastComponent.class)
public class MixinToastComponent {
    @Shadow
    @Final
    private Deque<Toast> queued;

    @Inject(
            method = "addToast",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void onAddToToastQueue(Toast toast, CallbackInfo ci) {
        if (Configs.disableRenderToast.getBooleanValue()) {
            ci.cancel();
        }
    }
}
