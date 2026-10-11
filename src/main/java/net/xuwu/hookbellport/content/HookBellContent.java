package net.xuwu.hookbellport.content;

import net.xuwu.hookbellport.HookBellPort;
import net.xuwu.hookbellport.block.HookBellBlock;
import net.xuwu.hookbellport.block.entity.HookBellBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class HookBellContent {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(HookBellPort.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(HookBellPort.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HookBellPort.MOD_ID);

    public static final DeferredBlock<HookBellBlock> HOOK_BELL = BLOCKS.registerBlock("hook_bell", HookBellBlock::new);
    public static final DeferredItem<BlockItem> HOOK_BELL_ITEM = ITEMS.registerSimpleBlockItem(HOOK_BELL);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HookBellBlockEntity>> HOOK_BELL_ENTITY =
            BLOCK_ENTITY_TYPES.register("hook_bell",
                    () -> new BlockEntityType<>(HookBellBlockEntity::new, HOOK_BELL.get()));

    private HookBellContent() {
    }
}
