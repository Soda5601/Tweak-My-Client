package top.hendrixshen.tweakmyclient;

import fi.dy.masa.malilib.event.RenderEventHandler;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.dependency.annotation.Dependencies;
import top.hendrixshen.magiclib.dependency.annotation.Dependency;
import top.hendrixshen.tweakmyclient.config.ConfigHandler;
import top.hendrixshen.tweakmyclient.config.Configs;
import top.hendrixshen.tweakmyclient.event.RenderHandler;
import top.hendrixshen.tweakmyclient.util.render.CustomBlockHitBoxRenderer;
//#if MC >= 11600
import top.hendrixshen.tweakmyclient.util.render.OpenWaterHelperRenderer;
//#endif
import top.hendrixshen.tweakmyclient.util.render.RestrictionBoxRenderer;

public class TweakMyClient implements ClientModInitializer {
    @Getter
    private static final Logger logger = LogManager.getLogger(TweakMyClientReference.getModIdentifier());
    @Getter
    @NotNull
    private static final Minecraft minecraftClient = Minecraft.getInstance();

    @Dependencies(
            //#if MC >= 11903
            and = {
                    @Dependency(value = "jade", versionPredicate = ">=9.3.1", optional = true),
                    @Dependency(value = "wthit", versionPredicate = ">=5.10.0", optional = true)
            }
            //#elseif MC >= 11800
            //$$ and = @Dependency(value = "wthit", versionPredicate = ">=4.12.0", optional = true)
            //#elseif MC >= 11700
            //$$ and = @Dependency(value = "wthit", versionPredicate = ">=3.11.3", optional = true)
            //#elseif MC >= 11600
            //$$ and = @Dependency(value = "wthit", versionPredicate = ">=2.10.15", optional = true)
            //#endif
    )
    @Override
    public void onInitializeClient() {
        ConfigHandler configHandler = TweakMyClientReference.getConfigHandler();
        configHandler.configManager.parseConfigClass(Configs.class);
        ConfigHandler.register(configHandler);
        Configs.initCallbacks(configHandler.configManager);
        RenderEventHandler.getInstance().registerWorldLastRenderer(RenderHandler.getInstance());
        RenderHandler.getInstance().registerWorldLastRenderer(CustomBlockHitBoxRenderer.getInstance());
        //#if MC >= 11600
        RenderHandler.getInstance().registerWorldLastRenderer(OpenWaterHelperRenderer.getInstance());
        //#endif
        RenderHandler.getInstance().registerWorldLastRenderer(RestrictionBoxRenderer.getInstance());

        TweakMyClient.getLogger().info("[{}]: Mod initialized - Version: {} ({})", TweakMyClientReference.getModName(), TweakMyClientReference.getModVersion(), TweakMyClientReference.getModVersionType());
    }
}
