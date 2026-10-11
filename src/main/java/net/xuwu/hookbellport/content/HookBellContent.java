package net.xuwu.hookbellport.content;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.xuwu.hookbellport.HookBellPort;
import net.xuwu.hookbellport.block.HookBellBlock;
import net.xuwu.hookbellport.block.entity.HookBellBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class HookBellContent {
    public static final HookBellBlock HOOK_BELL = Registry.register(BuiltInRegistries.BLOCK,
            HookBellPort.location("hook_bell"), new HookBellBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, HookBellPort.location("hook_bell")))));
    public static final BlockItem HOOK_BELL_ITEM = Registry.register(BuiltInRegistries.ITEM,
            HookBellPort.location("hook_bell"), new BlockItem(HOOK_BELL, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, HookBellPort.location("hook_bell")))));
    public static final BlockEntityType<HookBellBlockEntity> HOOK_BELL_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, HookBellPort.location("hook_bell"),
            FabricBlockEntityTypeBuilder.create(HookBellBlockEntity::new, HOOK_BELL).build());

    public static void init() {}
    private HookBellContent() {}
}
