package net.xuwu.hookbellport;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.xuwu.hookbellport.content.HookBellConfig;
import net.xuwu.hookbellport.content.HookBellContent;

public final class HookBellPort implements ModInitializer {
    public static final String MOD_ID = "hookbellport";

    @Override
    public void onInitialize() {
        HookBellConfig.load();
        HookBellContent.init();
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS)
                .register(entries -> entries.accept(HookBellContent.HOOK_BELL_ITEM));
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
