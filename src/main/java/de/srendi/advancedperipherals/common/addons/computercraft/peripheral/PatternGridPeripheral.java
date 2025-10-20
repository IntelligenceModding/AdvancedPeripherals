package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.autocrafting.PatternProviderItem;
import com.refinedmods.refinedstorage.common.autocrafting.CraftingPatternState;
import com.refinedmods.refinedstorage.common.autocrafting.PatternState;
import com.refinedmods.refinedstorage.common.autocrafting.ProcessingPatternState;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternType;
import com.refinedmods.refinedstorage.common.content.DataComponents;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.lua.ObjectLuaTable;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSApi;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PatternGridPeripheral extends BasePeripheral<TurtlePeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "pattern_grid";

    protected PatternGridPeripheral(TurtlePeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
    }

    public PatternGridPeripheral(ITurtleAccess turtle, TurtleSide side) {
        this(new TurtlePeripheralOwner(turtle, side));
    }

    @Override
    public boolean isEnabled() {
        return APAddon.REFINEDSTORAGE.isLoaded() && APConfig.PERIPHERALS_CONFIG.enablePatternGridTurtle.get();
    }

    /**
     * Returns information about a pattern, in the style of the RS Bridge ".getPattern()" method.
     *
     * @param target the pattern item to examine
     * @return a Map containing the properties as returned by the RSApi
     * @throws IllegalArgumentException if the item isn't a pattern
     * @throws IllegalStateException if the pattern is blank
     */
    public Map<String, Object> getDetailsForItem(ItemStack target) throws IllegalArgumentException, IllegalStateException {
        if (!target.is(Items.INSTANCE.getPattern())) {
            throw new IllegalArgumentException("Not a pattern");
        }

        Optional<Pattern> pattern = Optional.empty();
        if (target.getItem() instanceof PatternProviderItem patternProvider) {
            pattern = patternProvider.getPattern(target, this.getLevel());
        }

        if (pattern.isEmpty()) {
            throw new IllegalStateException("Pattern is blank");
        }

        return RSApi.parsePattern(pattern.get(), null);
    }

    @LuaFunction(mainThread = true)
    public MethodResult getDetails(Optional<Integer> slot) {
        try {
            ITurtleAccess turtle = this.getPeripheralOwner().getTurtle();
            // Note: the hack at the end of the line here converts between CC slot numbering (from 1) to Java slot numbering (from 0).
            // It's broken out like this so we can call getDetailsForItem from the builder functions later, and not have to care
            // what Lua thinks about array indexing.
            ItemStack target = turtle.getInventory().getItem(slot.orElse(turtle.getSelectedSlot() + 1) - 1);
            return MethodResult.of(getDetailsForItem(target));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return MethodResult.of(false, e.getMessage());
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Ensures that a slot contains patterns.
     *
     * @param slot the slot number to look in
     * @return how many patterns are in the slot
     * @throws IllegalStateException if the slot doesn't contain any patterns
     */
    private int verifyPatternsInSlot(int slot) throws IllegalStateException {
        Container turtleInventory = this.getPeripheralOwner().getTurtle().getInventory();
        ItemStack selected = turtleInventory.getItem(slot);
        if (selected.is(Items.INSTANCE.getPattern()) && selected.getCount() > 0) {
            return selected.getCount();
        } else {
            // TODO: do I even care to do this here
            throw new IllegalStateException("No pattern available");
        }
    }

    /**
     * Finds a free slot in which to insert the result of a pattern build.
     *
     * @param source the slot containing our source pattern
     * @return which slot to store the built pattern in
     * @throws IllegalStateException if there are no free slots in the turtle
     */
    private int getFreeSlotAfterBuild(int source) throws IllegalStateException {
        // We assume that we'll consume one of whatever is in the source slot.
        Container turtleInventory = this.getPeripheralOwner().getTurtle().getInventory();

        if (turtleInventory.getItem(source).getCount() <= 1) {
            // We're using the last of something, so feel free to take its place.
            return source;
        } else {
            // Otherwise... are there any free slots?
            for (int i = 0; i < turtleInventory.getContainerSize(); i++) {
                if (turtleInventory.getItem(i).getCount() == 0) {
                    return i;
                }
            }
            // Turtle's full, keep movin'
            throw new IllegalStateException("No room in destination");
        }
    }

    /**
     * Attempts to match a String resource name to an ItemResource or FluidResource.
     *
     * @param name the resource name
     * @return a ResourceKey representing the resource
     * @throws IllegalArgumentException if the name doesn't match a resource
     */
    private ResourceKey parseResourceName(String name) throws IllegalArgumentException {
        ResourceLocation location = ResourceLocation.parse(name);
        // Try as item first
        Item item = BuiltInRegistries.ITEM.get(location);
        // The parser seems to be greedy... sometimes it'll map an invalid Item to air. Make sure it behaves.
        if (!item.equals(net.minecraft.world.item.Items.AIR)) {
            return new ItemResource(item);
        }
        // Try as fluid
        Fluid fluid = BuiltInRegistries.FLUID.get(location);
        if (!fluid.equals(Fluids.EMPTY)) {
            return new FluidResource(fluid);
        }
        throw new IllegalArgumentException("Couldn't find item or fluid: " + name);
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Build a crafting pattern from a table of slots.
    @LuaFunction(mainThread = true)
    public MethodResult buildCrafting(Map<?, ?> recipeInput, Optional<Boolean> fuzzy) {
        ITurtleAccess turtle = this.getPeripheralOwner().getTurtle();

        try {
            verifyPatternsInSlot(turtle.getSelectedSlot());
            int destinationSlot = getFreeSlotAfterBuild(turtle.getSelectedSlot());

            ItemStack patternStack = new ItemStack(Items.INSTANCE.getPattern());
            PatternState patternState = new PatternState(UUID.randomUUID(), PatternType.CRAFTING);
            patternStack.set(DataComponents.INSTANCE.getPatternState(), patternState);

            List<ItemStack> ingredients = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));

            for (Object o : recipeInput.keySet()) {
                // Note: I'm assuming that Lua returns all numbers as doubles.
                // Only care about slots 1 through 9 in the input; we'll just ignore everything else.
                if (o instanceof Double) {
                    int slot = ((Double) o).intValue() - 1;
                    if (slot >= 0 && slot < 9) {
                        if (recipeInput.get(o) instanceof String) {
                            ingredients.set(slot, ((ItemResource) parseResourceName((String) recipeInput.get(o))).toItemStack());
                        } else {
                            return MethodResult.of(false, "Couldn't parse item in slot " + slot);
                        }
                    }
                }
            }

            CraftingInput craftingInput = CraftingInput.of(3, 3, ingredients);
            CraftingInput.Positioned positioned = new CraftingInput.Positioned(craftingInput, 0, 0);
            CraftingPatternState craftingState = new CraftingPatternState(fuzzy.orElse(false), positioned);
            patternStack.set(DataComponents.INSTANCE.getCraftingPatternState(), craftingState);

            // Kismet: a bad recipe will be caught automatically, since it'll stay a blank pattern
            Map<String, Object> result;
            try {
                result = getDetailsForItem(patternStack);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Bad recipe");
            }

            turtle.getInventory().removeItem(turtle.getSelectedSlot(), 1);
            turtle.getInventory().setItem(destinationSlot, patternStack);

            return MethodResult.of(true, result);
        } catch (IllegalStateException | IllegalArgumentException e) {
            // I like exceptions but the other peripherals in this mod don't use them. So just return errors as strings.
            return MethodResult.of(false, e.getMessage());
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    // Build a processing pattern from a list of ingredients.
    @LuaFunction(mainThread = true)
    public MethodResult buildProcessing(Map<?, ?> recipeArg) {
        ITurtleAccess turtle = this.getPeripheralOwner().getTurtle();
        LuaTable<?, ?> recipeTable = new ObjectLuaTable(recipeArg);

        try {
            verifyPatternsInSlot(turtle.getSelectedSlot());
            int destinationSlot = getFreeSlotAfterBuild(turtle.getSelectedSlot());

            ItemStack patternStack = new ItemStack(Items.INSTANCE.getPattern());
            PatternState patternState = new PatternState(UUID.randomUUID(), PatternType.PROCESSING);
            patternStack.set(DataComponents.INSTANCE.getPatternState(), patternState);

            List<Optional<ProcessingPatternState.ProcessingIngredient>> ingredients = new ArrayList<>();
            List<Optional<ResourceAmount>> results = new ArrayList<>();

            /* Attempt to parse our recipe. We should be passed in a table that looks like this:
             *
             * {
             *     inputs = {
             *         [1] = { name = "<item>", count = <int> },
             *         [2] = { name = "<item>", alts = { [1] = "<tag>", ... }, count = <int> },
             *         ...
             *     },
             *     outputs = {
             *         [1] = { name = "<item>", count = <int> },
             *         ...
             *     }
             * }
             *
             */

            // Lua sends in all numbers as doubles. We don't really care about the index.
            LuaTable<?, ?> inputsTable = new ObjectLuaTable(recipeTable.getTable("inputs"));

            for (LuaTable<?, ?> thisInput : inputsTable.values().stream().map(Map.class::cast).map(ObjectLuaTable::new).toList()) {
                int thisCount = thisInput.getInt("count");
                ResourceKey thisItem = parseResourceName(thisInput.getString("name"));

                List<ResourceLocation> theseAlts = new ArrayList<>();

                if (thisInput.containsKey("alts")) {
                    theseAlts = thisInput.getTable("alts").values().stream()
                        .map(String.class::cast).map(ResourceLocation::parse).toList();
                }

                ingredients.add(Optional.of(
                    new ProcessingPatternState.ProcessingIngredient(new ResourceAmount(thisItem, thisCount), theseAlts)));
            }

            LuaTable<?, ?> outputsTable = new ObjectLuaTable(recipeTable.getTable("outputs"));

            for (LuaTable<?, ?> thisOutput : outputsTable.values().stream().map(Map.class::cast).map(ObjectLuaTable::new).toList()) {
                int thisCount = thisOutput.getInt("count");
                ResourceKey thisItem = parseResourceName(thisOutput.getString("name"));

                results.add(Optional.of(new ResourceAmount(thisItem, thisCount)));
            }

            ProcessingPatternState processingState = new ProcessingPatternState(ingredients, results);
            patternStack.set(DataComponents.INSTANCE.getProcessingPatternState(), processingState);

            Map<String, Object> result;
            try {
                result = getDetailsForItem(patternStack);
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Bad recipe");
            }

            turtle.getInventory().removeItem(turtle.getSelectedSlot(), 1);
            turtle.getInventory().setItem(destinationSlot, patternStack);

            return MethodResult.of(true, result);
        } catch (IllegalStateException | IllegalArgumentException | LuaException e) {
            // I like exceptions but the other peripherals in this mod don't use them. So just return errors as strings.
            return MethodResult.of(false, e.getMessage());
        }
    }
}
