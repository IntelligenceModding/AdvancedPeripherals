package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketChatBox;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketEnvironment;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketGeoScanner;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.PocketPlayerDetector;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChatBox;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleEnvironmentDetector;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleGeoScanner;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtlePlayerDetector;
import de.srendi.advancedperipherals.common.blocks.PlayerDetectorBlock;
import de.srendi.advancedperipherals.common.blocks.RedstoneIntegratorBlock;
import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.items.APBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.util.function.Supplier;

public class Blocks {

    static void register() {
    }

    public static final RegistryObject<Block> ENVIRONMENT_DETECTOR = register("environment_detector", () -> new APTileEntityBlock<>(TileEntityTypes.ENVIRONMENT_DETECTOR, false),
            () -> new APBlockItem(Blocks.ENVIRONMENT_DETECTOR.get(), TurtleEnvironmentDetector.ID, PocketEnvironment.ID, () -> AdvancedPeripheralsConfig.enableEnvironmentDetector));

    public static final RegistryObject<Block> CHAT_BOX = register("chat_box", () -> new APTileEntityBlock<>(TileEntityTypes.CHAT_BOX, false),
            () -> new APBlockItem(Blocks.CHAT_BOX.get(), TurtleChatBox.ID, PocketChatBox.ID, () -> AdvancedPeripheralsConfig.enableChatBox));

    public static final RegistryObject<Block> PLAYER_DETECTOR = register("player_detector", PlayerDetectorBlock::new,
            () -> new APBlockItem(Blocks.PLAYER_DETECTOR.get(), TurtlePlayerDetector.ID, PocketPlayerDetector.ID, () -> AdvancedPeripheralsConfig.enablePlayerDetector));

    public static final RegistryObject<Block> ENERGY_DETECTOR = register("energy_detector", () -> new APTileEntityBlock<>(TileEntityTypes.ENERGY_DETECTOR, true),
            () -> new APBlockItem(Blocks.ENERGY_DETECTOR.get(), null, null, () -> AdvancedPeripheralsConfig.enableEnergyDetector));

    public static final RegistryObject<Block> PERIPHERAL_CASING = register("peripheral_casing", BaseBlock::new,
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
            () -> new APBlockItem(Blocks.GEO_SCANNER.get(), TurtleGeoScanner.ID, PocketGeoScanner.ID, () -> AdvancedPeripheralsConfig.enableGeoScanner));

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

    public static boolean never(BlockState p_235436_0_, BlockGetter p_235436_1_, BlockPos p_235436_2_) {
        return false;
    }

}