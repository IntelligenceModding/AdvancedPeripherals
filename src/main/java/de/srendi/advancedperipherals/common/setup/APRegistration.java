package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import dan200.computercraft.shared.media.MountMedia;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.blocks.base.BlockCapabilityProviders;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryBuilder;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

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
        APOverlayObjects.register();
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

        modEventBus.addListener(APRegistration::registerCapabilities);
        modEventBus.addListener(APRegistration::onCommonSetup);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // CC:T's registries are not thread safe
            CCRegistration.registerMain();
        });
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        BLOCK_ENTITIES.getEntries().forEach((entry) -> {
            @SuppressWarnings("rawtypes")
            BlockEntityType beType = entry.get();

            event.registerBlockEntity(
                PeripheralCapability.get(),
                beType,
                (blockEntity, side) -> blockEntity instanceof BlockCapabilityProviders.Peripheral provider
                    ? provider.createPeripheralCap(side)
                    : null
            );
            event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                beType,
                (blockEntity, side) -> blockEntity instanceof BlockCapabilityProviders.ItemHandler provider
                    ? provider.createItemHandlerCap(side)
                    : null
            );
            event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                beType,
                (blockEntity, side) -> blockEntity instanceof BlockCapabilityProviders.FluidHandler provider
                    ? provider.createFluidHandlerCap(side)
                    : null
            );
            event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                beType,
                (blockEntity, side) -> blockEntity instanceof BlockCapabilityProviders.EnergyStorage provider
                    ? provider.createEnergyStorageCap(side)
                    : null
            );
            if (APAddon.MEKANISM.isLoaded()) {
                event.registerBlockEntity(
                    mekanism.common.capabilities.Capabilities.CHEMICAL.block(),
                    beType,
                    (blockEntity, side) -> blockEntity instanceof BlockCapabilityProviders.ChemicalHandler provider
                        ? (IChemicalHandler) provider.createChemicalHandlerCap(side)
                        : null
                );
            }
        });

        ItemLike[] smartGlasses = new ItemLike[]{
            APItems.SMART_GLASSES.get(),
            APItems.SMART_GLASSES_NETHERITE.get(),
        };
        event.registerItem(MediaCapability.get(), (stack, ignored) -> MountMedia.COMPUTER, smartGlasses);
        event.registerItem(
            Capabilities.ItemHandler.ITEM,
            (stack, ignored) -> ((SmartGlassesItem) stack.getItem()).createItemHandlerCap(stack),
            smartGlasses
        );
        if (APAddon.CURIOS.isLoaded()) {
            event.registerItem(
                CuriosCapability.ITEM,
                (stack, ignored) -> (ICurio) ((SmartGlassesItem) stack.getItem()).createCurioCap(stack),
                smartGlasses
            );
        }
    }
}
