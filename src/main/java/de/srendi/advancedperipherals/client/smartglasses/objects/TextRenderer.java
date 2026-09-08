package de.srendi.advancedperipherals.client.smartglasses.objects;

import com.mojang.blaze3d.vertex.VertexConsumer;
import de.srendi.advancedperipherals.client.APRenderTypes;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.TextObject;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.HashMap;
import java.util.Map;

public class TextRenderer extends Simple2DObjectRenderer<TextObject> {
    private final Map<RenderType.CompositeRenderType, RenderType> noCullTexts = new HashMap<>();

    @Override
    protected void render(TextObject text, GuiGraphics gui, float partialTick, boolean is3D) {
        if (text.fontSize == 0) {
            return;
        }
        Font font = gui.minecraft.font;

        float width = font.width(text.content);

        gui.pose().scale(text.fontSize, text.fontSize * (is3D ? -1 : 1), 1);

        int color = (text.color & 0xffffff) | ((int) (Math.min(Math.max(text.opacity, 0), 1) * 0xff) << 24);

        font.drawInBatch(
            text.content,
            text.center ? -width / 2 : 0,
            0,
            color,
            text.shadow,
            gui.pose().last().pose(),
            text.culling ? gui.bufferSource() : this.new NoCullBufferSource(gui.bufferSource()),
            text.depthTest ? Font.DisplayMode.NORMAL : Font.DisplayMode.SEE_THROUGH,
            0,
            LightTexture.FULL_BRIGHT
        );
        gui.flush();
    }

    @Override
    public int getWeight() {
        return 110;
    }

    private RenderType buildNoCullText(RenderType.CompositeRenderType renderType) {
        RenderType.CompositeState state = renderType.state();
        return RenderType.create(
            renderType.toString() + "_no_cull",
            renderType.format(),
            renderType.mode(),
            renderType.bufferSize(),
            renderType.affectsCrumbling(),
            renderType.sortOnUpload,
            RenderType.CompositeState.builder()
                .setTextureState(state.textureState)
                .setShaderState(state.shaderState)
                .setTransparencyState(state.transparencyState)
                .setDepthTestState(state.depthTestState)
                .setCullState(APRenderTypes.NO_CULL)
                .setLightmapState(state.lightmapState)
                .setOverlayState(state.overlayState)
                .setLayeringState(state.layeringState)
                .setOutputState(state.outputState)
                .setTexturingState(state.texturingState)
                .setWriteMaskState(state.writeMaskState)
                .setLineState(state.lineState)
                .setColorLogicState(state.colorLogicState)
                .createCompositeState(state.outlineProperty)
        );
    }

    private final class NoCullBufferSource implements MultiBufferSource {
        private final MultiBufferSource source;

        private NoCullBufferSource(MultiBufferSource source) {
            this.source = source;
        }

        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            if (renderType instanceof RenderType.CompositeRenderType cRenderType) {
                renderType = noCullTexts.computeIfAbsent(cRenderType, TextRenderer.this::buildNoCullText);
            }
            return this.source.getBuffer(renderType);
        }
    }
}
