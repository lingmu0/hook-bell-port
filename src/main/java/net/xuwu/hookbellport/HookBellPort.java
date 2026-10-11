package net.xuwu.hookbellport;

import net.xuwu.hookbellport.client.HookBellRenderer;
import net.xuwu.hookbellport.content.HookBellConfig;
import net.xuwu.hookbellport.content.HookBellContent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(HookBellPort.MOD_ID)
public class HookBellPort {
    public static final String MOD_ID = "hookbellport";

    public HookBellPort(IEventBus modBus, ModContainer modContainer) {
        HookBellContent.BLOCKS.register(modBus);
        HookBellContent.BLOCK_ENTITY_TYPES.register(modBus);
        HookBellContent.ITEMS.register(modBus);
        modBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, HookBellConfig.SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(HookBellContent.HOOK_BELL_ITEM.get());
        }
    }

    public static Identifier location(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static final class ClientModEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(HookBellContent.HOOK_BELL_ENTITY.get(), HookBellRenderer::new);
        }

        private ClientModEvents() {
        }
    }
}
