package net.xuwu.hookbellport;

import net.xuwu.hookbellport.block.entity.HookBellBlockEntity;
import net.xuwu.hookbellport.client.HookBellRenderer;
import net.xuwu.hookbellport.content.HookBellConfig;
import net.xuwu.hookbellport.content.HookBellContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HookBellPort.MOD_ID)
public class HookBellPort {
    public static final String MOD_ID = "hookbellport";

    public HookBellPort() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        HookBellContent.BLOCKS.register(modBus);
        HookBellContent.BLOCK_ENTITY_TYPES.register(modBus);
        HookBellContent.ITEMS.register(modBus);
        modBus.addListener(this::addCreative);
        HookBellConfig.register();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(HookBellContent.HOOK_BELL_ITEM.get());
        }
    }

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ClientModEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(HookBellContent.HOOK_BELL_ENTITY.get(), HookBellRenderer::new);
        }

        private ClientModEvents() {
        }
    }
}
