package top.hendrixshen.tweakmyclient.util;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
//#if MC >= 11903
import net.minecraft.server.packs.resources.IoSupplier;
//#endif
import org.lwjgl.glfw.GLFW;
import top.hendrixshen.tweakmyclient.TweakMyClient;
import top.hendrixshen.tweakmyclient.TweakMyClientReference;
import top.hendrixshen.tweakmyclient.config.Configs;

//#if MC < 11903
//$$ import java.io.IOException;
//#endif
import java.io.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomWindowUtil {
    public static final HashMap<String, String> PLACEHOLDER_MAP = Maps.newHashMap();
    public static final HashMap<String, String> PLACEHOLDER_STATIC_MAP = Maps.newHashMap();
    public static final Pattern MOD_PATTERN = Pattern.compile("(?<=(\\{fabric_mod_ver:)).*?(?=(}))");
    private static String TITLE_CACHE;
    //#if MC >= 11500
    private static String TITLE_CACHE_WITH_ACTIVITY;
    //#endif
    private static final Random RANDOM = new Random();
    public static final Minecraft mc = TweakMyClient.getMinecraftClient();
    private static final ResourceLocation icon16 = new ResourceLocation(TweakMyClientReference.getModIdentifier(), "texture/icon/icon_16x16.png");
    private static final ResourceLocation icon32 = new ResourceLocation(TweakMyClientReference.getModIdentifier(), "texture/icon/icon_32x32.png");

    // These data should not be changed
    static {
        CustomWindowUtil.PLACEHOLDER_STATIC_MAP.put("{fabric_loader_version}", FabricLoaderImpl.VERSION);
        CustomWindowUtil.PLACEHOLDER_STATIC_MAP.put("{fabric_loader_asm_version}", String.valueOf(FabricLoaderImpl.ASM_VERSION));
        //#if MC >= 11600
        CustomWindowUtil.PLACEHOLDER_STATIC_MAP.put("{mc_protocol_version}", Integer.toString(SharedConstants.getProtocolVersion()));
        //#else
        //$$ CustomWindowUtil.PLACEHOLDER_STATIC_MAP.put("{mc_protocol_version}", Integer.toString(SharedConstants.getCurrentVersion().getProtocolVersion()));
        //#endif
        CustomWindowUtil.PLACEHOLDER_STATIC_MAP.put("{mc_version}", SharedConstants.getCurrentVersion().getName());
        CustomWindowUtil.PLACEHOLDER_STATIC_MAP.put("{tmc_version}", TweakMyClientReference.getModVersion());
        CustomWindowUtil.PLACEHOLDER_STATIC_MAP.put("{tmc_version_type}", TweakMyClientReference.getModVersionType());
        if (Configs.featureCustomWindowTitle) {
            //#if MC >= 11500
            CustomWindowUtil.rebuildCache(TitleType.TITLE);
            CustomWindowUtil.rebuildCache(TitleType.TITLE_WITH_ACTIVITY);
            //#else
            //$$ CustomWindowUtil.rebuildCache();
            //#endif
        }
    }

    public static String replacePlaceholders(HashMap<String, String> map, String str) {
        for (String key : map.keySet()) {
            str = str.replace(key, map.getOrDefault(key, "Null"));
        }
        return str;
    }

    public static String getWindowTitle() {
        //#if MC >= 11500
        return CustomWindowUtil.replacePlaceholders(PLACEHOLDER_MAP, (hasActivity() ? TITLE_CACHE_WITH_ACTIVITY : TITLE_CACHE));
        //#else
        //$$ return CustomWindowUtil.replacePlaceholders(PLACEHOLDER_MAP,  TITLE_CACHE);
        //#endif
    }

    //#if MC >= 11500
    public static String getActivity() {
        if (mc.getSingleplayerServer() != null && !mc.getSingleplayerServer().isPublished()) {
            return I18n.get("title.singleplayer");
        } else if (mc.isConnectedToRealms()) {
            return I18n.get("title.multiplayer.realms");
        } else if (mc.getSingleplayerServer() == null && (mc.getCurrentServer() == null || !mc.getCurrentServer().isLan())) {
            return I18n.get("title.multiplayer.other");
        } else {
            return I18n.get("title.multiplayer.lan");
        }
    }

    public static boolean hasActivity() {
        ClientPacketListener clientPacketListener = mc.getConnection();
        return Configs.customWindowTitleEnableActivity && clientPacketListener != null && clientPacketListener.getConnection().isConnected();
    }

    //#endif
    public static void updatePlaceholders() {
        // Maybe changed by other mods.
        CustomWindowUtil.PLACEHOLDER_MAP.put("{mc_username}", TweakMyClient.getMinecraftClient().getUser().getName());
        //#if MC >= 11500
        // Activity data.
        CustomWindowUtil.PLACEHOLDER_MAP.put("{mc_activity}", hasActivity() ? getActivity() : "Null");
        //#endif
    }

    public static void updateFPS(int fps) {
        CustomWindowUtil.PLACEHOLDER_MAP.put("{mc_fps}", String.valueOf(fps));
    }

    //#if MC >= 11500
    public static void rebuildCache(TitleType type) {
        if (type == TitleType.TITLE) {
            int size = Configs.listCustomWindowTitle.size();
            if (size > 0) {
                CustomWindowUtil.TITLE_CACHE = CustomWindowUtil.replacePlaceholders(PLACEHOLDER_STATIC_MAP, Configs.listCustomWindowTitle.get(Configs.customWindowTitleRandomly ? RANDOM.nextInt(size) : 0));
                CustomWindowUtil.TITLE_CACHE = CustomWindowUtil.replaceModVersion(CustomWindowUtil.TITLE_CACHE);
            } else {
                CustomWindowUtil.TITLE_CACHE = "";
            }
        } else if (type == TitleType.TITLE_WITH_ACTIVITY) {
            int size = Configs.listCustomWindowTitleWithActivity.size();
            if (size > 0) {
                CustomWindowUtil.TITLE_CACHE_WITH_ACTIVITY = CustomWindowUtil.replacePlaceholders(PLACEHOLDER_STATIC_MAP,Configs.listCustomWindowTitleWithActivity.get(Configs.customWindowTitleRandomly ? RANDOM.nextInt(size) : 0));
                CustomWindowUtil.TITLE_CACHE_WITH_ACTIVITY = CustomWindowUtil.replaceModVersion(CustomWindowUtil.TITLE_CACHE_WITH_ACTIVITY);
            } else {
                CustomWindowUtil.TITLE_CACHE_WITH_ACTIVITY = "";
            }
        }
    }
    //#else
    //$$ public static void rebuildCache() {
    //$$     int size = Configs.listCustomWindowTitle.size();
    //$$     if (size > 0) {
    //$$         CustomWindowUtil.TITLE_CACHE = CustomWindowUtil.replacePlaceholders(PLACEHOLDER_STATIC_MAP, Configs.listCustomWindowTitle.get(Configs.customWindowTitleRandomly ? RANDOM.nextInt(size) : 0));
    //$$         CustomWindowUtil.TITLE_CACHE = CustomWindowUtil.replaceModVersion(CustomWindowUtil.TITLE_CACHE);
    //$$     } else {
    //$$         CustomWindowUtil.TITLE_CACHE = "";
    //$$     }
    //$$ }
    //#endif

    public static String replaceModVersion(String str) {
        Matcher matcher = MOD_PATTERN.matcher(str);
        while (matcher.find()) {
            String group = matcher.group();
            Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(group);
            str = str.replace(String.format("{fabric_mod_ver:%s}", group), container.isPresent() ? container.get().getMetadata().getVersion().getFriendlyString() : "Null");
        }
        return str;
    }

    //#if MC >= 11500
    public enum TitleType {
        TITLE,
        TITLE_WITH_ACTIVITY
    }
    //#endif

    public static void reSetTitle() {
        //#if MC >= 11500
        GLFW.glfwSetWindowTitle(TweakMyClient.getMinecraftClient().getWindow().getWindow(), "Minecraft " + SharedConstants.getCurrentVersion().getName());
        //#else
        //$$ GLFW.glfwSetWindowTitle(TweakMyClient.getMinecraftClient().window.getWindow(), "Minecraft " + SharedConstants.getCurrentVersion().getName());
        //#endif
    }

    public static void updateTitle() {
        updatePlaceholders();
        //#if MC >= 11500
        GLFW.glfwSetWindowTitle(TweakMyClient.getMinecraftClient().getWindow().getWindow(), CustomWindowUtil.getWindowTitle());
        //#else
        //$$ GLFW.glfwSetWindowTitle(TweakMyClient.getMinecraftClient().window.getWindow(), CustomWindowUtil.getWindowTitle());
        //#endif
    }

    public static void updateIcon() {
        Window window = TweakMyClient.getMinecraftClient().getWindowCompat();
        //#if MC >= 11903
        IoSupplier<InputStream> icon16x;
        IoSupplier<InputStream> icon32x;
        if (Configs.featureCustomWindowIcon) {
            File data = new File("./tmc_data");
            if (!data.exists()) {
                data.mkdirs();
            }
            File i16x = new File(data.getPath(), "i16");
            File i32x = new File(data.getPath(), "i32");
            try {
                FileOutputStream i16os = new FileOutputStream(i16x);
                FileOutputStream i32os = new FileOutputStream(i32x);
                i16os.write(mc.getResourceManager().open(CustomWindowUtil.icon16).readAllBytes());
                i32os.write(mc.getResourceManager().open(CustomWindowUtil.icon32).readAllBytes());
                icon16x = IoSupplier.create(i16x.toPath());
                icon32x = IoSupplier.create(i32x.toPath());
            } catch (IOException e) {
                icon16x = null;
                icon32x = null;
            }
        } else {
            icon16x = mc.getVanillaPackResources().getRootResource("icons", "icon_16x16.png");
            icon32x = mc.getVanillaPackResources().getRootResource("icons", "icon_32x32.png");
        }
        if (icon16x != null && icon32x != null) {
            window.setIcon(icon16x, icon32x);
        } else {
            TweakMyClient.getLogger().error("Couldn't set icon");
        }
        //#else
        //$$ try {
        //$$     InputStream icon16x;
        //$$     InputStream icon32x;
        //$$     if (Configs.featureCustomWindowIcon) {
        //$$         icon16x = mc.getResourceManager().getResource(CustomWindowUtil.icon16).getInputStream();
        //$$         icon32x = mc.getResourceManager().getResource(CustomWindowUtil.icon32).getInputStream();
        //$$     } else {
        //$$         icon16x = mc.getClientPackSource().getVanillaPack().getResource(PackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_16x16.png"));
        //$$         icon32x = mc.getClientPackSource().getVanillaPack().getResource(PackType.CLIENT_RESOURCES, new ResourceLocation("icons/icon_32x32.png"));
        //$$     }
        //$$     window.setIcon(icon16x, icon32x);
        //$$ } catch (IOException e) {
        //$$     TweakMyClient.getLogger().error("Couldn't set icon", e);
        //$$ }
        //#endif
    }
}
