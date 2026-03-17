package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.blocks.PlayerDetectorBlock;
import de.srendi.advancedperipherals.common.blocks.base.APBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.APBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class APBlocks {

    public static final DeferredHolder<Block, BaseBlock> PERIPHERAL_CASING = register("peripheral_casing", BaseBlock::new, () -> new APBlockItem(APBlocks.PERIPHERAL_CASING.get(), new Item.Properties().stacksTo(16), () -> true));

    public static final DeferredHolder<Block, APBlockEntityBlock<?>> CHAT_BOX = register("chat_box", () -> new APBlockEntityBlock<>(APBlockEntityTypes.CHAT_BOX, true), () -> new APBlockItem(APBlocks.CHAT_BOX.get(), APConfig.PERIPHERALS_CONFIG.enableChatBox));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> DISTANCE_DETECTOR = register("distance_detector", () -> new APBlockEntityBlock<>(APBlockEntityTypes.DISTANCE_DETECTOR, true), () -> new APBlockItem(APBlocks.DISTANCE_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enableNBTStorage));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> ENVIRONMENT_DETECTOR = register("environment_detector", () -> new APBlockEntityBlock<>(APBlockEntityTypes.ENVIRONMENT_DETECTOR, false), () -> new APBlockItem(APBlocks.ENVIRONMENT_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enableEnvironmentDetector));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> GEO_SCANNER = register("geo_scanner", () -> new APBlockEntityBlock<>(APBlockEntityTypes.GEO_SCANNER, false), () -> new APBlockItem(APBlocks.GEO_SCANNER.get(), APConfig.PERIPHERALS_CONFIG.enableGeoScanner));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> PLAYER_DETECTOR = register("player_detector", PlayerDetectorBlock::new, () -> new APBlockItem(APBlocks.PLAYER_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enablePlayerDetector));

    public static final DeferredHolder<Block, APBlockEntityBlock<?>> ENERGY_DETECTOR = register("energy_detector", () -> new APBlockEntityBlock<>(APBlockEntityTypes.ENERGY_DETECTOR, true), () -> new APBlockItem(APBlocks.ENERGY_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enableEnergyDetector));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> FLUID_DETECTOR = register("fluid_detector", () -> new APBlockEntityBlock<>(APBlockEntityTypes.FLUID_DETECTOR, true), () -> new APBlockItem(APBlocks.FLUID_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enableFluidDetector));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> GAS_DETECTOR = register("gas_detector", () -> new APBlockEntityBlock<>(APAddon.MEKANISM.isLoaded() ? APBlockEntityTypes.GAS_DETECTOR : null, true), () -> new APBlockItem(APBlocks.GAS_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enableGasDetector));

    public static final DeferredHolder<Block, APBlockEntityBlock<?>> BLOCK_READER = register("block_reader", () -> new APBlockEntityBlock<>(APBlockEntityTypes.BLOCK_READER, true), () -> new APBlockItem(APBlocks.BLOCK_READER.get(), APConfig.PERIPHERALS_CONFIG.enableBlockReader));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> NBT_STORAGE = register("nbt_storage", () -> new APBlockEntityBlock<>(APBlockEntityTypes.NBT_STORAGE, false), () -> new APBlockItem(APBlocks.NBT_STORAGE.get(), APConfig.PERIPHERALS_CONFIG.enableNBTStorage));

    public static final DeferredHolder<Block, APBlockEntityBlock<?>> INVENTORY_MANAGER = register("inventory_manager", () -> new APBlockEntityBlock<>(APBlockEntityTypes.INVENTORY_MANAGER, false), () -> new APBlockItem(APBlocks.INVENTORY_MANAGER.get(), APConfig.PERIPHERALS_CONFIG.enableInventoryManager));

    public static final DeferredHolder<Block, APBlockEntityBlock<?>> COLONY_INTEGRATOR = register("colony_integrator", () -> new APBlockEntityBlock<>(APBlockEntityTypes.COLONY_INTEGRATOR, false), () -> new APBlockItem(APBlocks.COLONY_INTEGRATOR.get(), APConfig.PERIPHERALS_CONFIG.enableColonyIntegrator));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> ME_BRIDGE = register("me_bridge", () -> new APBlockEntityBlock<>(APAddon.AE2.isLoaded() ? APBlockEntityTypes.ME_BRIDGE : null, APAddon.AE2.isLoaded()), () -> new APBlockItem(APBlocks.ME_BRIDGE.get(), APConfig.PERIPHERALS_CONFIG.enableMEBridge));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> RS_BRIDGE = register("rs_bridge", () -> new APBlockEntityBlock<>(APAddon.REFINEDSTORAGE.isLoaded() ? APBlockEntityTypes.RS_BRIDGE : null, false), () -> new APBlockItem(APBlocks.RS_BRIDGE.get(), APConfig.PERIPHERALS_CONFIG.enableRSBridge));

    public static void register() {
    }

    private static <T extends Block> DeferredHolder<Block, T> registerNoItem(String name, Supplier<T> block) {
        return APRegistration.BLOCKS.register(name, block);
    }

    private static <T extends Block> DeferredHolder<Block, T> register(String name, Supplier<T> block, Supplier<BlockItem> blockItem) {
        DeferredHolder<Block, T> registryObject = registerNoItem(name, block);
        APRegistration.ITEMS.register(name, blockItem);
        return registryObject;
    }

}
