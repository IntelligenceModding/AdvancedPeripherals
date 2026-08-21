package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

public class APRegistration {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(Registries.MENU, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<TurtleUpgradeSerialiser<?>> TURTLE_SERIALIZER = DeferredRegister.create(TurtleUpgradeSerialiser.registryId(), AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<PocketUpgradeSerialiser<?>> POCKET_SERIALIZER = DeferredRegister.create(PocketUpgradeSerialiser.registryId(), AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AdvancedPeripherals.MOD_ID);

    public static final DeferredRegister<OverlayObjectType<?>> OVERLAY_OBJECTS = DeferredRegister.create(APRegistries.OVERLAY_OBJECTS, AdvancedPeripherals.MOD_ID);

    public static void register(IEventBus modEventBus) {
        APBlocks.register();
        APItems.register();
        APBlockEntityTypes.register();
        APContainerTypes.register();
        APTags.register();
        APVillagers.register();
        APEntities.register();
        APCreativeTabs.register();
        APDataComponents.register();
        CCRegistration.register();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CONTAINER_TYPES.register(modEventBus);
        POI_TYPES.register(modEventBus);
        VILLAGER_PROFESSIONS.register(modEventBus);
        ENTITIES.register(modEventBus);
        TURTLE_SERIALIZER.register(modEventBus);
        POCKET_SERIALIZER.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        OVERLAY_OBJECTS.register(modEventBus);

        modEventBus.addListener(APRegistration::onCommonSetup);
    }

    private static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey() == APRegistries.OVERLAY_OBJECTS) {
            APOverlayObjects.register();
        }
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        APNetworking.init();
        event.enqueueWork(() -> {
            // CC:T's registries are not thread safe
            CCRegistration.registerMain();
        });
    }
}
