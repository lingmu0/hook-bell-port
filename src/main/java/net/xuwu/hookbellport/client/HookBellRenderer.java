package net.xuwu.hookbellport.client;

import net.xuwu.hookbellport.HookBellPort;
import net.xuwu.hookbellport.block.entity.HookBellBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.bell.BellModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BellRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class HookBellRenderer implements BlockEntityRenderer<HookBellBlockEntity, BellRenderState> {
    private static final Identifier TEXTURE =
            HookBellPort.location("textures/entity/hook_bell/bell_body.png");
    private final BellModel model;

    public HookBellRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new BellModel(context.bakeLayer(ModelLayers.BELL));
    }

    @Override
    public BellRenderState createRenderState() {
        return new BellRenderState();
    }

    @Override
    public void extractRenderState(HookBellBlockEntity bell, BellRenderState state, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(bell, state, partialTick, cameraPos, breakProgress);
        state.ticks = bell.ticks + partialTick;
        state.shakeDirection = bell.shaking ? bell.clickDirection : null;
    }

    @Override
    public void submit(BellRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState cameraState) {
        BellModel.State modelState = new BellModel.State(state.ticks, state.shakeDirection);
        model.setupAnim(modelState);
        collector.submitModel(model, modelState, poseStack, TEXTURE, state.lightCoords,
                OverlayTexture.NO_OVERLAY, -1, state.breakProgress);
    }
}
