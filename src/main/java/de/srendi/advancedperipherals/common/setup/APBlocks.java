package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.blocks.BlockReaderBlock;
import de.srendi.advancedperipherals.common.blocks.PlayerDetectorBlock;
import de.srendi.advancedperipherals.common.blocks.base.APBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.APBlockItem;
import de.srendi.advancedperipherals.lib.annotation.DefaultTooltip;
import de.srendi.advancedperipherals.lib.annotation.DefaultTranslation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class APBlocks {
    @DefaultTranslation("Peripheral Casing")
    @DefaultTooltip("&7An empty hull without the love it deserves. Used as crafting ingredient")
    public static final DeferredHolder<Block, BaseBlock> PERIPHERAL_CASING = register("peripheral_casing", BaseBlock::new, () -> new APBlockItem(APBlocks.PERIPHERAL_CASING.get(), new Item.Properties().stacksTo(16), () -> true));

    @DefaultTranslation("Chat Box")
    @DefaultTooltip("&7Interacts with the ingame chat, can read and write messages.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> CHAT_BOX = register("chat_box", () -> new APBlockEntityBlock<>(APBlockEntityTypes.CHAT_BOX), () -> new APBlockItem(APBlocks.CHAT_BOX.get(), APConfig.PERIPHERALS_CONFIG.enableChatBox));
    @DefaultTranslation("Distance Detector")
    @DefaultTooltip("&7Measure the distance to the first obstruction.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> DISTANCE_DETECTOR = register("distance_detector", () -> new APBlockEntityBlock<>(APBlockEntityTypes.DISTANCE_DETECTOR), () -> new APBlockItem(APBlocks.DISTANCE_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enableNBTStorage));
    @DefaultTranslation("Environment Detector")
    @DefaultTooltip("&7This peripheral interacts with the minecraft world.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> ENVIRONMENT_DETECTOR = register("environment_detector", () -> new APBlockEntityBlock<>(APBlockEntityTypes.ENVIRONMENT_DETECTOR), () -> new APBlockItem(APBlocks.ENVIRONMENT_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enableEnvironmentDetector));
    @DefaultTranslation("Geo Scanner")
    @DefaultTooltip("&7Scans the area around it to find some shiny ores.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> GEO_SCANNER = register("geo_scanner", () -> new APBlockEntityBlock<>(APBlockEntityTypes.GEO_SCANNER), () -> new APBlockItem(APBlocks.GEO_SCANNER.get(), APConfig.PERIPHERALS_CONFIG.enableGeoScanner));
    @DefaultTranslation("Player Detector")
    @DefaultTooltip("&7This peripheral can be used to interact with players, but don't be a stalker.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> PLAYER_DETECTOR = register("player_detector", PlayerDetectorBlock::new, () -> new APBlockItem(APBlocks.PLAYER_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enablePlayerDetector));

    @DefaultTranslation("Energy Detector")
    @DefaultTooltip("&7Can detect energy flow and acts as a resistor.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> ENERGY_DETECTOR = register("energy_detector", () -> new APBlockEntityBlock<>(APBlockEntityTypes.ENERGY_DETECTOR), () -> new APBlockItem(APBlocks.ENERGY_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enableEnergyDetector));
    @DefaultTranslation("Fluid Detector")
    @DefaultTooltip("&7Can detect fluid flow and acts as a resistor.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> FLUID_DETECTOR = register("fluid_detector", () -> new APBlockEntityBlock<>(APBlockEntityTypes.FLUID_DETECTOR), () -> new APBlockItem(APBlocks.FLUID_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enableFluidDetector));
    @DefaultTranslation("Gas Detector")
    @DefaultTooltip("&7Can detect gas flow and acts as a resistor.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> GAS_DETECTOR = register("gas_detector", () -> new APBlockEntityBlock<>(APAddon.MEKANISM.isLoaded() ? APBlockEntityTypes.GAS_DETECTOR : null), () -> new APBlockItem(APBlocks.GAS_DETECTOR.get(), APConfig.PERIPHERALS_CONFIG.enableGasDetector));

    @DefaultTranslation("Block Reader")
    @DefaultTooltip("&7Reads nbt data of blocks to interact with blocks which do not have computer support.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> BLOCK_READER = register("block_reader", BlockReaderBlock::new, () -> new APBlockItem(APBlocks.BLOCK_READER.get(), APConfig.PERIPHERALS_CONFIG.enableBlockReader));
    @DefaultTranslation("NBT Storage")
    @DefaultTooltip("&7Acts like a storage disk. Can store nbt based data.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> NBT_STORAGE = register("nbt_storage", () -> new APBlockEntityBlock<>(APBlockEntityTypes.NBT_STORAGE), () -> new APBlockItem(APBlocks.NBT_STORAGE.get(), APConfig.PERIPHERALS_CONFIG.enableNBTStorage));

    @DefaultTranslation("Inventory Manager")
    @DefaultTooltip("&7This block is able to send or receive specific items from a player inventory.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> INVENTORY_MANAGER = register("inventory_manager", () -> new APBlockEntityBlock<>(APBlockEntityTypes.INVENTORY_MANAGER), () -> new APBlockItem(APBlocks.INVENTORY_MANAGER.get(), APConfig.PERIPHERALS_CONFIG.enableInventoryManager));

    @DefaultTranslation("Colony Integrator")
    @DefaultTooltip("&7Interacts with Minecolonies to read data about your colony and citizens.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> COLONY_INTEGRATOR = register("colony_integrator", () -> new APBlockEntityBlock<>(APBlockEntityTypes.COLONY_INTEGRATOR), () -> new APBlockItem(APBlocks.COLONY_INTEGRATOR.get(), APConfig.PERIPHERALS_CONFIG.enableColonyIntegrator));
    @DefaultTranslation("ME Bridge")
    @DefaultTooltip("&7The ME Bridge interacts with Applied Energistics to manage your items.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> ME_BRIDGE = register("me_bridge", () -> new APBlockEntityBlock<>(APAddon.AE2.isLoaded() ? APBlockEntityTypes.ME_BRIDGE : null), () -> new APBlockItem(APBlocks.ME_BRIDGE.get(), APConfig.PERIPHERALS_CONFIG.enableMEBridge));
    @DefaultTranslation("RS Bridge")
    @DefaultTooltip("&7The RS Bridge interacts with Refined Storage to manage your items.")
    public static final DeferredHolder<Block, APBlockEntityBlock<?>> RS_BRIDGE = register("rs_bridge", () -> new APBlockEntityBlock<>(APAddon.REFINEDSTORAGE.isLoaded() ? APBlockEntityTypes.RS_BRIDGE : null), () -> new APBlockItem(APBlocks.RS_BRIDGE.get(), APConfig.PERIPHERALS_CONFIG.enableRSBridge));

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
