package net.xuwu.hookbellport.client;

import net.xuwu.hookbellport.HookBellPort;
import net.xuwu.hookbellport.block.entity.HookBellBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class HookBellRenderer implements BlockEntityRenderer<HookBellBlockEntity> {
    private static final net.minecraft.resources.ResourceLocation TEXTURE =
            HookBellPort.location("textures/entity/hook_bell/bell_body.png");
    private final ModelPart bellBody;

    public HookBellRenderer(BlockEntityRendererProvider.Context context) {
        this.bellBody = context.bakeLayer(ModelLayers.BELL).getChild("bell_body");
    }

    @Override
    public void render(HookBellBlockEntity bell, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        float age = bell.ticks + partialTick;
        float xRotation = 0.0F;
        float zRotation = 0.0F;
        if (bell.shaking) {
            float swing = (float) Math.sin(age / Math.PI) / (4.0F + age / 3.0F);
            if (bell.clickDirection == Direction.NORTH) xRotation = -swing;
            else if (bell.clickDirection == Direction.SOUTH) xRotation = swing;
            else if (bell.clickDirection == Direction.EAST) zRotation = -swing;
            else if (bell.clickDirection == Direction.WEST) zRotation = swing;
        }

        bellBody.xRot = xRotation;
        bellBody.zRot = zRotation;
        VertexConsumer consumer = buffers.getBuffer(RenderType.entitySolid(TEXTURE));
        bellBody.render(poseStack, consumer, packedLight, packedOverlay);
    }
}
