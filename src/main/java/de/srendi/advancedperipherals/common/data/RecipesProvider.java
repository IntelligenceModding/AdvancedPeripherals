package de.srendi.advancedperipherals.common.data;

import appeng.core.definitions.AEBlocks;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.RSItems;
import dan200.computercraft.shared.ModRegistry;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.util.RawValue;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class RecipesProvider extends RecipeProvider implements IConditionBuilder {

    private static final Block CASING = Blocks.PERIPHERAL_CASING.get();
    private static final String HAS_ITEM = "has_item";

    public RecipesProvider(PackOutput pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.AR_CONTROLLER.get()).define('E', Tags.Items.ENDER_PEARLS).define('C', CASING).define('G', Items.SMOOTH_STONE).pattern("GEG").pattern("ECE").pattern("GEG").unlockedBy(HAS_ITEM, has(CASING)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, de.srendi.advancedperipherals.common.setup.Items.AR_GOGGLES.get()).define('E', Tags.Items.ENDER_PEARLS).define('S', Tags.Items.RODS_WOODEN).define('G', Tags.Items.GLASS_BLACK).pattern("GSG").pattern(" E ").unlockedBy(HAS_ITEM, has(Items.STICK)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.CHAT_BOX.get()).define('P', ItemTags.LOGS).define('A', CASING).define('G', Tags.Items.INGOTS_GOLD).pattern("PPP").pattern("PAP").pattern("PGP").unlockedBy(HAS_ITEM, has(CASING)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, de.srendi.advancedperipherals.common.setup.Items.CHUNK_CONTROLLER.get()).define('I', Tags.Items.INGOTS_IRON).define('R', Tags.Items.DUSTS_REDSTONE).define('A', Items.ENDER_EYE).pattern("IRI").pattern("RAR").pattern("IRI").unlockedBy(HAS_ITEM, has(Items.RESPAWN_ANCHOR)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, de.srendi.advancedperipherals.common.setup.Items.COMPUTER_TOOL.get()).define('I', Tags.Items.INGOTS_IRON).define('B', Items.BLUE_TERRACOTTA).pattern("I I").pattern("IBI").pattern(" B ").unlockedBy(HAS_ITEM, has(Items.BLUE_TERRACOTTA)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.ENERGY_DETECTOR.get()).define('B', Tags.Items.STORAGE_BLOCKS_REDSTONE).define('R', Items.REDSTONE_TORCH).define('C', Items.COMPARATOR).define('A', CASING).define('G', Tags.Items.INGOTS_GOLD).pattern("BRB").pattern("CAC").pattern("BGB").unlockedBy(HAS_ITEM, has(CASING)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.ENVIRONMENT_DETECTOR.get()).define('W', ItemTags.WOOL).define('S', ItemTags.SAPLINGS).define('C', Tags.Items.CROPS).define('A', CASING).define('L', ItemTags.LEAVES).pattern("WSW").pattern("LAL").pattern("WCW").unlockedBy(HAS_ITEM, has(CASING)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.INVENTORY_MANAGER.get()).define('I', Tags.Items.INGOTS_IRON).define('C', Tags.Items.CHESTS).define('A', CASING).pattern("ICI").pattern("CAC").pattern("ICI").unlockedBy(HAS_ITEM, has(CASING)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, de.srendi.advancedperipherals.common.setup.Items.MEMORY_CARD.get()).define('I', Tags.Items.INGOTS_IRON).define('W', Tags.Items.GLASS_WHITE).define('O', Items.OBSERVER).define('G', Tags.Items.INGOTS_GOLD).pattern("IWI").pattern("IOI").pattern(" G ").unlockedBy(HAS_ITEM, has(Items.OBSERVER)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.PERIPHERAL_CASING.get()).define('I', Tags.Items.INGOTS_IRON).define('i', Items.IRON_BARS).define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE).pattern("IiI").pattern("iRi").pattern("IiI").unlockedBy(HAS_ITEM, has(Items.REDSTONE_BLOCK)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.PLAYER_DETECTOR.get()).define('S', Items.SMOOTH_STONE).define('A', CASING).define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE).pattern("SSS").pattern("SAS").pattern("SRS").unlockedBy(HAS_ITEM, has(CASING)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.REDSTONE_INTEGRATOR.get()).define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE).define('A', CASING).define('C', Items.COMPARATOR).pattern("RCR").pattern("CAC").pattern("RCR").unlockedBy(HAS_ITEM, has(CASING)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.BLOCK_READER.get()).define('O', Items.OBSERVER).define('I', Tags.Items.INGOTS_IRON).define('M', ModRegistry.Blocks.WIRED_MODEM_FULL.get()).define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE).define('A', CASING).pattern("IRI").pattern("MAO").pattern("IRI").unlockedBy(HAS_ITEM, has(CASING)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.GEO_SCANNER.get()).define('O', Items.OBSERVER).define('D', Tags.Items.GEMS_DIAMOND).define('C', CASING).define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE).define('M', ModRegistry.Blocks.WIRED_MODEM_FULL.get()).pattern("DMD").pattern("DCD").pattern("ROR").unlockedBy(HAS_ITEM, has(CASING)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.NBT_STORAGE.get()).define('C', Tags.Items.CHESTS).define('A', CASING).define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE).define('I', Tags.Items.INGOTS_IRON).pattern("ICI").pattern("CAC").pattern("RCR").unlockedBy(HAS_ITEM, has(CASING)).save(consumer);

        ConditionalRecipe.builder().addCondition(modLoaded("minecolonies")).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.COLONY_INTEGRATOR.get()).define('O', ItemTags.LOGS).define('A', CASING).define('R', Ingredient.fromValues(Stream.of(new RawValue(new ResourceLocation("minecolonies", "blockminecoloniesrack"))))).pattern("ORO").pattern(" A ").pattern("ORO").unlockedBy(HAS_ITEM, has(CASING))::save).build(consumer, new ResourceLocation(AdvancedPeripherals.MOD_ID, "colony_integrator"));

        ConditionalRecipe.builder().addCondition(modLoaded("ae2")).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.ME_BRIDGE.get()).define('F', AEBlocks.FLUIX_BLOCK.asItem()).define('A', CASING).define('I', AEBlocks.INTERFACE.asItem()).pattern("FIF").pattern("IAI").pattern("FIF").unlockedBy(HAS_ITEM, has(CASING))::save).build(consumer, new ResourceLocation(AdvancedPeripherals.MOD_ID, "me_bridge"));

        ConditionalRecipe.builder().addCondition(modLoaded("refinedstorage")).addRecipe(ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.RS_BRIDGE.get()).define('Q', RSItems.QUARTZ_ENRICHED_IRON.get()).define('A', CASING).define('I', RSBlocks.INTERFACE.get()).pattern("QIQ").pattern("IAI").pattern("QIQ").unlockedBy(HAS_ITEM, has(CASING))::save).build(consumer, new ResourceLocation(AdvancedPeripherals.MOD_ID, "rs_bridge"));

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, de.srendi.advancedperipherals.common.setup.Items.WEAK_AUTOMATA_CORE.get()).define('A', CASING).define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE).define('S', Items.SOUL_LANTERN).define('D', Tags.Items.GEMS_DIAMOND).define('L', StrictNBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.LONG_REGENERATION))).pattern("RAR").pattern("DSD").pattern("RLR").unlockedBy(HAS_ITEM, has(CASING)).save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, de.srendi.advancedperipherals.common.setup.Items.OVERPOWERED_WEAK_AUTOMATA_CORE.get()).requires(de.srendi.advancedperipherals.common.setup.Items.WEAK_AUTOMATA_CORE.get()).requires(Items.NETHER_STAR).unlockedBy(HAS_ITEM, has(de.srendi.advancedperipherals.common.setup.Items.WEAK_AUTOMATA_CORE.get())).save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, de.srendi.advancedperipherals.common.setup.Items.OVERPOWERED_END_AUTOMATA_CORE.get()).requires(de.srendi.advancedperipherals.common.setup.Items.END_AUTOMATA_CORE.get()).requires(Items.NETHER_STAR).unlockedBy(HAS_ITEM, has(de.srendi.advancedperipherals.common.setup.Items.END_AUTOMATA_CORE.get())).save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, de.srendi.advancedperipherals.common.setup.Items.OVERPOWERED_HUSBANDRY_AUTOMATA_CORE.get()).requires(de.srendi.advancedperipherals.common.setup.Items.HUSBANDRY_AUTOMATA_CORE.get()).requires(Items.NETHER_STAR).unlockedBy(HAS_ITEM, has(de.srendi.advancedperipherals.common.setup.Items.HUSBANDRY_AUTOMATA_CORE.get())).save(consumer);
    }


}
