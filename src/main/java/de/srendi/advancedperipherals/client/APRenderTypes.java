package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class APRenderTypes extends RenderType {

    public static final ResourceLocation GLOW_MASK = AdvancedPeripherals.getRL("textures/block/shader/glow_mask.png");

    public APRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static final RenderType GLOWING_BLOCK = create("glowing_block", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 131_072, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(APShaders.GLOWING_BLOCK_SHADER.shard)
                    .setLightmapState(LIGHTMAP)
                    .setWriteMaskState(COLOR_WRITE)
                    .setTextureState(new RenderStateShard.TextureStateShard(GLOW_MASK, false, false))
                    .setOverlayState(OVERLAY)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .createCompositeState(false));
}
