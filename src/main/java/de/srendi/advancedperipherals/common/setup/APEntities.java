package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class APEntities {

    public static final RegistryObject<EntityType<TurtleSeatEntity>> TURTLE_SEAT = Registration.ENTITIES.register("turtle_seat",
        () -> EntityType.Builder.<TurtleSeatEntity>of(TurtleSeatEntity::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(5)
            .updateInterval(5)
            .fireImmune()
            .build("turtle_seat"));

    public static void register() {
    }

    @SubscribeEvent
    public static void livingRender(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(TURTLE_SEAT.get(), TurtleSeatEntity.Renderer::new);
    }
}
