package net.xuwu.hookbellport.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.xuwu.hookbellport.content.HookBellContent;

public final class HookBellClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(HookBellContent.HOOK_BELL_ENTITY, HookBellRenderer::new);
    }
}
