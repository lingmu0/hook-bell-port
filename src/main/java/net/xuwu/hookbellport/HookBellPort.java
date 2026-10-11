package net.xuwu.hookbellport;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.xuwu.hookbellport.content.HookBellConfig;
import net.xuwu.hookbellport.content.HookBellContent;

public final class HookBellPort implements ModInitializer {
    public static final String MOD_ID = "hookbellport";

    @Override
    public void onInitialize() {
        HookBellConfig.load();
        HookBellContent.init();
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
                .register(entries -> entries.accept(HookBellContent.HOOK_BELL_ITEM));
    }

    public static Identifier location(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
