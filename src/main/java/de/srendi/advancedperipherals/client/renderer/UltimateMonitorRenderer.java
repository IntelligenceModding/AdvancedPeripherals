/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package de.srendi.advancedperipherals.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.client.FrameInfo;
import dan200.computercraft.client.render.text.FixedWidthFontRenderer;
import dan200.computercraft.client.util.DirectBuffers;
import dan200.computercraft.client.util.DirectVertexBuffer;
import dan200.computercraft.shared.integration.ShaderMod;
import dan200.computercraft.shared.peripheral.monitor.MonitorRenderer;
import dan200.computercraft.shared.util.DirectionUtil;
import de.srendi.advancedperipherals.client.ClientEventSubscriber;
import de.srendi.advancedperipherals.client.renderer.text.DirectFixedWidthFontRenderer;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.monitor.UltimateClientMonitor;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.monitor.UltimateMonitorEntity;
import de.srendi.advancedperipherals.common.addons.computercraft.terminal.UltimateNetworkedTerminal;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import static dan200.computercraft.client.render.text.FixedWidthFontRenderer.FONT_HEIGHT;
import static dan200.computercraft.client.render.text.FixedWidthFontRenderer.FONT_WIDTH;

public class UltimateMonitorRenderer implements BlockEntityRenderer<UltimateMonitorEntity> {

    /**
     * {@link UltimateMonitorEntity#RENDER_MARGIN}, but a tiny bit of additional padding to ensure that there is no space between
     * the monitor frame and contents.
     */
    private static final float MARGIN = (float) (UltimateMonitorEntity.RENDER_MARGIN * 1.1);

    private static final Matrix3f IDENTITY_NORMAL = Util.make( new Matrix3f(), Matrix3f::setIdentity );

    private static ByteBuffer backingBuffer;

    public UltimateMonitorRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render( @Nonnull UltimateMonitorEntity monitor, float partialTicks, @Nonnull PoseStack transform, @Nonnull MultiBufferSource bufferSource, int lightmapCoord, int overlayLight ) {
        // Render from the origin monitor
        UltimateClientMonitor originTerminal = monitor.getClientMonitor();

        if (originTerminal == null) {
            return;
        }
        UltimateMonitorEntity origin = originTerminal.getOrigin();
        UltimateNetworkedTerminal terminal = originTerminal.getTerminal();
        float panelDepth = terminal == null ? 0 : terminal.getPanelDepth();
        BlockPos monitorPos = monitor.getBlockPos();
        // Ensure each monitor terminal is rendered only once. We allow rendering a specific tile
        // multiple times in a single frame to ensure compatibility with shaders which may run a
        // pass multiple times.
        long renderFrame = FrameInfo.getRenderFrame();
        if (originTerminal.lastRenderFrame == renderFrame && !monitorPos.equals(originTerminal.lastRenderPos)) {
            return;
        }

        originTerminal.lastRenderFrame = renderFrame;
        originTerminal.lastRenderPos = monitorPos;

        BlockPos originPos = origin.getBlockPos();
        double oHeight = origin.getHeight();
        double oWidth = origin.getWidth();
        double xSize = oWidth - 2.0 * (UltimateMonitorEntity.RENDER_MARGIN + UltimateMonitorEntity.RENDER_BORDER);
        double ySize = oHeight - 2.0 * (UltimateMonitorEntity.RENDER_MARGIN + UltimateMonitorEntity.RENDER_BORDER);

        // Determine orientation
        Direction dir = origin.getDirection();
        Direction front = origin.getFront();
        float yaw = dir.toYRot();
        float pitch = DirectionUtil.toPitchAngle(front);

        // Setup initial transform
        transform.pushPose();
        transform.translate(
            originPos.getX() - monitorPos.getX() + 0.5,
            originPos.getY() - monitorPos.getY() + 0.5,
            originPos.getZ() - monitorPos.getZ() + 0.5
        );

        transform.mulPose(Vector3f.YN.rotationDegrees(yaw));
        transform.mulPose(Vector3f.XP.rotationDegrees(pitch));
        transform.translate(
            -0.5 + UltimateMonitorEntity.RENDER_BORDER + UltimateMonitorEntity.RENDER_MARGIN,
            oHeight - 0.5 - (UltimateMonitorEntity.RENDER_BORDER + UltimateMonitorEntity.RENDER_MARGIN) + 0,
            0.5 - panelDepth
        );

        transform.pushPose();
        RenderSystem.disableCull();

        // Draw the contents
        if (terminal != null) {
            // Draw a terminal
            int width = terminal.getWidth(), height = terminal.getHeight();
            int pixelWidth = width * FONT_WIDTH, pixelHeight = height * FONT_HEIGHT;
            double xScale = xSize / pixelWidth;
            double yScale = ySize / pixelHeight;

            transform.scale((float) xScale, (float) -yScale, 1.0f);

            Matrix4f matrix = transform.last().pose();
            renderTerminal( matrix, originTerminal, (float) (MARGIN / xScale), (float) (MARGIN / yScale) );
        } else {
            FixedWidthFontRenderer.drawEmptyTerminal(
                FixedWidthFontRenderer.toVertexConsumer( transform, bufferSource.getBuffer( RenderTypes.TERMINAL ) ),
                -MARGIN, MARGIN,
                (float) (xSize + 2 * MARGIN), (float) -(ySize + MARGIN * 2)
            );
        }

        RenderSystem.enableCull();
        transform.popPose();
        transform.popPose();
    }

    // TODO: High-priority: make the transparency works without cutting other renders behind it.
    // TODO: High-priority: the renderer will ignore alpha channel (means render as a = 1) when camera is close to the panel enough and/or looking at some specific angle
    private static void renderTerminal(Matrix4f matrix, UltimateClientMonitor monitor, float xMargin, float yMargin) {
        UltimateNetworkedTerminal terminal = monitor.getTerminal();
        int width = terminal.getWidth(), height = terminal.getHeight();
        int pixelWidth = width * FONT_WIDTH, pixelHeight = height * FONT_HEIGHT;

        MonitorRenderer renderType = MonitorRenderer.current();
        boolean redraw = monitor.pollTerminalChanged();
        if( monitor.createBuffer( renderType ) ) redraw = true;

        switch(renderType) {
            case TBO:
            {
                if (redraw) {
                    var terminalBuffer = getBuffer( width * height * 3 );
                    UltimateMonitorTextureBufferShader.setTerminalData( terminalBuffer, terminal );
                    DirectBuffers.setBufferData( GL31.GL_TEXTURE_BUFFER, monitor.tboBuffer, terminalBuffer, GL20.GL_STATIC_DRAW );

                    var uniformBuffer = getBuffer( UltimateMonitorTextureBufferShader.UNIFORM_SIZE );
                    UltimateMonitorTextureBufferShader.setUniformData( uniformBuffer, terminal );
                    DirectBuffers.setBufferData( GL31.GL_UNIFORM_BUFFER, monitor.tboUniform, uniformBuffer, GL20.GL_STATIC_DRAW );
                }

                // Nobody knows what they're doing!
                int active = GlStateManager._getActiveTexture();
                RenderSystem.activeTexture( UltimateMonitorTextureBufferShader.TEXTURE_INDEX );
                GL11.glBindTexture( GL31.GL_TEXTURE_BUFFER, monitor.tboTexture );
                RenderSystem.activeTexture( active );

                UltimateMonitorTextureBufferShader shader = RenderTypes.getMonitorTextureBufferShader();
                shader.setupUniform(monitor.tboUniform);

                BufferBuilder buffer = Tesselator.getInstance().getBuilder();
                buffer.begin(RenderTypes.MONITOR_TBO.mode(), RenderTypes.MONITOR_TBO.format());
                tboVertex(buffer, matrix, -xMargin, -yMargin);
                tboVertex(buffer, matrix, -xMargin, pixelHeight + yMargin);
                tboVertex(buffer, matrix, pixelWidth + xMargin, -yMargin);
                tboVertex(buffer, matrix, pixelWidth + xMargin, pixelHeight + yMargin);
                RenderTypes.MONITOR_TBO.end(buffer, 0, 0, 0);

                break;
            }

            case VBO:
            {
                var backgroundBuffer = monitor.backgroundBuffer;
                var foregroundBuffer = monitor.foregroundBuffer;
                if (redraw) {
                    int size = DirectFixedWidthFontRenderer.getVertexCount(terminal);

                    // In an ideal world we could upload these both into one buffer. However, we can't render VBOs with
                    // and starting and ending offset, and so need to use two buffers instead.

                    renderToBuffer(backgroundBuffer, size, sink ->
                        DirectFixedWidthFontRenderer.drawTerminalBackground(sink, 0, 0, terminal, yMargin, yMargin, xMargin, xMargin));

                    renderToBuffer(foregroundBuffer, size, sink -> {
                        DirectFixedWidthFontRenderer.drawTerminalForeground(sink, 0, 0, terminal);
                        // If the cursor is visible, we append it to the end of our buffer. When rendering, we can either
                        // render n or n+1 quads and so toggle the cursor on and off.
                        DirectFixedWidthFontRenderer.drawCursor(sink, 0, 0, terminal);
                    });
                }

                // Our VBO doesn't transform its vertices with the provided pose stack, which means that the inverse view
                // rotation matrix gives entirely wrong numbers for fog distances. We just set it to the identity which
                // gives a good enough approximation.
                Matrix3f oldInverseRotation = RenderSystem.getInverseViewRotationMatrix();
                RenderSystem.setInverseViewRotationMatrix( IDENTITY_NORMAL );

                RenderTypes.TERMINAL.setupRenderState();

                // Render background geometry
                backgroundBuffer.bind();
                backgroundBuffer.drawWithShader( matrix, RenderSystem.getProjectionMatrix(), RenderTypes.getTerminalShader() );

                // Render foreground geometry with glPolygonOffset enabled.
                GL11.glPolygonOffset( -1.0f, -10.0f );
                GL11.glEnable( GL11.GL_POLYGON_OFFSET_FILL );

                foregroundBuffer.bind();
                foregroundBuffer.drawWithShader(
                    matrix, RenderSystem.getProjectionMatrix(), RenderTypes.getTerminalShader(),
                    // As mentioned in the above comment, render the extra cursor quad if it is visible this frame. Each
                    // // quad has an index count of 6.
                    FixedWidthFontRenderer.isCursorVisible( terminal ) && FrameInfo.getGlobalCursorBlink()
                        ? foregroundBuffer.getIndexCount() + 6 : foregroundBuffer.getIndexCount()
                );

                // Clear state
                GL11.glPolygonOffset( 0.0f, -0.0f );
                GL11.glDisable( GL11.GL_POLYGON_OFFSET_FILL );
                RenderTypes.TERMINAL.clearRenderState();
                VertexBuffer.unbind();

                RenderSystem.setInverseViewRotationMatrix( oldInverseRotation );

                break;
            }
        }
    }

    private static DirectFixedWidthFontRenderer.QuadEmitter getQuadEmitter(int vertexCount, IntFunction<ByteBuffer> makeBuffer) {
        return new DirectFixedWidthFontRenderer.ByteBufferEmitter(
            makeBuffer.apply(RenderTypes.TERMINAL.format().getVertexSize() * vertexCount * 4)
        );
    }

    private static void renderToBuffer(DirectVertexBuffer vbo, int size, Consumer<DirectFixedWidthFontRenderer.QuadEmitter> draw) {
        var sink = getQuadEmitter(size, UltimateMonitorRenderer::getBuffer);
        var buffer = sink.buffer();

        draw.accept(sink);
        buffer.flip();
        vbo.upload(buffer.limit() / sink.format().getVertexSize(), RenderTypes.TERMINAL.mode(), sink.format(), buffer);
    }

    private static void tboVertex(VertexConsumer builder, Matrix4f matrix, float x, float y) {
        // We encode position in the UV, as that's not transformed by the matrix.
        builder.vertex(matrix, x, y, 0).uv(x, y).endVertex();
    }

    @Nonnull
    private static ByteBuffer getBuffer(int capacity) {
        ByteBuffer buffer = backingBuffer;
        if (buffer == null || buffer.capacity() < capacity) {
            buffer = backingBuffer = buffer == null ? MemoryTracker.create(capacity) : MemoryTracker.resize(buffer, capacity);
        }
        buffer.clear();
        return buffer;
    }

    @Override
    public int getViewDistance() {
        return ComputerCraft.monitorDistance;
    }
}
