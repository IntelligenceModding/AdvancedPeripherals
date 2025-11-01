// TODO: check recipe correctness

package de.srendi.advancedperipherals.common.data;

import appeng.core.definitions.AEBlocks;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.items.ModItems;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.common.misc.ProcessorItem;
import dan200.computercraft.shared.ModRegistry;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APItems;
import de.srendi.advancedperipherals.common.util.RawValue;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.concurrent.CompletableFuture;

public class RecipesProvider extends RecipeProvider implements IConditionBuilder {

    private static final Block CASING = APBlocks.PERIPHERAL_CASING.get();
    private static final String HAS_ITEM = "has_item";

    public RecipesProvider(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(pGenerator, lookupProvider);
    }


    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {


        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.CHAT_BOX.get()).define('P', ItemTags.LOGS).define('A', CASING).define('G', Tags.Items.INGOTS_GOLD).pattern("PPP").pattern("PAP").pattern("PGP").unlockedBy(HAS_ITEM, has(CASING)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.CHUNK_CONTROLLER.get()).define('I', Tags.Items.INGOTS_IRON).define('R', Tags.Items.DUSTS_REDSTONE).define('A', Items.ENDER_EYE).pattern("IRI").pattern("RAR").pattern("IRI").unlockedBy(HAS_ITEM, has(Items.RESPAWN_ANCHOR)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.COMPUTER_TOOL.get()).define('I', Tags.Items.INGOTS_IRON).define('B', Items.BLUE_TERRACOTTA).pattern("I I").pattern("IBI").pattern(" B ").unlockedBy(HAS_ITEM, has(Items.BLUE_TERRACOTTA)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.ENERGY_DETECTOR.get()).define('B', Tags.Items.STORAGE_BLOCKS_REDSTONE).define('R', Items.REDSTONE_TORCH).define('C', Items.COMPARATOR).define('A', CASING).define('G', Tags.Items.INGOTS_GOLD).pattern("BRB").pattern("CAC").pattern("BGB").unlockedBy(HAS_ITEM, has(CASING)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.ENVIRONMENT_DETECTOR.get()).define('W', ItemTags.WOOL).define('S', ItemTags.SAPLINGS).define('C', Tags.Items.CROPS).define('A', CASING).define('L', ItemTags.LEAVES).pattern("WSW").pattern("LAL").pattern("WCW").unlockedBy(HAS_ITEM, has(CASING)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.INVENTORY_MANAGER.get()).define('I', Tags.Items.INGOTS_IRON).define('C', Tags.Items.CHESTS).define('A', CASING).pattern("ICI").pattern("CAC").pattern("ICI").unlockedBy(HAS_ITEM, has(CASING)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.MEMORY_CARD.get()).define('I', Tags.Items.INGOTS_IRON).define('W', Tags.Items.GLASS_BLOCKS_CHEAP).define('O', Items.OBSERVER).define('G', Tags.Items.INGOTS_GOLD).pattern("IWI").pattern("IOI").pattern(" G ").unlockedBy(HAS_ITEM, has(Items.OBSERVER)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.PERIPHERAL_CASING.get()).define('I', Tags.Items.INGOTS_IRON).define('i', Items.IRON_BARS).define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE).pattern("IiI").pattern("iRi").pattern("IiI").unlockedBy(HAS_ITEM, has(Items.REDSTONE_BLOCK)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.PLAYER_DETECTOR.get()).define('S', Items.SMOOTH_STONE).define('A', CASING).define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE).pattern("SSS").pattern("SAS").pattern("SRS").unlockedBy(HAS_ITEM, has(CASING)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.BLOCK_READER.get()).define('O', Items.OBSERVER).define('I', Tags.Items.INGOTS_IRON).define('M', ModRegistry.Blocks.WIRED_MODEM_FULL.get()).define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE).define('A', CASING).pattern("IRI").pattern("MAO").pattern("IRI").unlockedBy(HAS_ITEM, has(CASING)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.GEO_SCANNER.get()).define('O', Items.OBSERVER).define('D', Tags.Items.GEMS_DIAMOND).define('C', CASING).define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE).define('M', ModRegistry.Blocks.WIRED_MODEM_FULL.get()).pattern("DMD").pattern("DCD").pattern("ROR").unlockedBy(HAS_ITEM, has(CASING)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.NBT_STORAGE.get()).define('C', Tags.Items.CHESTS).define('A', CASING).define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE).define('I', Tags.Items.INGOTS_IRON).pattern("ICI").pattern("CAC").pattern("RCR").unlockedBy(HAS_ITEM, has(CASING)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.ME_BRIDGE.get()).define('F', AEBlocks.FLUIX_BLOCK.asItem()).define('A', CASING).define('I', AEBlocks.INTERFACE.asItem()).pattern("FIF").pattern("IAI").pattern("FIF").unlockedBy(HAS_ITEM, has(CASING)).save(recipeOutput.withConditions(new ModLoadedCondition(APAddon.AE2.getModId())));


        com.refinedmods.refinedstorage.common.content.Items rsItems = com.refinedmods.refinedstorage.common.content.Items.INSTANCE;
        com.refinedmods.refinedstorage.common.content.Blocks rsBlocks = com.refinedmods.refinedstorage.common.content.Blocks.INSTANCE;
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.RS_BRIDGE.get())
                .define('P', rsItems.getProcessor(ProcessorItem.Type.ADVANCED))
                .define('C', CASING)
                .define('I', rsBlocks.getInterface())
                .define('X', com.refinedmods.refinedstorage.common.content.Tags.EXTERNAL_STORAGES)
                .define('E', com.refinedmods.refinedstorage.common.content.Tags.EXPORTERS)
                .define('R', com.refinedmods.refinedstorage.common.content.Tags.IMPORTERS)
                .pattern("PXP")
                .pattern("ECR")
                .pattern("PIP").unlockedBy(HAS_ITEM, has(CASING)).save(recipeOutput.withConditions(new ModLoadedCondition(APAddon.REFINEDSTORAGE.getModId())));

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.COLONY_INTEGRATOR.get()).define('O', ItemTags.LOGS).define('A', CASING).define('B', ModItems.buildGoggles).define('S', com.ldtteam.structurize.items.ModItems.buildTool).define('R', ModBlocks.blockRack).pattern("ORO").pattern("BAS").pattern("ORO").unlockedBy(HAS_ITEM, has(CASING)).save(recipeOutput.withConditions(new ModLoadedCondition(APAddon.MINECOLONIES.getModId())));

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.WEAK_AUTOMATA_CORE.get())
                .define('A', CASING)
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('S', Items.SOUL_LANTERN)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('L', () -> {
                    ItemStack potion = Items.POTION.getDefaultInstance();
                    potion.update(DataComponents.POTION_CONTENTS, PotionContents.EMPTY, Potions.LONG_REGENERATION, PotionContents::withPotion);
                    return potion.getItem();
                })
                .pattern("RAR")
                .pattern("DSD")
                .pattern("RLR")
                .unlockedBy(HAS_ITEM, has(CASING))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, APItems.OVERPOWERED_WEAK_AUTOMATA_CORE.get()).requires(APItems.WEAK_AUTOMATA_CORE.get()).requires(Items.NETHER_STAR).unlockedBy(HAS_ITEM, has(APItems.WEAK_AUTOMATA_CORE.get())).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, APItems.OVERPOWERED_END_AUTOMATA_CORE.get()).requires(APItems.END_AUTOMATA_CORE.get()).requires(Items.NETHER_STAR).unlockedBy(HAS_ITEM, has(APItems.END_AUTOMATA_CORE.get())).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, APItems.OVERPOWERED_HUSBANDRY_AUTOMATA_CORE.get()).requires(APItems.HUSBANDRY_AUTOMATA_CORE.get()).requires(Items.NETHER_STAR).unlockedBy(HAS_ITEM, has(APItems.HUSBANDRY_AUTOMATA_CORE.get())).save(recipeOutput);
    }

    private void addSmithing(@NotNull Consumer<FinishedRecipe> consumer) {
        UpgradeRecipeBuilder
                .smithing(Ingredient.of(APItems.SMART_GLASSES.get()), Ingredient.of(Items.NETHERITE_INGOT), APItems.SMART_GLASSES_NETHERITE.get())
                .unlocks("has_item", has(Items.NETHERITE_INGOT))
                .save(consumer, new ResourceLocation(AdvancedPeripherals.MOD_ID, "armor/" + APItems.SMART_GLASSES_NETHERITE.getKey().location().getPath()));
    }

}
