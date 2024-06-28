package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class APRegistration {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<TurtleUpgradeSerialiser<?>> TURTLE_SERIALIZER = DeferredRegister.create(TurtleUpgradeSerialiser.REGISTRY_ID, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<PocketUpgradeSerialiser<?>> POCKET_SERIALIZER = DeferredRegister.create(PocketUpgradeSerialiser.REGISTRY_ID, AdvancedPeripherals.MOD_ID);

    public static void register() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);
        CONTAINER_TYPES.register(modEventBus);
        POI_TYPES.register(modEventBus);
        VILLAGER_PROFESSIONS.register(modEventBus);
        TURTLE_SERIALIZER.register(modEventBus);
        POCKET_SERIALIZER.register(modEventBus);

        APBlocks.register();
        APBlockEntityTypes.register();
        APItems.register();
        APEntities.register();
        APContainerTypes.register();
        APVillagers.register();
        CCRegistration.register();
        APTags.Items.init();
    }
}
