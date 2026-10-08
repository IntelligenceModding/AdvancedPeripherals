package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.entity.SmartChestHand;
import de.srendi.advancedperipherals.common.entity.TurtleEnderPearl;
import de.srendi.advancedperipherals.common.entity.TurtleSeatEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class APEntities {

    public static final RegistryObject<EntityType<TurtleEnderPearl>> TURTLE_ENDER_PEARL = APRegistration.ENTITIES.register("turtle_ender_pearl",
        () -> EntityType.Builder.<TurtleEnderPearl>of(TurtleEnderPearl::new, MobCategory.MISC)
            .noSave()
            .noSummon()
            .sized(0.5f, 0.5f)
            .clientTrackingRange(4)
            .updateInterval(4)
            .fireImmune()
            .build("turtle_ender_pearl"));
    public static final RegistryObject<EntityType<TurtleSeatEntity>> TURTLE_SEAT = APRegistration.ENTITIES.register("turtle_seat",
        () -> EntityType.Builder.<TurtleSeatEntity>of(TurtleSeatEntity::new, MobCategory.MISC)
            .noSummon()
            .sized(0.8f, 0.8f)
            .updateInterval(1)
            .fireImmune()
            .build("turtle_seat"));
    public static final RegistryObject<EntityType<SmartChestHand>> SMART_CHEST_HAND = APRegistration.ENTITIES.register("smart_chest_hand",
        () -> EntityType.Builder.<SmartChestHand>of(SmartChestHand::new, MobCategory.MISC)
            .noSave()
            .noSummon()
            .sized(0.4f, 0.4f)
            .clientTrackingRange(32)
            .updateInterval(2)
            .fireImmune()
            .build("smart_chest_hand"));

    public static void register() {

    }

    @SubscribeEvent
    public static void livingRender(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(TURTLE_ENDER_PEARL.get(), TurtleEnderPearl.Renderer::new);
        event.registerEntityRenderer(TURTLE_SEAT.get(), TurtleSeatEntity.Renderer::new);
        event.registerEntityRenderer(SMART_CHEST_HAND.get(), SmartChestHand.Renderer::new);
    }
}
