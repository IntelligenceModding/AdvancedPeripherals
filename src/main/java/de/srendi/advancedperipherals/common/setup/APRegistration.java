package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.media.MediaCapability;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import dan200.computercraft.shared.media.MountMedia;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.blocks.base.BlockCapabilityProviders;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObjectType;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class APRegistration {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(Registries.MENU, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<UpgradeType<? extends ITurtleUpgrade>> TURTLE_SERIALIZER = DeferredRegister.create(ITurtleUpgrade.typeRegistry(), AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<UpgradeType<? extends IPocketUpgrade>> POCKET_SERIALIZER = DeferredRegister.create(IPocketUpgrade.typeRegistry(), AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AdvancedPeripherals.MOD_ID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AdvancedPeripherals.MOD_ID);

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
        DATA_COMPONENT_TYPES.register(modEventBus);
        OVERLAY_OBJECTS.register(modEventBus);

        modEventBus.addListener(APRegistration::registerRegistries);
        modEventBus.addListener(APRegistration::registerCapabilities);
        modEventBus.addListener(APRegistration::onCommonSetup);
    }

    private static void registerRegistries(NewRegistryEvent event) {
        event.create(new RegistryBuilder<>(OVERLAY_OBJECTS.getRegistryKey()));
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
