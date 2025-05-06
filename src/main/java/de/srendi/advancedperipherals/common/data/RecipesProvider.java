package de.srendi.advancedperipherals.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RecipesProvider extends RecipeProvider implements IConditionBuilder {

    private static final String HAS_ITEM = "has_item";

    public RecipesProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(de.srendi.advancedperipherals.common.setup.Items.CHUNK_CONTROLLER.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('A', Items.ENDER_EYE)
                .pattern("IRI")
                .pattern("RAR")
                .pattern("IRI")
                .unlockedBy(HAS_ITEM, has(Items.RESPAWN_ANCHOR))
                .save(consumer);
    }

}
