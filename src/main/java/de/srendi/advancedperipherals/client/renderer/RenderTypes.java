/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package de.srendi.advancedperipherals.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.client.render.text.FixedWidthFontRenderer;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.io.IOException;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RenderTypes {
    public static final int FULL_BRIGHT_LIGHTMAP = (0xF << 4) | (0xF << 20);

    private static UltimateMonitorTextureBufferShader monitorTboShader;

    /**
     * Renders a fullbright terminal.
     */
    public static final RenderType TERMINAL = Types.TERMINAL;

    /**
     * Renders a monitor with the TBO shader.
     *
     * @see UltimateMonitorTextureBufferShader
     */
    public static final RenderType MONITOR_TBO = Types.MONITOR_TBO;

    /**
     * A variant of {@link #TERMINAL} which uses the lightmap rather than rendering fullbright.
     */
    public static final RenderType PRINTOUT_TEXT = RenderType.text( FixedWidthFontRenderer.FONT );

    /**
     * Printout's background texture. {@link RenderType#text(ResourceLocation)} is a <em>little</em> questionable, but
     * it is what maps use, so should behave the same as vanilla in both item frames and in-hand.
     */
    public static final RenderType PRINTOUT_BACKGROUND = RenderType.text( new ResourceLocation( ComputerCraft.MOD_ID, "textures/gui/printout.png" ) );

    @Nonnull
    static UltimateMonitorTextureBufferShader getMonitorTextureBufferShader()
    {
        if( monitorTboShader == null ) throw new NullPointerException( "MonitorTboShader has not been registered" );
        return monitorTboShader;
    }

    @Nonnull
    static ShaderInstance getTerminalShader()
    {
        return GameRenderer.getRendertypeTextShader();
    }

    @SubscribeEvent
    public static void registerShaders( RegisterShadersEvent event ) throws IOException
    {
        event.registerShader(
            new UltimateMonitorTextureBufferShader(
                event.getResourceManager(),
                new ResourceLocation( AdvancedPeripherals.MOD_ID, "monitor_tbo" ),
                MONITOR_TBO.format()
            ),
            x -> monitorTboShader = (UltimateMonitorTextureBufferShader) x
        );
    }

    private static final class Types extends RenderStateShard {
        private static final RenderStateShard.TextureStateShard TERM_FONT_TEXTURE = new TextureStateShard(
            FixedWidthFontRenderer.FONT,
            false, false // blur, minimap
        );

        public static final RenderType TERMINAL = RenderType.create(
            "text",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_TEXT_SHADER)
                .setTextureState(TERM_FONT_TEXTURE)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(true)
        );

        static final RenderType MONITOR_TBO = RenderType.create(
            "monitor_tbo", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.TRIANGLE_STRIP, 128,
            true, true, // useDelegate, needsSorting
            RenderType.CompositeState.builder()
                .setTextureState(TERM_FONT_TEXTURE)
                .setShaderState(new ShaderStateShard(RenderTypes::getMonitorTextureBufferShader))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setOutputState(TRANSLUCENT_TARGET)
                .createCompositeState(true)
        );

        private Types(String name, Runnable setup, Runnable destroy) {
            super(name, setup, destroy);
        }
    }
}