// TODO: check recipe correctness

package de.srendi.advancedperipherals.common.data;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.items.ModItems;
// import com.refinedmods.refinedstorage.common.misc.ProcessorItem;
import dan200.computercraft.shared.ModRegistry;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APItems;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import java.util.function.Consumer;

public class RecipesProvider extends RecipeProvider implements IConditionBuilder {

    private static final Block CASING = APBlocks.PERIPHERAL_CASING.get();
    private static final String HAS_ITEM = "has_item";

    // private static final com.refinedmods.refinedstorage.common.content.Items RS_ITEMS = com.refinedmods.refinedstorage.common.content.Items.INSTANCE;
    // private static final com.refinedmods.refinedstorage.common.content.Blocks RS_BLOCKS = com.refinedmods.refinedstorage.common.content.Blocks.INSTANCE;

    public RecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        //// ITEMS ////

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.COMPUTER_TOOL.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('B', Items.BLUE_TERRACOTTA)
            .pattern("I I")
            .pattern("IBI")
            .pattern(" B ")
            .unlockedBy(HAS_ITEM, has(Items.BLUE_TERRACOTTA))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.KEYBOARD.get())
            .define('A', CASING)
            .define('B', ItemTags.BUTTONS)
            .define('G', Tags.Items.INGOTS_GOLD)
            .pattern("BBB")
            .pattern("BGB")
            .pattern("BAB")
            .unlockedBy(HAS_ITEM, has(CASING))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.MEMORY_CARD.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('W', Tags.Items.GLASS)
            .define('O', Items.OBSERVER)
            .define('G', Tags.Items.INGOTS_GOLD)
            .pattern("IWI")
            .pattern("IOI")
            .pattern(" G ")
            .unlockedBy(HAS_ITEM, has(Items.OBSERVER))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.CHUNK_CONTROLLER.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .define('A', Items.ENDER_EYE)
            .pattern("IRI")
            .pattern("RAR")
            .pattern("IRI")
            .unlockedBy(HAS_ITEM, has(Items.RESPAWN_ANCHOR))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.WEAK_AUTOMATA_CORE.get())
                .define('A', CASING)
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('S', Items.SOUL_LANTERN)
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('L', makePotion(Potions.LONG_REGENERATION))
                .pattern("RAR")
                .pattern("DSD")
                .pattern("RLR")
                .unlockedBy(HAS_ITEM, has(CASING))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, APItems.OVERPOWERED_WEAK_AUTOMATA_CORE.get())
            .requires(APItems.WEAK_AUTOMATA_CORE.get())
            .requires(Items.NETHER_STAR)
            .unlockedBy(HAS_ITEM, has(APItems.WEAK_AUTOMATA_CORE.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, APItems.OVERPOWERED_END_AUTOMATA_CORE.get())
            .requires(APItems.END_AUTOMATA_CORE.get())
            .requires(Items.NETHER_STAR)
            .unlockedBy(HAS_ITEM, has(APItems.END_AUTOMATA_CORE.get()))
            .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, APItems.OVERPOWERED_HUSBANDRY_AUTOMATA_CORE.get())
            .requires(APItems.HUSBANDRY_AUTOMATA_CORE.get())
            .requires(Items.NETHER_STAR)
            .unlockedBy(HAS_ITEM, has(APItems.HUSBANDRY_AUTOMATA_CORE.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, APItems.SMART_GLASSES.get())
            .define('A', ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get())
            .define('G', Tags.Items.GLASS_PANES_COLORLESS)
            .define('I', Tags.Items.INGOTS_IRON)
            .pattern("G G")
            .pattern("IAI")
            .pattern("I I")
            .unlockedBy(HAS_ITEM, has(ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get()))
            .save(recipeOutput);

        SmithingTransformRecipeBuilder
            .smithing(
                Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                Ingredient.of(APItems.SMART_GLASSES.get()),
                Ingredient.of(Items.NETHERITE_INGOT),
                RecipeCategory.COMBAT,
                APItems.SMART_GLASSES_NETHERITE.get()
            )
            .unlocks(HAS_ITEM, has(Items.NETHERITE_INGOT))
            .save(recipeOutput, AdvancedPeripherals.getRL("armor/" + APItems.SMART_GLASSES_NETHERITE.getKey().location().getPath()));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, APItems.SMART_GLASSES_INTERFACE.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('G', Tags.Items.INGOTS_GOLD)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .pattern("I")
            .pattern("G")
            .pattern("R")
            .unlockedBy(HAS_ITEM, has(APItems.SMART_GLASSES.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, APItems.HOTKEY_MODULE.get())
            .define('A', CASING)
            .define('B', ItemTags.BUTTONS)
            .pattern("B")
            .pattern("A")
            .unlockedBy(HAS_ITEM, has(APItems.SMART_GLASSES.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, APItems.NIGHT_VISION_MODULE.get())
            .define('A', CASING)
            .define('N', makePotion(Potions.NIGHT_VISION))
            .pattern("N")
            .pattern("A")
            .unlockedBy(HAS_ITEM, has(APItems.SMART_GLASSES.get()))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, APItems.OVERLAY_MODULE.get())
            .define('A', CASING)
            .define('M', ModRegistry.Items.MONITOR_ADVANCED.get())
            .pattern("MAM")
            .unlockedBy(HAS_ITEM, has(APItems.SMART_GLASSES.get()))
            .save(recipeOutput);

        ConditionalRecipe.builder()
            .addCondition(modLoaded(APAddon.AE2.getModId()))
            .addRecipe(
                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.AE_DISK_CELL_1M.get())
                    .define('P', AEItems.LOGIC_PROCESSOR.asItem())
                    .define('D', ModRegistry.Items.DISK.get())
                    .define('R', Tags.Items.DUSTS_REDSTONE)
                    .pattern("RDR")
                    .pattern("DPD")
                    .pattern("RDR")
                    .unlockedBy(HAS_ITEM, has(ModRegistry.Items.DISK.get()))
                    ::save
            )
            .build(recipeOutput, RecipeBuilder.getDefaultRecipeId(APItems.AE_DISK_CELL_1M.get()));

        ConditionalRecipe.builder()
            .addCondition(modLoaded(APAddon.AE2.getModId()))
            .addRecipe(
                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.AE_DISK_CELL_4M.get())
                    .define('P', AEItems.CALCULATION_PROCESSOR.asItem())
                    .define('G', AEBlocks.QUARTZ_GLASS)
                    .define('D', APItems.AE_DISK_CELL_1M.get())
                    .define('R', Tags.Items.DUSTS_REDSTONE)
                    .pattern("RPR")
                    .pattern("DGD")
                    .pattern("RDR")
                    .unlockedBy(HAS_ITEM, has(APItems.AE_DISK_CELL_1M.get()))
                    ::save
            )
            .build(recipeOutput, RecipeBuilder.getDefaultRecipeId(APItems.AE_DISK_CELL_4M.get()));

        ConditionalRecipe.builder()
            .addCondition(modLoaded(APAddon.AE2.getModId()))
            .addRecipe(
                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.AE_DISK_CELL_16M.get())
                    .define('P', AEItems.CALCULATION_PROCESSOR.asItem())
                    .define('G', AEBlocks.QUARTZ_GLASS)
                    .define('D', APItems.AE_DISK_CELL_4M.get())
                    .define('R', Tags.Items.DUSTS_GLOWSTONE)
                    .pattern("RPR")
                    .pattern("DGD")
                    .pattern("RDR")
                    .unlockedBy(HAS_ITEM, has(APItems.AE_DISK_CELL_4M.get()))
                    ::save
            )
            .build(recipeOutput, RecipeBuilder.getDefaultRecipeId(APItems.AE_DISK_CELL_16M.get()));

        ConditionalRecipe.builder()
            .addCondition(modLoaded(APAddon.AE2.getModId()))
            .addRecipe(
                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.AE_DISK_CELL_64M.get())
                    .define('P', AEItems.CALCULATION_PROCESSOR.asItem())
                    .define('G', AEBlocks.QUARTZ_GLASS)
                    .define('D', APItems.AE_DISK_CELL_16M.get())
                    .define('R', Tags.Items.DUSTS_GLOWSTONE)
                    .pattern("RPR")
                    .pattern("DGD")
                    .pattern("RDR")
                    .unlockedBy(HAS_ITEM, has(APItems.AE_DISK_CELL_16M.get()))
                    ::save
            )
            .build(recipeOutput, RecipeBuilder.getDefaultRecipeId(APItems.AE_DISK_CELL_64M.get()));

        ConditionalRecipe.builder()
            .addCondition(modLoaded(APAddon.AE2.getModId()))
            .addRecipe(
                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APItems.AE_DISK_CELL_256M.get())
                    .define('P', AEItems.CALCULATION_PROCESSOR.asItem())
                    .define('G', AEBlocks.QUARTZ_GLASS)
                    .define('D', APItems.AE_DISK_CELL_64M.get())
                    .define('R', AEItems.SKY_DUST.asItem())
                    .pattern("RPR")
                    .pattern("DGD")
                    .pattern("RDR")
                    .unlockedBy(HAS_ITEM, has(APItems.AE_DISK_CELL_64M.get()))
                    ::save
            )
            .build(recipeOutput, RecipeBuilder.getDefaultRecipeId(APItems.AE_DISK_CELL_256M.get()));

        //// BLOCKS ////

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, CASING)
            .define('I', Tags.Items.INGOTS_IRON)
            .define('B', Items.IRON_BARS)
            .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .pattern("IBI")
            .pattern("BRB")
            .pattern("IBI")
            .unlockedBy(HAS_ITEM, has(Items.REDSTONE_BLOCK))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.CHAT_BOX.get())
            .define('A', CASING)
            .define('P', ItemTags.LOGS)
            .define('G', Tags.Items.INGOTS_GOLD)
            .pattern("PPP")
            .pattern("PAP")
            .pattern("PGP")
            .unlockedBy(HAS_ITEM, has(CASING))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.DISTANCE_DETECTOR.get())
            .define('C', CASING)
            .define('O', Items.OBSERVER)
            .define('D', Tags.Items.GEMS_DIAMOND)
            .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .define('G', Tags.Items.GLASS)
            .pattern("GDG")
            .pattern("GCG")
            .pattern("ROR")
            .unlockedBy(HAS_ITEM, has(CASING))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.ENVIRONMENT_DETECTOR.get())
            .define('A', CASING)
            .define('W', ItemTags.WOOL)
            .define('S', ItemTags.SAPLINGS)
            .define('C', Tags.Items.CROPS)
            .define('L', ItemTags.LEAVES)
            .pattern("WSW")
            .pattern("LAL")
            .pattern("WCW")
            .unlockedBy(HAS_ITEM, has(CASING))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.GEO_SCANNER.get())
            .define('C', CASING)
            .define('O', Items.OBSERVER)
            .define('D', Tags.Items.GEMS_DIAMOND)
            .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .define('M', ModRegistry.Blocks.WIRED_MODEM_FULL.get())
            .pattern("DMD")
            .pattern("DCD")
            .pattern("ROR")
            .unlockedBy(HAS_ITEM, has(CASING))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.PLAYER_DETECTOR.get())
            .define('A', CASING)
            .define('S', Items.SMOOTH_STONE)
            .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .pattern("SSS")
            .pattern("SAS")
            .pattern("SRS")
            .unlockedBy(HAS_ITEM, has(CASING))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.ENERGY_DETECTOR.get())
            .define('A', CASING)
            .define('B', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .define('R', Items.REDSTONE_TORCH)
            .define('C', Items.COMPARATOR)
            .define('G', Tags.Items.INGOTS_GOLD)
            .pattern("BRB")
            .pattern("CAC")
            .pattern("BGB")
            .unlockedBy(HAS_ITEM, has(CASING))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.FLUID_DETECTOR.get())
            .define('A', CASING)
            .define('B', Items.BUCKET)
            .define('P', Items.PISTON)
            .define('C', Items.COMPARATOR)
            .define('G', Tags.Items.INGOTS_GOLD)
            .pattern("BPB")
            .pattern("CAC")
            .pattern("BGB")
            .unlockedBy(HAS_ITEM, has(CASING))
            .save(recipeOutput);

        ConditionalRecipe.builder()
            .addCondition(modLoaded(APAddon.MEKANISM.getModId()))
            .addRecipe(
                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.GAS_DETECTOR.get())
                    .define('A', CASING)
                    .define('B', MekanismBlocks.BASIC_PRESSURIZED_TUBE)
                    .define('P', Items.PISTON)
                    .define('C', Items.COMPARATOR)
                    .define('G', Tags.Items.INGOTS_GOLD)
                    .pattern("BPB")
                    .pattern("CAC")
                    .pattern("BGB")
                    .unlockedBy(HAS_ITEM, has(CASING))
                    ::save
            )
            .build(recipeOutput, RecipeBuilder.getDefaultRecipeId(APBlocks.GAS_DETECTOR.get()));

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.BLOCK_READER.get())
            .define('A', CASING)
            .define('O', Items.OBSERVER)
            .define('I', Tags.Items.INGOTS_IRON)
            .define('M', ModRegistry.Blocks.WIRED_MODEM_FULL.get())
            .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .pattern("IRI")
            .pattern("MAO")
            .pattern("IRI")
            .unlockedBy(HAS_ITEM, has(CASING))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.NBT_STORAGE.get())
            .define('A', CASING)
            .define('C', Tags.Items.CHESTS)
            .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .define('I', Tags.Items.INGOTS_IRON)
            .pattern("ICI")
            .pattern("CAC")
            .pattern("RCR")
            .unlockedBy(HAS_ITEM, has(CASING))
            .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.INVENTORY_MANAGER.get())
            .define('A', CASING)
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', Tags.Items.CHESTS)
            .pattern("ICI")
            .pattern("CAC")
            .pattern("ICI")
            .unlockedBy(HAS_ITEM, has(CASING))
            .save(recipeOutput);

        ConditionalRecipe.builder()
            .addCondition(modLoaded(APAddon.MINECOLONIES.getModId()))
            .addRecipe(
                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.COLONY_INTEGRATOR.get())
                    .define('A', CASING)
                    .define('O', ItemTags.LOGS)
                    .define('B', ModItems.buildGoggles)
                    .define('S', com.ldtteam.structurize.items.ModItems.buildTool.get())
                    .define('R', ModBlocks.blockRack)
                    .pattern("ORO")
                    .pattern("BAS")
                    .pattern("ORO")
                    .unlockedBy(HAS_ITEM, has(CASING))
                    ::save
            )
            .build(recipeOutput, RecipeBuilder.getDefaultRecipeId(APBlocks.COLONY_INTEGRATOR.get()));

        ConditionalRecipe.builder()
            .addCondition(modLoaded(APAddon.AE2.getModId()))
            .addRecipe(
                ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.ME_BRIDGE.get())
                    .define('A', CASING)
                    .define('F', AEBlocks.FLUIX_BLOCK.asItem())
                    .define('I', AEBlocks.INTERFACE.asItem())
                    .pattern("FIF")
                    .pattern("IAI")
                    .pattern("FIF")
                    .unlockedBy(HAS_ITEM, has(CASING))
                    ::save
            )
            .build(recipeOutput, RecipeBuilder.getDefaultRecipeId(APBlocks.ME_BRIDGE.get()));

        // ConditionalRecipe.builder()
        //     .addCondition(modLoaded(APAddon.REFINEDSTORAGE.getModId()))
        //     .addRecipe(
        //         ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.RS_BRIDGE.get())
        //             .define('C', CASING)
        //             .define('P', RS_ITEMS.getProcessor(ProcessorItem.Type.ADVANCED))
        //             .define('I', RS_BLOCKS.getInterface())
        //             .define('X', com.refinedmods.refinedstorage.common.content.Tags.EXTERNAL_STORAGES)
        //             .define('E', com.refinedmods.refinedstorage.common.content.Tags.EXPORTERS)
        //             .define('R', com.refinedmods.refinedstorage.common.content.Tags.IMPORTERS)
        //             .pattern("PXP")
        //             .pattern("ECR")
        //             .pattern("PIP")
        //             .unlockedBy(HAS_ITEM, has(CASING))
        //             ::save
        //     )
        //     .build(recipeOutput, RecipeBuilder.getDefaultRecipeId(APBlocks.RS_BRIDGE.get()));

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, APBlocks.SMART_RAIL.get())
            .define('M', ModRegistry.Blocks.WIRED_MODEM_FULL.get())
            .define('D', Items.DETECTOR_RAIL)
            .define('P', Items.POWERED_RAIL)
            .define('A', Items.ACTIVATOR_RAIL)
            .pattern("D ")
            .pattern("PM")
            .pattern("A ")
            .unlockedBy(HAS_ITEM, has(ItemTags.RAILS))
            .save(recipeOutput);
    }

    private static Ingredient makePotion(Potion potionType) {
        return StrictNBTIngredient.of(PotionUtils.setPotion(Items.POTION.getDefaultInstance(), potionType));
    }
}
