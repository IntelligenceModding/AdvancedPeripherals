package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.entity.TurtleEnderPearl;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class APEntities {

    public static final RegistryObject<EntityType<TurtleEnderPearl>> TURTLE_ENDER_PEARL = APRegistration.ENTITIES.register("turtle_ender_pearl",
        () -> EntityType.Builder.<TurtleEnderPearl>of(TurtleEnderPearl::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(4)
            .updateInterval(5)
            .fireImmune()
            .build("turtle_ender_pearl"));
    public static final RegistryObject<EntityType<TurtleSeatEntity>> TURTLE_SEAT = APRegistration.ENTITIES.register("turtle_seat",
        () -> EntityType.Builder.<TurtleSeatEntity>of(TurtleSeatEntity::new, MobCategory.MISC)
            .sized(0.8F, 0.2F)
            .clientTrackingRange(4)
            .updateInterval(4)
            .fireImmune()
            .build("turtle_seat"));

    public static void register() {
    }

    @SubscribeEvent
    public static void livingRender(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(TURTLE_ENDER_PEARL.get(), TurtleEnderPearl.Renderer::new);
        event.registerEntityRenderer(TURTLE_SEAT.get(), TurtleSeatEntity.Renderer::new);
    }
}
