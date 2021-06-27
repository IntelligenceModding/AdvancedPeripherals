package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.blocks.*;
import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.tileentity.ChatBoxTileEntity;
import de.srendi.advancedperipherals.common.items.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class Blocks {

    //TODO-1.17 fix the turtle and pocket ids of the chat box
    public static final RegistryObject<Block> ENVIRONMENT_DETECTOR = register("environment_detector", () -> new APTileEntityBlock<>(TileEntityTypes.ENVIRONMENT_DETECTOR, false),
            ()-> new APBlockItem(Blocks.ENVIRONMENT_DETECTOR.get(), "environment_detector_turtle", "environment_pocket",
                    new TranslationTextComponent("item.advancedperipherals.tooltip.environment_detector")));
    public static final RegistryObject<Block> CHAT_BOX = register("chat_box", () -> new APTileEntityBlock<>(TileEntityTypes.CHAT_BOX, false),
            ()-> new APBlockItem(Blocks.CHAT_BOX.get(), "chat_box_turtle", "chatty_turtle",
                    new TranslationTextComponent("item.advancedperipherals.tooltip.chat_box")));
    public static final RegistryObject<Block> PLAYER_DETECTOR = register("player_detector", PlayerDetectorBlock::new,
            ()-> new APBlockItem(Blocks.PLAYER_DETECTOR.get(), "player_detector_turtle", "player_pocket",
                    new TranslationTextComponent("item.advancedperipherals.tooltip.player_detector")));
    public static final RegistryObject<Block> ME_BRIDGE = register("me_bridge", () -> new APTileEntityBlock<>(TileEntityTypes.ME_BRIDGE, false, ModList.get().isLoaded("appliedenergistics2")),
            ()-> new APBlockItem(Blocks.ME_BRIDGE.get(), null, null,
                    new TranslationTextComponent("item.advancedperipherals.tooltip.me_bridge")));
    public static final RegistryObject<Block> RS_BRIDGE = register("rs_bridge", () -> new APTileEntityBlock<>(TileEntityTypes.RS_BRIDGE, false, ModList.get().isLoaded("refinedstorage")),
            ()-> new APBlockItem(Blocks.RS_BRIDGE.get(), null, null,
                    new TranslationTextComponent("item.advancedperipherals.tooltip.rs_bridge")));
    public static final RegistryObject<Block> ENERGY_DETECTOR = register("energy_detector", () -> new APTileEntityBlock<>(TileEntityTypes.ENERGY_DETECTOR, true),
            ()-> new APBlockItem(Blocks.ENERGY_DETECTOR.get(), null, null,
                    new TranslationTextComponent("item.advancedperipherals.tooltip.energy_detector")));
    public static final RegistryObject<Block> PERIPHERAL_CASING = register("peripheral_casing", PeripheralCasingBlock::new,
            ()-> new APBlockItem(Blocks.PERIPHERAL_CASING.get(), new Item.Properties().stacksTo(16), null, null,
                    new TranslationTextComponent("item.advancedperipherals.tooltip.peripheral_casing")));
    public static final RegistryObject<Block> AR_CONTROLLER = register("ar_controller", () -> new APTileEntityBlock<>(TileEntityTypes.AR_CONTROLLER, false),
            ()-> new APBlockItem(Blocks.AR_CONTROLLER.get(), null, null,
                    new TranslationTextComponent("item.advancedperipherals.tooltip.ar_controller")));
    public static final RegistryObject<Block> INVENTORY_MANAGER = register("inventory_manager", () -> new APTileEntityBlock<>(TileEntityTypes.INVENTORY_MANAGER, false),
            ()-> new APBlockItem(Blocks.INVENTORY_MANAGER.get(), null, null,
                new TranslationTextComponent("item.advancedperipherals.tooltip.inventory_manager")));
    public static final RegistryObject<Block> REDSTONE_INTEGRATOR = register("redstone_integrator", RedstoneIntegratorBlock::new,
            ()-> new APBlockItem(Blocks.REDSTONE_INTEGRATOR.get(), null, null,
                new TranslationTextComponent("item.advancedperipherals.tooltip.redstone_integrator")));
    public static final RegistryObject<Block> BLOCK_READER = register("block_reader", () -> new APTileEntityBlock<>(TileEntityTypes.BLOCK_READER, true),
            ()-> new APBlockItem(Blocks.BLOCK_READER.get(), null, null,
                new TranslationTextComponent("item.advancedperipherals.tooltip.block_reader")));
    public static final RegistryObject<Block> GEO_SCANNER = register("geo_scanner", () -> new APTileEntityBlock<>(TileEntityTypes.GEO_SCANNER, false),
    		()-> new APBlockItem(Blocks.GEO_SCANNER.get(), "geoscanner_turtle", "geoscanner_pocket",
    			new TranslationTextComponent("item.advancedperipherals.tooltip.geo_scanner")));

    static void register() {
    }

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
