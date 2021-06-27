package de.srendi.advancedperipherals.common.data;

import appeng.core.Api;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.RSItems;
import dan200.computercraft.shared.Registry;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

public class RecipesProvider extends RecipeProvider implements IConditionBuilder {

    private static final Block CASING = Blocks.PERIPHERAL_CASING.get();

    public RecipesProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(Blocks.AR_CONTROLLER.get())
                .define('E', Items.ENDER_PEARL)
                .define('C', CASING)
                .define('G', Items.SMOOTH_STONE)
                .pattern("GEG")
                .pattern("ECE")
                .pattern("GEG")
                .unlockedBy("has_item", has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(de.srendi.advancedperipherals.common.setup.Items.AR_GOGGLES.get())
                .define('E', Items.ENDER_PEARL)
                .define('S', Items.STICK)
                .define('G', Items.BLACK_STAINED_GLASS)
                .pattern("GSG")
                .pattern(" E ")
                .unlockedBy("has_item", has(Items.STICK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.CHAT_BOX.get())
                .define('P', ItemTags.LOGS)
                .define('A', CASING)
                .define('g', Items.GOLD_INGOT)
                .pattern("PPP")
                .pattern("PAP")
                .pattern("PgP")
                .unlockedBy("has_item", has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(de.srendi.advancedperipherals.common.setup.Items.CHUNK_CONTROLLER.get())
                .define('I', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .define('A', Items.RESPAWN_ANCHOR)
                .pattern("IRI")
                .pattern("RAR")
                .pattern("IRI")
                .unlockedBy("has_item", has(Items.RESPAWN_ANCHOR))
                .save(consumer);

        ShapedRecipeBuilder.shaped(de.srendi.advancedperipherals.common.setup.Items.COMPUTER_TOOL.get())
                .define('I', Items.IRON_INGOT)
                .define('B', Items.BLUE_TERRACOTTA)
                .pattern("I I")
                .pattern("IBI")
                .pattern(" B ")
                .unlockedBy("has_item", has(Items.BLUE_TERRACOTTA))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.ENERGY_DETECTOR.get())
                .define('B', Items.REDSTONE_BLOCK)
                .define('R', Items.REDSTONE_TORCH)
                .define('C', Items.COMPARATOR)
                .define('A', CASING)
                .define('G', Items.GOLD_INGOT)
                .pattern("BRB")
                .pattern("CAC")
                .pattern("BGB")
                .unlockedBy("has_item", has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.ENVIRONMENT_DETECTOR.get())
                .define('W', ItemTags.WOOL)
                .define('S', ItemTags.SAPLINGS)
                .define('C', Tags.Items.CROPS)
                .define('A', CASING)
                .define('L', ItemTags.LEAVES)
                .pattern("WSW")
                .pattern("LAL")
                .pattern("WCW")
                .unlockedBy("has_item", has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.INVENTORY_MANAGER.get())
                .define('I', Items.IRON_INGOT)
                .define('C', Tags.Items.CHESTS)
                .define('A', CASING)
                .pattern("ICI")
                .pattern("CAC")
                .pattern("ICI")
                .unlockedBy("has_item", has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.ME_BRIDGE.get())
                .define('F', Api.INSTANCE.definitions().blocks().fluixBlock().block())
                .define('I', Api.INSTANCE.definitions().parts().iface().item())
                .define('A', CASING)
                .pattern("FIF")
                .pattern("IAI")
                .pattern("FIF")
                .unlockedBy("has_item", has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(de.srendi.advancedperipherals.common.setup.Items.MEMORY_CARD.get())
                .define('I', Items.IRON_INGOT)
                .define('W', Items.WHITE_STAINED_GLASS)
                .define('O', Items.OBSERVER)
                .define('G', Items.GOLD_INGOT.getItem())
                .pattern("IWI")
                .pattern("IOI")
                .pattern(" G ")
                .unlockedBy("has_item", has(Items.OBSERVER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.PERIPHERAL_CASING.get())
                .define('I', Items.IRON_INGOT)
                .define('i', Items.IRON_BARS)
                .define('R', Items.REDSTONE_BLOCK)
                .pattern("IiI")
                .pattern("iRi")
                .pattern("IiI")
                .unlockedBy("has_item", has(Items.REDSTONE_BLOCK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.PLAYER_DETECTOR.get())
                .define('S', Items.SMOOTH_STONE)
                .define('A', CASING)
                .define('R', Items.REDSTONE_BLOCK)
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SRS")
                .unlockedBy("has_item", has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.REDSTONE_INTEGRATOR.get())
                .define('R', Items.REDSTONE_BLOCK)
                .define('A', CASING)
                .define('C', Items.COMPARATOR)
                .pattern("RCR")
                .pattern("CAC")
                .pattern("RCR")
                .unlockedBy("has_item", has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.RS_BRIDGE.get())
                .define('Q', RSItems.QUARTZ_ENRICHED_IRON.get())
                .define('I', RSBlocks.INTERFACE.get())
                .define('A', CASING)
                .pattern("QIQ")
                .pattern("IAI")
                .pattern("QIQ")
                .unlockedBy("has_item", has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.BLOCK_READER.get())
                .define('O', Items.OBSERVER)
                .define('I', Items.IRON_INGOT)
                .define('M', Registry.ModBlocks.WIRED_MODEM_FULL.get())
                .define('R', Items.REDSTONE_BLOCK)
                .define('A', CASING)
                .pattern("IRI")
                .pattern("MAO")
                .pattern("IRI")
                .unlockedBy("has_item", has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.GEO_SCANNER.get())
                .define('O', Items.OBSERVER)
                .define('D', Items.DIAMOND)
                .define('C', CASING)
                .define('R', Items.REDSTONE_BLOCK)
                .define('M', Registry.ModBlocks.WIRED_MODEM_FULL.get())
                .pattern("DMD")
                .pattern("DCD")
                .pattern("ROR")
                .unlockedBy("has_item", has(CASING))
                .save(consumer);
    }
}
