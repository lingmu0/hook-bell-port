package net.xuwu.hookbellport.content;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class HookBellConfig {
    private static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Entity types the Hook Bell must not teleport (namespaced IDs).").push("hook_bell");
        BLACKLIST = builder.defineList("blacklist", List.of(), value -> value instanceof String);
        builder.pop();
        SPEC = builder.build();
    }

    public static void register() {
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, SPEC);
    }

    public static boolean isBlacklisted(EntityType<?> entityType) {
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString();
        return BLACKLIST.get().contains(id);
    }

    private HookBellConfig() {
    }
}
