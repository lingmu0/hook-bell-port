package net.xuwu.hookbellport.content;

import net.xuwu.hookbellport.HookBellPort;
import net.xuwu.hookbellport.block.HookBellBlock;
import net.xuwu.hookbellport.block.entity.HookBellBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class HookBellContent {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, HookBellPort.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, HookBellPort.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HookBellPort.MOD_ID);

    public static final DeferredHolder<Block, HookBellBlock> HOOK_BELL = BLOCKS.register("hook_bell", HookBellBlock::new);
    public static final DeferredHolder<Item, Item> HOOK_BELL_ITEM = ITEMS.register("hook_bell",
            () -> new BlockItem(HOOK_BELL.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HookBellBlockEntity>> HOOK_BELL_ENTITY =
            BLOCK_ENTITY_TYPES.register("hook_bell",
                    () -> BlockEntityType.Builder.of(HookBellBlockEntity::new, HOOK_BELL.get()).build(null));

    private HookBellContent() {
    }
}
