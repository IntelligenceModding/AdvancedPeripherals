package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.objects.three_dim.ThreeDimensionalObject;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
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
            .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
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
    );

    public static Function<ThreeDimensionalObject, RenderType> create3DRenderTypeGetter(
        String name,
        VertexFormat format,
        VertexFormat.Mode mode,
        int bufferSize,
        boolean affectsCrumbling,
        boolean sortOnUpload,
        RenderType.CompositeState.CompositeStateBuilder stateBuilder
    ) {
        Map<@NotNull RenderTypeState, @NotNull RenderType> map = Map.of(
            new RenderTypeState(true, true),
            RenderType.create(
                name,
                format,
                mode,
                bufferSize,
                affectsCrumbling,
                sortOnUpload,
                stateBuilder
                    .setCullState(RenderStateShard.CULL)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .createCompositeState(false)
            ),
            new RenderTypeState(true, false),
            RenderType.create(
                name + "_nodepth",
                format,
                mode,
                bufferSize,
                affectsCrumbling,
                sortOnUpload,
                stateBuilder
                    .setCullState(RenderStateShard.CULL)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .createCompositeState(false)
            ),
            new RenderTypeState(false, true),
            RenderType.create(
                name + "_nocull",
                format,
                mode,
                bufferSize,
                affectsCrumbling,
                sortOnUpload,
                stateBuilder
                    .setCullState(RenderStateShard.NO_CULL)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .createCompositeState(false)
            ),
            new RenderTypeState(false, false),
            RenderType.create(
                name + "_nocull_nodepth",
                format,
                mode,
                bufferSize,
                affectsCrumbling,
                sortOnUpload,
                stateBuilder
                    .setCullState(RenderStateShard.NO_CULL)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .createCompositeState(false)
            )
        );
        return (o) -> Objects.requireNonNull(map.get(new RenderTypeState(o.culling, o.depthTest)), "render type getter assert");
    }

    public record RenderTypeState(boolean cull, boolean depthTest) {}
}
