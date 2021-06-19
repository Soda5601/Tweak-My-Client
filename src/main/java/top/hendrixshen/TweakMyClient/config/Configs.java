package top.hendrixshen.TweakMyClient.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import top.hendrixshen.TweakMyClient.Reference;
import top.hendrixshen.TweakMyClient.TweakMyClient;
import top.hendrixshen.TweakMyClient.gui.GuiConfigs;
import top.hendrixshen.TweakMyClient.util.AntiGhostItemsUtils;
import top.hendrixshen.TweakMyClient.util.AutoDropUtils;
import top.hendrixshen.TweakMyClient.util.RayTraceUtils;

import java.io.File;

public class Configs implements IConfigHandler {
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";

    public static void loadFromFile() {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();
                ConfigUtils.readConfigBase(root, "Generic", Generic.OPTIONS);
                ConfigUtils.readConfigBase(root, "List", List.OPTIONS);
                ConfigUtils.readConfigBase(root, "Color", Color.OPTIONS);
                ConfigUtils.readHotkeyToggleOptions(root, "DisableHotkey", "Disable", Disable.OPTIONS);
                ConfigUtils.readHotkeyToggleOptions(root, "FeatureHotkey", "Feature", Feature.OPTIONS);
            }
        }

        AutoDropUtils.itemStacksBlackList = top.hendrixshen.TweakMyClient.util.StringUtils.getItemStackSets(List.LIST_AUTO_DROP_BLACK_LIST.getStrings());
        AutoDropUtils.itemStacksWhitelist = top.hendrixshen.TweakMyClient.util.StringUtils.getItemStackSets(List.LIST_AUTO_DROP_WHITE_LIST.getStrings());
    }

    public static void saveToFile() {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();
            ConfigUtils.writeConfigBase(root, "Generic", Generic.OPTIONS);
            ConfigUtils.writeConfigBase(root, "List", List.OPTIONS);
            ConfigUtils.writeConfigBase(root, "Color", Color.OPTIONS);
            ConfigUtils.writeHotkeyToggleOptions(root, "DisableHotkey", "Disable", Disable.OPTIONS);
            ConfigUtils.writeHotkeyToggleOptions(root, "FeatureHotkey", "Feature", Feature.OPTIONS);
            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveToFile();
    }

    private enum TargetBlockPositionPrintMode implements IConfigOptionListEntry {
        PUBLIC("public"),
        PRIVATE("private");

        private final String name;

        TargetBlockPositionPrintMode(String name) {
            this.name = name;
        }

        @Override
        public String getStringValue() {
            return this.name;
        }

        @Override
        public String getDisplayName() {
            return StringUtils.translate(String.format("%s.label.targetBlockPositionPrintMode.%s", Reference.MOD_ID, this.name));
        }

        @Override
        public IConfigOptionListEntry cycle(boolean forward) {
            int id = this.ordinal();

            if (forward) {
                if (++id >= values().length) {
                    id = 0;
                }
            } else {
                if (--id < 0) {
                    id = values().length - 1;
                }
            }
            return values()[id % values().length];
        }

        @Override
        public IConfigOptionListEntry fromString(String value) {
            for (TargetBlockPositionPrintMode mode : TargetBlockPositionPrintMode.values()) {
                if (mode.name.equalsIgnoreCase(name)) {
                    return mode;
                }
            }
            return TargetBlockPositionPrintMode.PRIVATE;
        }
    }
    public enum AutoDropListType implements IConfigOptionListEntry {
        BLACKLIST("blackList"),
        WHITELIST("whiteList");

        private final String name;

        AutoDropListType(String name) {
            this.name = name;
        }

        @Override
        public String getStringValue() {
            return this.name;
        }

        @Override
        public String getDisplayName() {
            return StringUtils.translate(String.format("%s.label.autoDropListType.%s", Reference.MOD_ID, this.name));
        }

        @Override
        public IConfigOptionListEntry cycle(boolean forward) {
            int id = this.ordinal();

            if (forward) {
                if (++id >= values().length) {
                    id = 0;
                }
            } else {
                if (--id < 0) {
                    id = values().length - 1;
                }
            }
            return values()[id % values().length];
        }

        @Override
        public IConfigOptionListEntry fromString(String value) {
            for (AutoDropListType mode : AutoDropListType.values()) {
                if (mode.name.equalsIgnoreCase(name)) {
                    return mode;
                }
            }
            return AutoDropListType.WHITELIST;
        }
    }

    public static class Generic {
        private static final String PREFIX = String.format("%s.config.generic", Reference.MOD_ID);
        public static final ConfigHotkey ANTI_GHOST_ITEMS_MANUAL_TRIGGER = new TranslatableConfigHotkey(PREFIX, "antiGhostItemsManualTrigger", "");
        public static final ConfigInteger AUTO_RECONNECT_TIMER = new TranslatableConfigInteger(PREFIX, "autoReconnectTimer", 5, 0, 60);
        public static final ConfigInteger DAYLIGHT_OVERRIDE_TIME = new TranslatableConfigInteger(PREFIX, "daylightOverrideTime", 6000, 0, 24000);
        public static final ConfigHotkey GET_TARGET_BLOCK_POSITION = new TranslatableConfigHotkey(PREFIX, "getTargetBlockPosition", "");
        public static final ConfigHotkey OPEN_CONFIG_GUI = new TranslatableConfigHotkey(PREFIX, "openConfigGui", "T,C");
        public static final ConfigDouble TARGET_BLOCK_MAX_TRACE_DISTANCE = new TranslatableConfigDouble(PREFIX, "targetBlockMaxTraceDistance", 100, 0, 200);
        public static final ConfigString TARGET_BLOCK_POSITION_FORMAT = new TranslatableConfigString(PREFIX, "targetBlockPositionFormat", "I'm tracing this position [x: {X},y: {Y}, z: {Z}]");
        public static final ConfigOptionList TARGET_BLOCK_POSITION_PRINT_MODE = new TranslatableConfigOptionList(PREFIX, "targetBlockPositionPrintMode", TargetBlockPositionPrintMode.PRIVATE);

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                ANTI_GHOST_ITEMS_MANUAL_TRIGGER,
                AUTO_RECONNECT_TIMER,
                DAYLIGHT_OVERRIDE_TIME,
                GET_TARGET_BLOCK_POSITION,
                OPEN_CONFIG_GUI,
                TARGET_BLOCK_MAX_TRACE_DISTANCE,
                TARGET_BLOCK_POSITION_FORMAT,
                TARGET_BLOCK_POSITION_PRINT_MODE
        );
        public static final ImmutableList<ConfigHotkey> HOTKEYS = ImmutableList.of(
                ANTI_GHOST_ITEMS_MANUAL_TRIGGER,
                GET_TARGET_BLOCK_POSITION,
                OPEN_CONFIG_GUI
        );

        static {
            ANTI_GHOST_ITEMS_MANUAL_TRIGGER.getKeybind().setCallback((action, key) -> {
                MinecraftClient mc = TweakMyClient.minecraftClient;
                if (!Feature.FEATURE_ANTI_GHOST_ITEMS.getBooleanValue() || mc.player == null) {
                    return true;
                }
                if (AntiGhostItemsUtils.manualRefreshTimer > 0) {
                    mc.player.sendMessage(new LiteralText(StringUtils.translate("tweakmyclient.message.antiGhostItemsManualTrigger.mustWait", AntiGhostItemsUtils.manualRefreshTimer / 20)), true);
                    return true;
                }
                AntiGhostItemsUtils.refreshInventory();
                AntiGhostItemsUtils.manualRefreshTimer = 200;
                return true;
            });
            GET_TARGET_BLOCK_POSITION.getKeybind().setCallback((action, key) -> {
                if (!Feature.FEATURE_GET_TARGET_BLOCK_POSITION.getBooleanValue()) {
                    return true;
                }
                MinecraftClient mc = TweakMyClient.minecraftClient;
                BlockPos blockPos = RayTraceUtils.getTargetedPosition(mc.world, mc.player, TARGET_BLOCK_MAX_TRACE_DISTANCE.getDoubleValue(), false);
                if (blockPos == null || mc.player == null) {
                    return false;
                }
                String str = Generic.TARGET_BLOCK_POSITION_FORMAT.getStringValue();
                str = str.replace("{X}", String.format("%d", blockPos.getX()));
                str = str.replace("{Y}", String.format("%d", blockPos.getY()));
                str = str.replace("{Z}", String.format("%d", blockPos.getZ()));
                TargetBlockPositionPrintMode mode = (TargetBlockPositionPrintMode) Generic.TARGET_BLOCK_POSITION_PRINT_MODE.getOptionListValue();
                switch (mode) {
                    case PUBLIC:
                        mc.player.sendChatMessage(str);
                        break;
                    case PRIVATE:
                        mc.player.networkHandler.onGameMessage(new GameMessageS2CPacket(new LiteralText(str), MessageType.CHAT, mc.player.getUuid()));
                        break;
                }
                return true;
            });
            OPEN_CONFIG_GUI.getKeybind().setCallback((keyAction, iKeybind) -> {
                GuiConfigs.openGui(new GuiConfigs());
                return true;
            });
        }
    }

    public static class List {
        private static final String PREFIX = String.format("%s.config.list", Reference.MOD_ID);
        public static final ConfigStringList LIST_AUTO_DROP_BLACK_LIST = new TranslatableConfigStringList(PREFIX, "listAutoDropBlackList", ImmutableList.of("minecraft:bow", "minecraft:crossbow", "minecraft:diamond_axe", "minecraft:diamond_boots", "minecraft:diamond_chestplate", "minecraft:diamond_helmet", "minecraft:diamond_hoe", "minecraft:diamond_leggings", "minecraft:diamond_pickaxe", "minecraft:diamond_shovel", "minecraft:diamond_sword", "minecraft:elytra", "minecraft:enchanted_golden_apple", "minecraft:flint_and_steel", "minecraft:fishing_rod", "minecraft:golden_apple", "minecraft:golden_axe", "minecraft:golden_boots", "minecraft:golden_chestplate", "minecraft:golden_helmet", "minecraft:golden_hoe", "minecraft:golden_leggings", "minecraft:golden_pickaxe", "minecraft:golden_shovel", "minecraft:golden_sword", "minecraft:iron_axe", "minecraft:iron_boots", "minecraft:iron_chestplate", "minecraft:iron_helmet", "minecraft:iron_hoe", "minecraft:iron_leggings", "minecraft:iron_pickaxe", "minecraft:iron_shovel", "minecraft:iron_sword", "minecraft:netherite_axe", "minecraft:netherite_boots", "minecraft:netherite_chestplate", "minecraft:netherite_helmet", "minecraft:netherite_hoe", "minecraft:netherite_leggings", "minecraft:netherite_pickaxe", "minecraft:netherite_shovel", "minecraft:netherite_sword", "minecraft:shears", "minecraft:shield", "minecraft:totem_of_undying", "minecraft:trident", "minecraft:turtle_helmet"));
        public static final ConfigOptionList LIST_AUTO_DROP_TYPE = new TranslatableConfigOptionList(PREFIX, "listAutoDropType", AutoDropListType.WHITELIST);
        public static final ConfigStringList LIST_AUTO_DROP_WHITE_LIST = new TranslatableConfigStringList(PREFIX, "listAutoDropWhiteList", ImmutableList.of("minecraft:stone", "minecraft:dirt", "minecraft:cobblestone", "minecraft:gravel", "minecraft:rotten_flesh"));
        public static final ImmutableList<ConfigBase> OPTIONS = ImmutableList.of(
                LIST_AUTO_DROP_BLACK_LIST,
                LIST_AUTO_DROP_TYPE,
                LIST_AUTO_DROP_WHITE_LIST
        );
    }

    public static class Feature {
        private static final String PREFIX = String.format("%s.config.feature_toggle", Reference.MOD_ID);
        public static final ConfigBooleanHotkeyed FEATURE_ANTI_GHOST_ITEMS = new TranslatableConfigBooleanHotkeyed(PREFIX, "featureAntiGhostItems", false, "");
        public static final ConfigBooleanHotkeyed FEATURE_AUTO_DROP = new TranslatableConfigBooleanHotkeyed(PREFIX, "featureAutoDrop", false, "");
        public static final ConfigBooleanHotkeyed FEATURE_AUTO_RECONNECT = new TranslatableConfigBooleanHotkeyed(PREFIX, "featureAutoReconnect", false, "");
        public static final ConfigBooleanHotkeyed FEATURE_AUTO_RESPAWN = new TranslatableConfigBooleanHotkeyed(PREFIX, "featureAutoRespawn", false, "");
        public static final ConfigBooleanHotkeyed FEATURE_CUSTOM_BLOCK_OUTSIDE_COLOR = new TranslatableConfigBooleanHotkeyed(PREFIX, "featureBlockOutsideColor", false, "");
        public static final ConfigBooleanHotkeyed FEATURE_CUSTOM_GUI_BACKGROUND_COLOR = new TranslatableConfigBooleanHotkeyed(PREFIX, "featureGuiBackgroundColor", false, "");
        public static final ConfigBooleanHotkeyed FEATURE_CUSTOM_SIDEBAR_BACKGROUND_COLOR = new TranslatableConfigBooleanHotkeyed(PREFIX, "featureCustomSidebarBackgroundColor", false, "");
        public static final ConfigBooleanHotkeyed FEATURE_DAYLIGHT_OVERRIDE = new TranslatableConfigBooleanHotkeyed(PREFIX, "featureDaylightOverride", false, "");
        public static final ConfigBooleanHotkeyed FEATURE_GET_TARGET_BLOCK_POSITION = new TranslatableConfigBooleanHotkeyed(PREFIX, "featureGetTargetBlockPosition", false, "");
        public static final ConfigBooleanHotkeyed FEATURE_OPEN_WATER_HELPER = new TranslatableConfigBooleanHotkeyed(PREFIX, "featureOpenWaterHelper", false, "");
        public static final ConfigBooleanHotkeyed FEATURE_UNFOCUSED_CPU = new TranslatableConfigBooleanHotkeyed(PREFIX, "featureUnfocusedCPU", false, "");

        public static final ImmutableList<ConfigBooleanHotkeyed> OPTIONS = ImmutableList.of(
                FEATURE_ANTI_GHOST_ITEMS,
                FEATURE_AUTO_DROP,
                FEATURE_AUTO_RECONNECT,
                FEATURE_AUTO_RESPAWN,
                FEATURE_CUSTOM_BLOCK_OUTSIDE_COLOR,
                FEATURE_CUSTOM_GUI_BACKGROUND_COLOR,
                FEATURE_CUSTOM_SIDEBAR_BACKGROUND_COLOR,
                FEATURE_DAYLIGHT_OVERRIDE,
                FEATURE_GET_TARGET_BLOCK_POSITION,
                FEATURE_OPEN_WATER_HELPER,
                FEATURE_UNFOCUSED_CPU
        );
    }

    public static class Color {
        private static final String PREFIX = String.format("%s.config.color", Reference.MOD_ID);
        public static final ConfigColor COLOR_BLOCK_OUTSIDE = new TranslatableConfigColor(PREFIX, "colorBlockOutside", "#66000000");
        public static final ConfigColor COLOR_GUI_START = new TranslatableConfigColor(PREFIX, "colorGuiStart", "#C00F0F0F");
        public static final ConfigColor COLOR_GUI_STOP = new TranslatableConfigColor(PREFIX, "colorGuiStop", "#D00F0F0F");
        public static final ConfigColor COLOR_SIDEBAR_CONTENT = new TranslatableConfigColor(PREFIX, "colorSidebarContent", "#4C000000");
        public static final ConfigColor COLOR_SIDEBAR_TITLE = new TranslatableConfigColor(PREFIX, "colorSidebarTitle", "#66000000");
        public static final ConfigColor COLOR_WATER_OPEN = new TranslatableConfigColor(PREFIX, "colorWaterOpen", "#7F00FF00");
        public static final ConfigColor COLOR_WATER_SHALLOW = new TranslatableConfigColor(PREFIX, "colorWaterShallow", "#7FFF0000");
        public static final ImmutableList<ConfigColor> OPTIONS = ImmutableList.of(
                COLOR_BLOCK_OUTSIDE,
                COLOR_GUI_START,
                COLOR_GUI_STOP,
                COLOR_SIDEBAR_CONTENT,
                COLOR_SIDEBAR_TITLE,
                COLOR_WATER_OPEN,
                COLOR_WATER_SHALLOW
        );
    }

    public static class Disable {
        private static final String PREFIX = String.format("%s.config.disable_toggle", Reference.MOD_ID);
        public static final ConfigBooleanHotkeyed DISABLE_CLIENT_BLOCK_EVENTS = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableClientBlockEvents", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_CLIENT_ENTITY_TNT_UPDATES = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableClientEntityTNTUpdates", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_CLIENT_ENTITY_WITHER_UPDATES = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableClientEntityWitherUpdates", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_CLIENT_ENTITY_ZOMBIE_VILLAGER_UPDATES = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableClientEntityZombieVillagerUpdates", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_ENTITY_TNT_RENDERING = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableEntityTNTRendering", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_ENTITY_WITHER_RENDERING = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableEntityWitherRendering", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_ENTITY_ZOMBIE_VILLAGER_RENDERING = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableEntityZombieVillagerRendering", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_GUI_SHADOW_LAYER = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableGuiShadowLayer", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_RENDER_BOSS_BAR = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableRenderBossBar", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_RENDER_OVERLAY_FIRE = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableRenderOverlayFire", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_RENDER_OVERLAY_PUMPKIN = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableRenderOverlayPumpkin", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_RENDER_SCOREBOARD = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableRenderScoreboard", false, "");
        public static final ConfigBooleanHotkeyed DISABLE_SLOWDOWN = new TranslatableConfigBooleanHotkeyed(PREFIX, "disableSlowdown", false, "");

        public static final ImmutableList<ConfigBooleanHotkeyed> OPTIONS = ImmutableList.of(
                DISABLE_CLIENT_BLOCK_EVENTS,
                DISABLE_CLIENT_ENTITY_TNT_UPDATES,
                DISABLE_CLIENT_ENTITY_WITHER_UPDATES,
                DISABLE_CLIENT_ENTITY_ZOMBIE_VILLAGER_UPDATES,
                DISABLE_ENTITY_TNT_RENDERING,
                DISABLE_ENTITY_ZOMBIE_VILLAGER_RENDERING,
                DISABLE_ENTITY_WITHER_RENDERING,
                DISABLE_GUI_SHADOW_LAYER,
                DISABLE_RENDER_BOSS_BAR,
                DISABLE_RENDER_OVERLAY_FIRE,
                DISABLE_RENDER_OVERLAY_PUMPKIN,
                DISABLE_RENDER_SCOREBOARD,
                DISABLE_SLOWDOWN
        );
    }
}
