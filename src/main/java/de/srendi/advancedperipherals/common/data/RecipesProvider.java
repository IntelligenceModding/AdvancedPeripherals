package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.shared.Registry;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RecipesProvider extends RecipeProvider implements IConditionBuilder {

    private static final Block CASING = Blocks.PERIPHERAL_CASING.get();
    private static final String HAS_ITEM = "has_item";

    public RecipesProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(Blocks.AR_CONTROLLER.get())
                .define('E', Tags.Items.ENDER_PEARLS)
                .define('C', CASING)
                .define('G', Items.SMOOTH_STONE)
                .pattern("GEG")
                .pattern("ECE")
                .pattern("GEG")
                .unlockedBy(HAS_ITEM, has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(de.srendi.advancedperipherals.common.setup.Items.AR_GOGGLES.get())
                .define('E', Tags.Items.ENDER_PEARLS)
                .define('S', Tags.Items.RODS_WOODEN)
                .define('G', Tags.Items.GLASS_BLACK)
                .pattern("GSG")
                .pattern(" E ")
                .unlockedBy(HAS_ITEM, has(Items.STICK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.CHAT_BOX.get())
                .define('P', ItemTags.LOGS)
                .define('A', CASING)
                .define('G', Tags.Items.INGOTS_GOLD)
                .pattern("PPP")
                .pattern("PAP")
                .pattern("PGP")
                .unlockedBy(HAS_ITEM, has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(de.srendi.advancedperipherals.common.setup.Items.CHUNK_CONTROLLER.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('A', Items.ENDER_EYE)
                .pattern("IRI")
                .pattern("RAR")
                .pattern("IRI")
                .unlockedBy(HAS_ITEM, has(Items.RESPAWN_ANCHOR))
                .save(consumer);

        ShapedRecipeBuilder.shaped(de.srendi.advancedperipherals.common.setup.Items.COMPUTER_TOOL.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('B', Items.BLUE_TERRACOTTA)
                .pattern("I I")
                .pattern("IBI")
                .pattern(" B ")
                .unlockedBy(HAS_ITEM, has(Items.BLUE_TERRACOTTA))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.ENERGY_DETECTOR.get())
                .define('B', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('R', Items.REDSTONE_TORCH)
                .define('C', Items.COMPARATOR)
                .define('A', CASING)
                .define('G', Tags.Items.INGOTS_GOLD)
                .pattern("BRB")
                .pattern("CAC")
                .pattern("BGB")
                .unlockedBy(HAS_ITEM, has(CASING))
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
                .unlockedBy(HAS_ITEM, has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.INVENTORY_MANAGER.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('C', Tags.Items.CHESTS)
                .define('A', CASING)
                .pattern("ICI")
                .pattern("CAC")
                .pattern("ICI")
                .unlockedBy(HAS_ITEM, has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(de.srendi.advancedperipherals.common.setup.Items.MEMORY_CARD.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('W', Tags.Items.GLASS_WHITE)
                .define('O', Items.OBSERVER)
                .define('G', Tags.Items.INGOTS_GOLD)
                .pattern("IWI")
                .pattern("IOI")
                .pattern(" G ")
                .unlockedBy(HAS_ITEM, has(Items.OBSERVER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.PERIPHERAL_CASING.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('i', Items.IRON_BARS)
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .pattern("IiI")
                .pattern("iRi")
                .pattern("IiI")
                .unlockedBy(HAS_ITEM, has(Items.REDSTONE_BLOCK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.PLAYER_DETECTOR.get())
                .define('S', Items.SMOOTH_STONE)
                .define('A', CASING)
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SRS")
                .unlockedBy(HAS_ITEM, has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.REDSTONE_INTEGRATOR.get())
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('A', CASING)
                .define('C', Items.COMPARATOR)
                .pattern("RCR")
                .pattern("CAC")
                .pattern("RCR")
                .unlockedBy(HAS_ITEM, has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.BLOCK_READER.get())
                .define('O', Items.OBSERVER)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('M', Registry.ModBlocks.WIRED_MODEM_FULL.get())
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('A', CASING)
                .pattern("IRI")
                .pattern("MAO")
                .pattern("IRI")
                .unlockedBy(HAS_ITEM, has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.GEO_SCANNER.get())
                .define('O', Items.OBSERVER)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('C', CASING)
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('M', Registry.ModBlocks.WIRED_MODEM_FULL.get())
                .pattern("DMD")
                .pattern("DCD")
                .pattern("ROR")
                .unlockedBy(HAS_ITEM, has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Blocks.NBT_STORAGE.get())
                .define('C', Tags.Items.CHESTS)
                .define('A', CASING)
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('I', Tags.Items.INGOTS_IRON)
                .pattern("ICI")
                .pattern("CAC")
                .pattern("RCR")
                .unlockedBy(HAS_ITEM, has(CASING))
                .save(consumer);

        ShapedRecipeBuilder.shaped(de.srendi.advancedperipherals.common.setup.Items.WEAK_AUTOMATA_CORE.get())
                .define('A', CASING)
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('S', Items.SOUL_LANTERN)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('L', new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LONG_REGENERATION)))
                .pattern("RAR")
                .pattern("DSD")
                .pattern("RLR")
                .unlockedBy(HAS_ITEM, has(CASING))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(de.srendi.advancedperipherals.common.setup.Items.OVERPOWERED_WEAK_AUTOMATA_CORE.get())
                .requires(de.srendi.advancedperipherals.common.setup.Items.WEAK_AUTOMATA_CORE.get())
                .requires(Items.NETHER_STAR)
                .unlockedBy(HAS_ITEM, has(de.srendi.advancedperipherals.common.setup.Items.WEAK_AUTOMATA_CORE.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(de.srendi.advancedperipherals.common.setup.Items.OVERPOWERED_END_AUTOMATA_CORE.get())
                .requires(de.srendi.advancedperipherals.common.setup.Items.END_AUTOMATA_CORE.get())
                .requires(Items.NETHER_STAR)
                .unlockedBy(HAS_ITEM, has(de.srendi.advancedperipherals.common.setup.Items.END_AUTOMATA_CORE.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(de.srendi.advancedperipherals.common.setup.Items.OVERPOWERED_HUSBANDRY_AUTOMATA_CORE.get())
                .requires(de.srendi.advancedperipherals.common.setup.Items.HUSBANDRY_AUTOMATA_CORE.get())
                .requires(Items.NETHER_STAR)
                .unlockedBy(HAS_ITEM, has(de.srendi.advancedperipherals.common.setup.Items.HUSBANDRY_AUTOMATA_CORE.get()))
                .save(consumer);
    }

    public static class NBTIngredient extends net.minecraftforge.common.crafting.NBTIngredient {
        public NBTIngredient(ItemStack stack) {
            super(stack);
        }
    }
}
