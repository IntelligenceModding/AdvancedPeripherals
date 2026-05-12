package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.function.Function;

public class APRenderTypes {
    public static final RenderType TRIANGLE_FAN_2D = RenderType.create(
        "ap_overlay_2d_triangle_fan",
        DefaultVertexFormat.POSITION_COLOR,
        VertexFormat.Mode.TRIANGLE_FAN,
        1536,
        false,
        false,
        RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false)
    );

    public static final RenderType TRIANGLE_STRIP_2D = RenderType.create(
        "ap_overlay_2d_triangle_strip",
        DefaultVertexFormat.POSITION_COLOR,
        VertexFormat.Mode.TRIANGLE_STRIP,
        1536,
        false,
        false,
        RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false)
    );

    public static final RenderType QUADS_2D = RenderType.create(
        "ap_overlay_2d_quads",
        DefaultVertexFormat.POSITION_COLOR,
        VertexFormat.Mode.QUADS,
        1536,
        false,
        false,
        RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false)
    );

    public static final Function<ThreeDimensionalObject, RenderType> BLOCK_MAP = create3DRenderTypeGetter(
        "ap_overlay_translucent_block",
        DefaultVertexFormat.BLOCK,
        VertexFormat.Mode.QUADS,
        786432,
        true,
        true,
        RenderType.CompositeState.builder()
            .setLightmapState(RenderStateShard.LIGHTMAP)
            .setShaderState(RenderStateShard.RENDERTYPE_TRANSLUCENT_SHADER)
            .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
    );

    public static final Function<ThreeDimensionalObject, RenderType> TRIANGLE_3D_MAP = create3DRenderTypeGetter(
        "ap_overlay_3d_triangle",
        DefaultVertexFormat.POSITION_COLOR,
        VertexFormat.Mode.TRIANGLES,
        1536,
        false,
        true,
        RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
    );

    public static final Function<ThreeDimensionalObject, RenderType> QUADS_3D_MAP = create3DRenderTypeGetter(
        "ap_overlay_3d_quads",
        DefaultVertexFormat.POSITION_COLOR,
        VertexFormat.Mode.QUADS,
        1536,
        false,
        true,
        RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
    );

    private static Function<ThreeDimensionalObject, RenderType> create3DRenderTypeGetter(
        String name,
        VertexFormat format,
        VertexFormat.Mode mode,
        int bufferSize,
        boolean affectsCrumbling,
        boolean sortOnUpload,
        RenderType.CompositeState.CompositeStateBuilder stateBuilder
    ) {
        int size = 2 * 2;
        RenderType[] types = new RenderType[size];
        for (int i = 0; i < size; i++) {
            boolean culling = RenderTypeStateFlag.CULL.has(i);
            boolean depthTest = RenderTypeStateFlag.DEPTH_TEST.has(i);
            boolean depthMask = RenderTypeStateFlag.DEPTH_MASK.has(i);
            types[i] = RenderType.create(
                name
                    + (culling ? "" : "_nocull")
                    + (depthTest ? "" : "_nodepth")
                    + (depthMask ? "" : "_nodmask"),
                format,
                mode,
                bufferSize,
                affectsCrumbling,
                sortOnUpload,
                stateBuilder
                    .setCullState(culling ? RenderStateShard.CULL : RenderStateShard.NO_CULL)
                    .setDepthTestState(depthTest ? RenderStateShard.LEQUAL_DEPTH_TEST : RenderStateShard.NO_DEPTH_TEST)
                    .setWriteMaskState(depthMask ? RenderStateShard.COLOR_DEPTH_WRITE : RenderStateShard.COLOR_WRITE)
                    .createCompositeState(false)
            );
        }
        return (o) -> types[RenderTypeStateFlag.combineFlags(o.culling, o.depthTest, o.depthMask)];
    }

    private enum RenderTypeStateFlag {
        CULL,
        DEPTH_TEST,
        DEPTH_MASK;

        public boolean has(int flags) {
            return (flags & (1 << this.ordinal())) != 0;
        }

        static int combineFlags(
            boolean culling,
            boolean depthTest,
            boolean depthMask
        ) {
            return
                (culling ? (1 << CULL.ordinal()) : 0) |
                (depthTest ? (1 << DEPTH_TEST.ordinal()) : 0) |
                (depthMask ? (1 << DEPTH_MASK.ordinal()) : 0);
        }
    }
}
