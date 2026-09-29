package net.xuwu.hookbellport.content;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class HookBellConfig {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Entity types the Hook Bell must not teleport (namespaced IDs).").push("hook_bell");
        BLACKLIST = builder.defineList("blacklist", List.of(), value -> value instanceof String);
        builder.pop();
        SPEC = builder.build();
    }

    public static boolean isBlacklisted(EntityType<?> entityType) {
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString();
        return BLACKLIST.get().contains(id);
    }

    private HookBellConfig() {
    }
}
