package top.hendrixshen.TweakMyClient;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.hendrixshen.TweakMyClient.config.Configs;
import top.hendrixshen.TweakMyClient.event.InputHandler;
import top.hendrixshen.TweakMyClient.event.RenderHandler;

public class TweakMyClient implements ModInitializer {
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);
    public static MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Override
    public void onInitialize() {
        ConfigManager.getInstance().registerConfigHandler(Reference.MOD_ID, new Configs());
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());

        IRenderer renderer = new RenderHandler();
        RenderEventHandler.getInstance().registerWorldLastRenderer(renderer);
        logger.info(Reference.MOD_NAME + "-" + Reference.MOD_VERSION + " initialized.");
    }
}
