package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.addons.computercraft.pocket.*;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChatBoxUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleEnvironmentDetectorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleGeoScannerUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtlePlayerDetectorUpgrade;
import de.srendi.advancedperipherals.common.blocks.PeripheralCasingBlock;
import de.srendi.advancedperipherals.common.blocks.PlayerDetectorBlock;
import de.srendi.advancedperipherals.common.blocks.RedstoneIntegratorBlock;
import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.items.APBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class Blocks {

    static void register() {
    }

    public static final RegistryObject<Block> ENVIRONMENT_DETECTOR = register("environment_detector", () -> new APTileEntityBlock<>(TileEntityTypes.ENVIRONMENT_DETECTOR, false),
            () -> new APBlockItem(Blocks.ENVIRONMENT_DETECTOR.get(), TurtleEnvironmentDetectorUpgrade.ID, PocketEnvironmentUpgrade.ID, () -> AdvancedPeripheralsConfig.enableEnvironmentDetector));
    public static final RegistryObject<Block> CHAT_BOX = register("chat_box", () -> new APTileEntityBlock<>(TileEntityTypes.CHAT_BOX, false),
            () -> new APBlockItem(Blocks.CHAT_BOX.get(), TurtleChatBoxUpgrade.ID, PocketChatBoxUpgrade.ID, () -> AdvancedPeripheralsConfig.enableChatBox));
    public static final RegistryObject<Block> PLAYER_DETECTOR = register("player_detector", PlayerDetectorBlock::new,
            () -> new APBlockItem(Blocks.PLAYER_DETECTOR.get(), TurtlePlayerDetectorUpgrade.ID, PocketPlayerDetectorUpgrade.ID, () -> AdvancedPeripheralsConfig.enablePlayerDetector));
    public static final RegistryObject<Block> ME_BRIDGE = register("me_bridge", () -> new APTileEntityBlock<>(TileEntityTypes.ME_BRIDGE, false, ModList.get().isLoaded("appliedenergistics2")),
            () -> new APBlockItem(Blocks.ME_BRIDGE.get(), null, null, () -> AdvancedPeripheralsConfig.enableMeBridge));
    public static final RegistryObject<Block> RS_BRIDGE = register("rs_bridge", () -> new APTileEntityBlock<>(TileEntityTypes.RS_BRIDGE, false, ModList.get().isLoaded("refinedstorage")),
            () -> new APBlockItem(Blocks.RS_BRIDGE.get(), null, null, () -> AdvancedPeripheralsConfig.enableRsBridge));
    public static final RegistryObject<Block> ENERGY_DETECTOR = register("energy_detector", () -> new APTileEntityBlock<>(TileEntityTypes.ENERGY_DETECTOR, true),
            () -> new APBlockItem(Blocks.ENERGY_DETECTOR.get(), null, null, () -> AdvancedPeripheralsConfig.enableEnergyDetector));
    public static final RegistryObject<Block> PERIPHERAL_CASING = register("peripheral_casing", PeripheralCasingBlock::new,
            () -> new APBlockItem(Blocks.PERIPHERAL_CASING.get(), new Item.Properties().stacksTo(16), null, null, () -> true));
    public static final RegistryObject<Block> AR_CONTROLLER = register("ar_controller", () -> new APTileEntityBlock<>(TileEntityTypes.AR_CONTROLLER, false),
            () -> new APBlockItem(Blocks.AR_CONTROLLER.get(), null, null, () -> AdvancedPeripheralsConfig.enableARGoggles));
    public static final RegistryObject<Block> INVENTORY_MANAGER = register("inventory_manager", () -> new APTileEntityBlock<>(TileEntityTypes.INVENTORY_MANAGER, false),
            () -> new APBlockItem(Blocks.INVENTORY_MANAGER.get(), null, null, () -> AdvancedPeripheralsConfig.enableInventoryManager));
    public static final RegistryObject<Block> REDSTONE_INTEGRATOR = register("redstone_integrator", RedstoneIntegratorBlock::new,
            () -> new APBlockItem(Blocks.REDSTONE_INTEGRATOR.get(), null, null, () -> AdvancedPeripheralsConfig.enableRedstoneIntegrator));
    public static final RegistryObject<Block> BLOCK_READER = register("block_reader", () -> new APTileEntityBlock<>(TileEntityTypes.BLOCK_READER, true),
            () -> new APBlockItem(Blocks.BLOCK_READER.get(), null, null, () -> AdvancedPeripheralsConfig.enableBlockReader));
    public static final RegistryObject<Block> GEO_SCANNER = register("geo_scanner", () -> new APTileEntityBlock<>(TileEntityTypes.GEO_SCANNER, false),
            () -> new APBlockItem(Blocks.GEO_SCANNER.get(), TurtleGeoScannerUpgrade.ID, PocketGeoScannerUpgrade.ID, () -> AdvancedPeripheralsConfig.enableGeoScanner));
    public static final RegistryObject<Block> COLONY_INTEGRATOR = register("colony_integrator", () -> new APTileEntityBlock<>(TileEntityTypes.COLONY_INTEGRATOR, false, ModList.get().isLoaded("minecolonies")),
            () -> new APBlockItem(Blocks.COLONY_INTEGRATOR.get(), null, PocketColonyIntegratorUpgrade.ID, () -> AdvancedPeripheralsConfig.enableColonyIntegrator));
    public static final RegistryObject<Block> NBT_STORAGE = register("nbt_storage", () -> new APTileEntityBlock<>(TileEntityTypes.NBT_STORAGE, true),
            () -> new APBlockItem(Blocks.NBT_STORAGE.get(), null, null, () -> AdvancedPeripheralsConfig.enableNBTStorage));

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return Registration.BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Supplier<BlockItem> blockItem) {
        RegistryObject<T> registryObject = registerNoItem(name, block);
        Registration.ITEMS.register(name, blockItem);
        return registryObject;
    }

    public static boolean never(BlockState p_235436_0_, IBlockReader p_235436_1_, BlockPos p_235436_2_) {
        return false;
    }

}