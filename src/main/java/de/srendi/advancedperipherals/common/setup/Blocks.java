package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.blocks.PlayerDetectorBlock;
import de.srendi.advancedperipherals.common.blocks.RedstoneIntegratorBlock;
import de.srendi.advancedperipherals.common.blocks.base.APBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.APBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.registries.RegistryObject;

import java.util.function.Supplier;

public class Blocks {

    public static final DeferredHolder<Block, APBlockEntityBlock<?>> ENVIRONMENT_DETECTOR = register("environment_detector", () -> new APBlockEntityBlock<>(BlockEntityTypes.ENVIRONMENT_DETECTOR, false), () -> new APBlockItem(Blocks.ENVIRONMENT_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enableEnvironmentDetector::get));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> CHAT_BOX = register("chat_box", () -> new APBlockEntityBlock<>(BlockEntityTypes.CHAT_BOX, true), () -> new APBlockItem(Blocks.CHAT_BOX.get(), APConfig.PERIPHERALS_CONFIG.enableChatBox::get));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> PLAYER_DETECTOR = register("player_detector", PlayerDetectorBlock::new, () -> new APBlockItem(Blocks.PLAYER_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enablePlayerDetector::get));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> ME_BRIDGE = register("me_bridge", () -> new APBlockEntityBlock<>(ModList.get().isLoaded("ae2") ? BlockEntityTypes.ME_BRIDGE : null, ModList.get().isLoaded("ae2")), () -> new APBlockItem(Blocks.ME_BRIDGE.get(), APConfig.PERIPHERALS_CONFIG.enableMEBridge::get));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> RS_BRIDGE = register("rs_bridge", () -> new APBlockEntityBlock<>(ModList.get().isLoaded("refinedstorage") ? BlockEntityTypes.RS_BRIDGE : null, false), () -> new APBlockItem(Blocks.RS_BRIDGE.get(), APConfig.PERIPHERALS_CONFIG.enableRSBridge::get));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> ENERGY_DETECTOR = register("energy_detector", () -> new APBlockEntityBlock<>(BlockEntityTypes.ENERGY_DETECTOR, true), () -> new APBlockItem(Blocks.ENERGY_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enableEnergyDetector::get));
    public static final DeferredHolder<Block, BaseBlock> PERIPHERAL_CASING = register("peripheral_casing", BaseBlock::new, () -> new APBlockItem(Blocks.PERIPHERAL_CASING.get(), new Item.Properties().stacksTo(16), () -> true));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> INVENTORY_MANAGER = register("inventory_manager", () -> new APBlockEntityBlock<>(BlockEntityTypes.INVENTORY_MANAGER, false), () -> new APBlockItem(Blocks.INVENTORY_MANAGER.get(), APConfig.PERIPHERALS_CONFIG.enableInventoryManager::get));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> REDSTONE_INTEGRATOR = register("redstone_integrator", RedstoneIntegratorBlock::new, () -> new APBlockItem(Blocks.REDSTONE_INTEGRATOR.get(), APConfig.PERIPHERALS_CONFIG.enableRedstoneIntegrator::get));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> BLOCK_READER = register("block_reader", () -> new APBlockEntityBlock<>(BlockEntityTypes.BLOCK_READER, true), () -> new APBlockItem(Blocks.BLOCK_READER.get(), APConfig.PERIPHERALS_CONFIG.enableBlockReader::get));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> GEO_SCANNER = register("geo_scanner", () -> new APBlockEntityBlock<>(BlockEntityTypes.GEO_SCANNER, false), () -> new APBlockItem(Blocks.GEO_SCANNER.get(), APConfig.PERIPHERALS_CONFIG.enableGeoScanner::get));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> COLONY_INTEGRATOR = register("colony_integrator", () -> new APBlockEntityBlock<>(ModList.get().isLoaded("minecolonies") ? BlockEntityTypes.COLONY_INTEGRATOR : null, false), () -> new APBlockItem(Blocks.COLONY_INTEGRATOR.get(), APConfig.PERIPHERALS_CONFIG.enableColonyIntegrator::get));
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> NBT_STORAGE = register("nbt_storage", () -> new APBlockEntityBlock<>(BlockEntityTypes.NBT_STORAGE, false), () -> new APBlockItem(Blocks.NBT_STORAGE.get(), APConfig.PERIPHERALS_CONFIG.enableNBTStorage::get));

    public static void register() {
    }

    private static <T extends Block> DeferredHolder<Block, T> registerNoItem(String name, Supplier<T> block) {
        return Registration.BLOCKS.register(name, block);
    }

    private static <T extends Block> DeferredHolder<Block, T> register(String name, Supplier<T> block, Supplier<BlockItem> blockItem) {
        DeferredHolder<Block, T> registryObject = registerNoItem(name, block);
        Registration.ITEMS.register(name, blockItem);
        return registryObject;
    }

    public static boolean never(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

}
