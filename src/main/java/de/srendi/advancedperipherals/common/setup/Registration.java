package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.registries.DeferredRegister;

public class Registration {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(Registries.MENU, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<TurtleUpgradeSerialiser<?>> TURTLE_SERIALIZER = DeferredRegister.create(TurtleUpgradeSerialiser.registryId(), AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<PocketUpgradeSerialiser<?>> POCKET_SERIALIZER = DeferredRegister.create(PocketUpgradeSerialiser.registryId(), AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AdvancedPeripherals.MOD_ID);

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);
        CONTAINER_TYPES.register(modEventBus);
        POI_TYPES.register(modEventBus);
        VILLAGER_PROFESSIONS.register(modEventBus);
        TURTLE_SERIALIZER.register(modEventBus);
        POCKET_SERIALIZER.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        Blocks.register();
        BlockEntityTypes.register();
        Items.register();
        ContainerTypes.register();
        Villagers.register();
        CCRegistration.register();
        CreativeTabs.register();
    }
}
