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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class Blocks {

    static void register() {
    }

    public static final RegistryObject<Block> ENVIRONMENT_DETECTOR = register("environment_detector", () -> new APBlockEntityBlock<>(BlockEntityTypes.ENVIRONMENT_DETECTOR, false), () -> new APBlockItem(Blocks.ENVIRONMENT_DETECTOR.get(), CCRegistration.ID.ENVIRONMENT_TURTLE, CCRegistration.ID.ENVIRONMENT_POCKET, APConfig.PERIPHERALS_CONFIG.enableEnvironmentDetector::get));
    public static final RegistryObject<Block> CHAT_BOX = register("chat_box", () -> new APBlockEntityBlock<>(BlockEntityTypes.CHAT_BOX, true), () -> new APBlockItem(Blocks.CHAT_BOX.get(), CCRegistration.ID.CHATTY_TURTLE, CCRegistration.ID.CHATTY_POCKET, APConfig.PERIPHERALS_CONFIG.enableChatBox::get));
    public static final RegistryObject<Block> PLAYER_DETECTOR = register("player_detector", PlayerDetectorBlock::new, () -> new APBlockItem(Blocks.PLAYER_DETECTOR.get(), CCRegistration.ID.PLAYER_TURTLE, CCRegistration.ID.PLAYER_POCKET, APConfig.PERIPHERALS_CONFIG.enablePlayerDetector::get));
    public static final RegistryObject<Block> ME_BRIDGE = register("me_bridge", () -> new APBlockEntityBlock<>(ModList.get().isLoaded("ae2") ? BlockEntityTypes.ME_BRIDGE : null, ModList.get().isLoaded("ae2")), () -> new APBlockItem(Blocks.ME_BRIDGE.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableMEBridge::get));
    public static final RegistryObject<Block> RS_BRIDGE = register("rs_bridge", () -> new APBlockEntityBlock<>(ModList.get().isLoaded("refinedstorage") ? BlockEntityTypes.RS_BRIDGE : null, false), () -> new APBlockItem(Blocks.RS_BRIDGE.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableRSBridge::get));
    public static final RegistryObject<Block> ENERGY_DETECTOR = register("energy_detector", () -> new APBlockEntityBlock<>(BlockEntityTypes.ENERGY_DETECTOR, true), () -> new APBlockItem(Blocks.ENERGY_DETECTOR.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableEnergyDetector::get));
    public static final RegistryObject<Block> PERIPHERAL_CASING = register("peripheral_casing", BaseBlock::new, () -> new APBlockItem(Blocks.PERIPHERAL_CASING.get(), new Item.Properties().stacksTo(16), null, null, () -> true));
    public static final RegistryObject<Block> AR_CONTROLLER = register("ar_controller", () -> new APBlockEntityBlock<>(BlockEntityTypes.AR_CONTROLLER, false), () -> new APBlockItem(Blocks.AR_CONTROLLER.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableARGoggles::get));
    public static final RegistryObject<Block> INVENTORY_MANAGER = register("inventory_manager", () -> new APBlockEntityBlock<>(BlockEntityTypes.INVENTORY_MANAGER, false), () -> new APBlockItem(Blocks.INVENTORY_MANAGER.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableInventoryManager::get));
    public static final RegistryObject<Block> REDSTONE_INTEGRATOR = register("redstone_integrator", RedstoneIntegratorBlock::new, () -> new APBlockItem(Blocks.REDSTONE_INTEGRATOR.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableRedstoneIntegrator::get));
    public static final RegistryObject<Block> BLOCK_READER = register("block_reader", () -> new APBlockEntityBlock<>(BlockEntityTypes.BLOCK_READER, true), () -> new APBlockItem(Blocks.BLOCK_READER.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableBlockReader::get));
    public static final RegistryObject<Block> GEO_SCANNER = register("geo_scanner", () -> new APBlockEntityBlock<>(BlockEntityTypes.GEO_SCANNER, false), () -> new APBlockItem(Blocks.GEO_SCANNER.get(), CCRegistration.ID.GEOSCANNER_TURTLE, CCRegistration.ID.GEOSCANNER_POCKET, APConfig.PERIPHERALS_CONFIG.enableGeoScanner::get));
    public static final RegistryObject<Block> COLONY_INTEGRATOR = register("colony_integrator", () -> new APBlockEntityBlock<>(ModList.get().isLoaded("minecolonies") ? BlockEntityTypes.COLONY_INTEGRATOR : null, false), () -> new APBlockItem(Blocks.COLONY_INTEGRATOR.get(), null, CCRegistration.ID.COLONY_POCKET, APConfig.PERIPHERALS_CONFIG.enableColonyIntegrator::get));
    public static final RegistryObject<Block> NBT_STORAGE = register("nbt_storage", () -> new APBlockEntityBlock<>(BlockEntityTypes.NBT_STORAGE, false), () -> new APBlockItem(Blocks.NBT_STORAGE.get(), null, null, APConfig.PERIPHERALS_CONFIG.enableNBTStorage::get));

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return Registration.BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Supplier<BlockItem> blockItem) {
        RegistryObject<T> registryObject = registerNoItem(name, block);
        Registration.ITEMS.register(name, blockItem);
        return registryObject;
    }

    public static boolean never(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

}
