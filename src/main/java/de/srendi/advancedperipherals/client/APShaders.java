package de.srendi.advancedperipherals.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = AdvancedPeripherals.MOD_ID)
public class APShaders {

    public static final ShaderTracker GLOWING_BLOCK_SHADER = new ShaderTracker();

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        registerShader(event, new ResourceLocation(AdvancedPeripherals.MOD_ID, "glowing_block"), DefaultVertexFormat.BLOCK, GLOWING_BLOCK_SHADER);
    }

    private static void registerShader(RegisterShadersEvent event, ResourceLocation shaderLocation, VertexFormat vertexFormat, ShaderTracker tracker) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), shaderLocation, vertexFormat), tracker::setInstance);
    }


    static class ShaderTracker implements Supplier<ShaderInstance> {

        private ShaderInstance instance;
        final RenderStateShard.ShaderStateShard shard = new RenderStateShard.ShaderStateShard(this);

        private ShaderTracker() {
        }

        private void setInstance(ShaderInstance instance) {
            this.instance = instance;
        }

        @Override
        public ShaderInstance get() {
            return instance;
        }
    }
}
