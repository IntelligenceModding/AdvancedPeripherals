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
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(Blocks.AR_CONTROLLER.get())
                .key('E', Items.ENDER_PEARL)
                .key('C', CASING)
                .key('G', Items.SMOOTH_STONE)
                .patternLine("GEG")
                .patternLine("ECE")
                .patternLine("GEG")
                .addCriterion("has_item", hasItem(CASING))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(de.srendi.advancedperipherals.common.setup.Items.AR_GOGGLES.get())
                .key('E', Items.ENDER_PEARL)
                .key('S', Items.STICK)
                .key('G', Items.BLACK_STAINED_GLASS)
                .patternLine("GSG")
                .patternLine(" E ")
                .addCriterion("has_item", hasItem(Items.STICK))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(Blocks.CHAT_BOX.get())
                .key('P', ItemTags.LOGS)
                .key('A', CASING)
                .key('g', Items.GOLD_INGOT)
                .patternLine("PPP")
                .patternLine("PAP")
                .patternLine("PgP")
                .addCriterion("has_item", hasItem(CASING))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(de.srendi.advancedperipherals.common.setup.Items.CHUNK_CONTROLLER.get())
                .key('I', Items.IRON_INGOT)
                .key('R', Items.REDSTONE)
                .key('A', Items.RESPAWN_ANCHOR)
                .patternLine("IRI")
                .patternLine("RAR")
                .patternLine("IRI")
                .addCriterion("has_item", hasItem(Items.RESPAWN_ANCHOR))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(de.srendi.advancedperipherals.common.setup.Items.COMPUTER_TOOL.get())
                .key('I', Items.IRON_INGOT)
                .key('B', Items.BLUE_TERRACOTTA)
                .patternLine("I I")
                .patternLine("IBI")
                .patternLine(" B ")
                .addCriterion("has_item", hasItem(Items.BLUE_TERRACOTTA))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(Blocks.ENERGY_DETECTOR.get())
                .key('B', Items.REDSTONE_BLOCK)
                .key('R', Items.REDSTONE_TORCH)
                .key('C', Items.COMPARATOR)
                .key('A', CASING)
                .key('G', Items.GOLD_INGOT)
                .patternLine("BRB")
                .patternLine("CAC")
                .patternLine("BGB")
                .addCriterion("has_item", hasItem(CASING))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(Blocks.ENVIRONMENT_DETECTOR.get())
                .key('W', ItemTags.WOOL)
                .key('S', ItemTags.SAPLINGS)
                .key('C', Tags.Items.CROPS)
                .key('A', CASING)
                .key('L', ItemTags.LEAVES)
                .patternLine("WSW")
                .patternLine("LAL")
                .patternLine("WCW")
                .addCriterion("has_item", hasItem(CASING))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(Blocks.INVENTORY_MANAGER.get())
                .key('I', Items.IRON_INGOT)
                .key('C', Tags.Items.CHESTS)
                .key('A', CASING)
                .patternLine("ICI")
                .patternLine("CAC")
                .patternLine("ICI")
                .addCriterion("has_item", hasItem(CASING))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(Blocks.ME_BRIDGE.get())
                .key('F', Api.INSTANCE.definitions().blocks().fluixBlock().block())
                .key('I', Api.INSTANCE.definitions().parts().iface().item())
                .key('A', CASING)
                .patternLine("FIF")
                .patternLine("IAI")
                .patternLine("FIF")
                .addCriterion("has_item", hasItem(CASING))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(de.srendi.advancedperipherals.common.setup.Items.MEMORY_CARD.get())
                .key('I', Items.IRON_INGOT)
                .key('W', Items.WHITE_STAINED_GLASS)
                .key('O', Items.OBSERVER)
                .key('G', Items.GOLD_INGOT.getItem())
                .patternLine("IWI")
                .patternLine("IOI")
                .patternLine(" G ")
                .addCriterion("has_item", hasItem(Items.OBSERVER))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(Blocks.PERIPHERAL_CASING.get())
                .key('I', Items.IRON_INGOT)
                .key('i', Items.IRON_BARS)
                .key('R', Items.REDSTONE_BLOCK)
                .patternLine("IiI")
                .patternLine("iRi")
                .patternLine("IiI")
                .addCriterion("has_item", hasItem(Items.REDSTONE_BLOCK))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(Blocks.PLAYER_DETECTOR.get())
                .key('S', Items.SMOOTH_STONE)
                .key('A', CASING)
                .key('R', Items.REDSTONE_BLOCK)
                .patternLine("SSS")
                .patternLine("SAS")
                .patternLine("SRS")
                .addCriterion("has_item", hasItem(CASING))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(Blocks.REDSTONE_INTEGRATOR.get())
                .key('R', Items.REDSTONE_BLOCK)
                .key('A', CASING)
                .key('C', Items.COMPARATOR)
                .patternLine("RCR")
                .patternLine("CAC")
                .patternLine("RCR")
                .addCriterion("has_item", hasItem(CASING))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(Blocks.RS_BRIDGE.get())
                .key('Q', RSItems.QUARTZ_ENRICHED_IRON.get())
                .key('I', RSBlocks.INTERFACE.get())
                .key('A', CASING)
                .patternLine("QIQ")
                .patternLine("IAI")
                .patternLine("QIQ")
                .addCriterion("has_item", hasItem(CASING))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(Blocks.PERIPHERAL_PROXY.get())
                .key('O', Items.OBSERVER)
                .key('I', Items.IRON_INGOT)
                .key('M', Registry.ModBlocks.WIRED_MODEM_FULL.get())
                .key('R', Items.REDSTONE_BLOCK)
                .key('A', CASING)
                .patternLine("IRI")
                .patternLine("MAO")
                .patternLine("IRI")
                .addCriterion("has_item", hasItem(CASING))
                .build(consumer);
    }
}
