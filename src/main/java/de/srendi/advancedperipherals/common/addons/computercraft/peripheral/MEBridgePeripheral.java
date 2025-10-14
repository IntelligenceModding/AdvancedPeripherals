package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.parts.encoding.PatternEncodingTerminalPart;
import dan200.computercraft.api.lua.*;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.*;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.MEBridgeEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.ListUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.*;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MEBridgePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<MEBridgeEntity>> implements IStorageSystemPeripheral {

    public static final String PERIPHERAL_TYPE = "me_bridge";

    private final MEBridgeEntity bridge;
    private IGridNode node;
    private final RecipeManager recipeManager;
    Item blankPattern = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("ae2", "blank_pattern"));
    public MEBridgePeripheral(MEBridgeEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
        this.bridge = tileEntity;
        this.node = tileEntity.getActionableNode();
        recipeManager = bridge.getLevel().getRecipeManager();
    }

    public void setNode(IManagedGridNode node) {
        this.node = node.getNode();
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableMEBridge.get();
    }

    private ICraftingService getCraftingService() {
        return node.getGrid().getCraftingService();
    }

    public MEBridgeEntity getBridge() {
        return bridge;
    }

    /**
     * exports an item out of the system to a valid inventory
     *
     * @param arguments       the arguments given by the computer
     * @param targetInventory the give inventory
     * @return the exportable amount or null with a string if something went wrong
     */
    protected MethodResult exportToChest(@NotNull IArguments arguments, IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AEApi.getMonitor(node);
        MEItemHandler itemHandler = new MEItemHandler(monitor, bridge);
        Pair<ItemFilter, String> filter = ItemFilter.parse(new ObjectLuaTable(arguments.getTable(0)));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveItem(itemHandler, targetInventory, filter.getLeft()), null);
    }

    /**
     * exports a fluid out of the system to a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param targetTank the give tank
     * @return the exportable amount or null with a string if something went wrong
     */
    protected MethodResult exportToTank(@NotNull IArguments arguments, IFluidHandler targetTank) throws LuaException {
        MEStorage monitor = AEApi.getMonitor(node);
        MEFluidHandler fluidHandler = new MEFluidHandler(monitor, bridge);
        Pair<FluidFilter, String> filter = FluidFilter.parse(new ObjectLuaTable(arguments.getTable(0)));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(FluidUtil.moveFluid(fluidHandler, targetTank, filter.getLeft()), null);
    }


    /**
     * imports an item to the system from a valid inventory
     *
     * @param arguments       the arguments given by the computer
     * @param targetInventory the give inventory
     * @return the imported amount or null with a string if something went wrong
     */
    protected MethodResult importToME(@NotNull IArguments arguments, IItemHandler targetInventory) throws LuaException {
        MEStorage monitor = AEApi.getMonitor(node);
        MEItemHandler itemHandler = new MEItemHandler(monitor, bridge);
        Pair<ItemFilter, String> filter = ItemFilter.parse(new ObjectLuaTable(arguments.getTable(0)));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(InventoryUtil.moveItem(targetInventory, itemHandler, filter.getLeft()), null);
    }

    /**
     * imports a fluid to the system from a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param targetTank the give tank
     * @return the imported amount or null with a string if something went wrong
     */
    protected MethodResult importToME(@NotNull IArguments arguments, IFluidHandler targetTank) throws LuaException {
        MEStorage monitor = AEApi.getMonitor(node);
        MEFluidHandler fluidHandler = new MEFluidHandler(monitor, bridge);
        Pair<FluidFilter, String> filter = FluidFilter.parse(new ObjectLuaTable(arguments.getTable(0)));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(FluidUtil.moveFluid(targetTank, fluidHandler, filter.getLeft()), null);
    }
    protected MethodResult removeBlankPatternsAndInsertCreatedPatterns(List<ItemStack> patterns) {
        if (patterns.isEmpty()) {
            return MethodResult.of(StatusConstants.NOT_FOUND.withInfo("No matching recipe for given output"));
        }
        MEStorage monitor = AEApi.getMonitor(node);
        MEItemHandler itemHandler = new MEItemHandler(monitor, bridge);
        ItemFilter filter = ItemFilter.fromStack(new ItemStack(blankPattern));
        ItemStack removedBlankPattern = itemHandler.extractItem(filter, patterns.size(), false);
        if (removedBlankPattern.getCount() < patterns.size()) {
            itemHandler.insertItem(1, removedBlankPattern, false);
            return MethodResult.of(StatusConstants.MISSING_BLANK_PATTERN
                                           .withInfo("Missing " + (patterns.size() - removedBlankPattern.getCount())
                                                     + " required blank patterns in the system"));
        }
        List<ItemStack> notInsertedPatterns = patterns.stream()
                                                      .map(pattern -> itemHandler.insertItem(1, pattern, false))
                                                      .filter(itemStack -> !itemStack.isEmpty()).toList();
        Containers.dropContents(
                bridge.getLevel(), bridge.getBlockPos().above(),
                new SimpleContainer(notInsertedPatterns.toArray(ItemStack[]::new)));
        return MethodResult.of(StatusConstants.PATTERN_CREATED
                                       .withInfo("Inserted " + (patterns.size() - notInsertedPatterns.size()) + " patterns, "
                                                 + notInsertedPatterns.size() + " didn't fit in the system"));
    }
    protected ItemStack createCraftingPatternForRecipe(RecipeHolder<CraftingRecipe> recipe, boolean allowSubstitutes,
                                                       boolean allowFluidSubstitutes) {
        CraftingRecipe craftingRecipe = recipe.value();
        int width = 3;
        int height = 3;
        if (recipe.value() instanceof ShapedRecipe shaped) {
            width = shaped.getWidth();
            height = shaped.getHeight();
        }
        List<ItemStack> inputs = new ArrayList<>(9);
        for (int i = 0; i < craftingRecipe.getIngredients().size(); i++) {
            Ingredient ing = craftingRecipe.getIngredients().get(i);
            inputs.add(ing.isEmpty() ? ItemStack.EMPTY : ing.getItems()[0]);
            // if next row is new row add missing empty slots to row
            if ((i + 1) % width == 0) {
                for (int j = width; j < 3; j++) {
                    inputs.add(ItemStack.EMPTY);
                }
            }
        }
        for (int j = height; j < 3; j++) {
            inputs.addAll(List.of(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));
        }
        ItemStack[] inputsArray = inputs.toArray(ItemStack[]::new);
        return PatternDetailsHelper.encodeCraftingPattern(recipe,
                                                          inputsArray,
                                                          craftingRecipe.getResultItem(
                                                                  bridge.getLevel().registryAccess()),
                                                          allowSubstitutes, allowFluidSubstitutes);
    }
    protected <T extends AbstractCookingRecipe> List<ItemStack> createCookingPatternsForRecipe(RecipeHolder<T> recipe) {
        T cookingRecipe = recipe.value();
        ItemStack resultItem = cookingRecipe.getResultItem(bridge.getLevel().registryAccess());
        GenericStack result = new GenericStack(AEItemKey.of(resultItem), resultItem.getCount());
        List<List<GenericStack>> inputs = cookingRecipe
                .getIngredients()
                .stream()
                .map(Ingredient::getItems)
                .map(stackList -> Arrays.stream(stackList)
                                        .map(stack -> new GenericStack(AEItemKey.of(stack), stack.getCount())).toList())
                .toList();
        List<ItemStack> patterns = new ArrayList<>();
        for (List<GenericStack> combinations : ListUtil.cartesianProduct(inputs)) {
            patterns.add(PatternDetailsHelper.encodeProcessingPattern(combinations, List.of(result)));
        }
        return patterns;
    }
    protected List<ItemStack> createSmithingPatternsForRecipe(RecipeHolder<SmithingRecipe> recipe, boolean allowSubstitutes) {
        SmithingRecipe smithingRecipe = recipe.value();
        ItemStack resultItem = smithingRecipe.getResultItem(bridge.getLevel().registryAccess());
        AEItemKey result = AEItemKey.of(resultItem);
        List<ItemStack> patterns = new ArrayList<>();
        Class<? extends SmithingRecipe> clazz = smithingRecipe.getClass();
        Field templateField;
        Field baseField;
        Field additionField;
        try {
            templateField = ObfuscationReflectionHelper.findField(clazz, "template");
            baseField = ObfuscationReflectionHelper.findField(clazz, "base");
            additionField = ObfuscationReflectionHelper.findField(clazz, "addition");
            List<AEItemKey> template =
                    Arrays.stream(((Ingredient) templateField.get(smithingRecipe)).getItems()).map(AEItemKey::of).toList();
            List<AEItemKey> base = Arrays.stream(((Ingredient) baseField.get(smithingRecipe)).getItems()).map(AEItemKey::of).toList();
            List<AEItemKey> addition = Arrays.stream(((Ingredient) additionField.get(smithingRecipe)).getItems()).map(AEItemKey::of)
                                             .toList();
            for (List<AEItemKey> combination : ListUtil.cartesianProduct(List.of(template, base, addition))) {
                patterns.add(PatternDetailsHelper.encodeSmithingTablePattern(recipe, combination.get(0), combination.get(1),
                                                                             combination.get(2), result, allowSubstitutes));
            }
            return patterns;
        } catch (Exception e) {
            return patterns;
        }
    }
    protected ItemStack createStonecuttingPatternForRecipe(RecipeHolder<StonecutterRecipe> recipe,
                                                           boolean allowSubstitutes) {
        StonecutterRecipe smithingRecipe = recipe.value();
        ItemStack resultItem = smithingRecipe.getResultItem(bridge.getLevel().registryAccess());
        AEItemKey result = AEItemKey.of(resultItem);
        AEItemKey input = AEItemKey.of(smithingRecipe.getIngredients().get(0).getItems()[0]);
        return PatternDetailsHelper.encodeStonecuttingPattern(recipe, input, result, allowSubstitutes);
    }
    protected List<GenericStack> createGenericStacksFromLuaTable(Map<?, ?> table) {
        List<GenericStack> result = new ArrayList<>();
        for (Map<?, ?> subTable : table.values().stream().map(Map.class::cast).toList()) {
            ResourceLocation resourceLocation = ResourceLocation.parse(subTable.get(1.0).toString());
            Item item = BuiltInRegistries.ITEM.get(resourceLocation);
            int amount = subTable.size() > 1 ? ((Double) subTable.get(2.0)).intValue() : 1;
            GenericStack stack = GenericStack.fromItemStack(new ItemStack(item, amount));
            result.add(stack);
        }
        return result;
    }
    private boolean filterRecipes(RecipeHolder<?> recipe, String keyRegex) {
        return BuiltInRegistries.ITEM.getKey(recipe.value().getResultItem(bridge.getLevel().registryAccess()).getItem())
                                     .toString().matches(keyRegex);
    }
    private MethodResult notConnected(@Nullable Object defaultValue) {
        return MethodResult.of(defaultValue, StatusConstants.NOT_CONNECTED.toString());
    }

    private boolean isAvailable() {
        return node.hasGridBooted();
    }
    private MethodResult patternEncodingInactive() {
        if (!isAvailable())
            return notConnected(null);
        if (!APConfig.PERIPHERALS_CONFIG.enableMEBridgePatternCreator.get())
            return MethodResult
                    .of(StatusConstants.PATTERN_ENCODING_DISABLED.withInfo("Pattern Encoding is disabled in config"));
        if (node.getGrid().getActiveMachines(PatternEncodingTerminalPart.class).isEmpty())
            return MethodResult.of(StatusConstants.NOT_CONNECTED.withInfo("Pattern Encoder not connected"));
        return null;
    }
    @Override
    @LuaFunction(mainThread = true)
    public final boolean isConnected() {
        return isAvailable();
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult isOnline() {
        return MethodResult.of(node.isOnline());
    }
    @LuaFunction(mainThread = true)
    public final MethodResult createCraftingPattern(IArguments arguments) throws LuaException {
        MethodResult patternEncodingInactive = patternEncodingInactive();
        if (patternEncodingInactive != null)
            return patternEncodingInactive;
        String recipeOutput = arguments.getString(0);
        boolean allowSubstitutes = arguments.optBoolean(1).orElse(true);
        boolean allowFluidSubstitutes = arguments.optBoolean(2).orElse(true);
        List<ItemStack> patterns = recipeManager.getAllRecipesFor(RecipeType.CRAFTING)
                                                .stream()
                                                .filter(r -> filterRecipes(r, recipeOutput))
                                                .map(r -> createCraftingPatternForRecipe(r, allowSubstitutes, allowFluidSubstitutes))
                                                .toList();
        return removeBlankPatternsAndInsertCreatedPatterns(patterns);
    }
    @LuaFunction(mainThread = true)
    public final MethodResult createSmeltingPattern(IArguments arguments) throws LuaException {
        MethodResult patternEncodingInactive = patternEncodingInactive();
        if (patternEncodingInactive != null)
            return patternEncodingInactive;
        String recipeOutput = arguments.getString(0);
        List<ItemStack> patterns = recipeManager.getAllRecipesFor(RecipeType.SMELTING)
                                                .stream()
                                                .filter(r -> filterRecipes(r, recipeOutput))
                                                .map(this::createCookingPatternsForRecipe)
                                                .flatMap(List::stream)
                                                .toList();
        return removeBlankPatternsAndInsertCreatedPatterns(patterns);
    }
    @LuaFunction(mainThread = true)
    public final MethodResult createBlastingPattern(IArguments arguments) throws LuaException {
        MethodResult patternEncodingInactive = patternEncodingInactive();
        if (patternEncodingInactive != null)
            return patternEncodingInactive;
        String recipeOutput = arguments.getString(0);
        List<ItemStack> patterns = recipeManager.getAllRecipesFor(RecipeType.BLASTING)
                                                .stream()
                                                .filter(r -> filterRecipes(r, recipeOutput))
                                                .map(this::createCookingPatternsForRecipe)
                                                .flatMap(List::stream)
                                                .toList();
        return removeBlankPatternsAndInsertCreatedPatterns(patterns);
    }
    @LuaFunction(mainThread = true)
    public final MethodResult createSmokingPattern(IArguments arguments) throws LuaException {
        MethodResult patternEncodingInactive = patternEncodingInactive();
        if (patternEncodingInactive != null)
            return patternEncodingInactive;
        String recipeOutput = arguments.getString(0);
        List<ItemStack> patterns = recipeManager.getAllRecipesFor(RecipeType.SMOKING)
                                                .stream()
                                                .filter(r -> filterRecipes(r, recipeOutput))
                                                .map(this::createCookingPatternsForRecipe)
                                                .flatMap(List::stream)
                                                .toList();
        return removeBlankPatternsAndInsertCreatedPatterns(patterns);
    }
    @LuaFunction(mainThread = true)
    public final MethodResult createSmithingPattern(IArguments arguments) throws LuaException {
        MethodResult patternEncodingInactive = patternEncodingInactive();
        if (patternEncodingInactive != null)
            return patternEncodingInactive;
        String recipeOutput = arguments.getString(0);
        boolean allowSubstitutes = arguments.optBoolean(1).orElse(true);
        List<ItemStack> patterns = recipeManager.getAllRecipesFor(RecipeType.SMITHING)
                                                .stream()
                                                .filter(r -> filterRecipes(r, recipeOutput))
                                                .filter(r -> r.value() instanceof SmithingTransformRecipe)
                                                .flatMap(r -> createSmithingPatternsForRecipe(r,
                                                                                              allowSubstitutes).stream())
                                                .toList();
        return removeBlankPatternsAndInsertCreatedPatterns(patterns);
    }
    @LuaFunction(mainThread = true)
    public final MethodResult createStonecuttingPattern(IArguments arguments) throws LuaException {
        MethodResult patternEncodingInactive = patternEncodingInactive();
        if (patternEncodingInactive != null)
            return patternEncodingInactive;
        String recipeOutput = arguments.getString(0);
        boolean allowSubstitutes = arguments.optBoolean(1).orElse(true);
        List<ItemStack> patterns = recipeManager.getAllRecipesFor(RecipeType.STONECUTTING)
                                                .stream()
                                                .filter(r -> filterRecipes(r, recipeOutput))
                                                .map(r -> createStonecuttingPatternForRecipe(r,
                                                                                             allowSubstitutes))
                                                .toList();
        return removeBlankPatternsAndInsertCreatedPatterns(patterns);
    }
    @LuaFunction(mainThread = true)
    public final MethodResult createProcessingPattern(IArguments arguments) throws LuaException {
        MethodResult patternEncodingInactive = patternEncodingInactive();
        if (patternEncodingInactive != null)
            return patternEncodingInactive;
        Map<?, ?> inputs = arguments.getTable(0);
        Map<?, ?> outputs = arguments.getTable(1);
        List<GenericStack> stackInputs;
        List<GenericStack> stackOutputs;
        try {
            stackInputs = this.createGenericStacksFromLuaTable(inputs);
            stackOutputs = this.createGenericStacksFromLuaTable(outputs);
        } catch (Exception e) {
            return MethodResult.of(StatusConstants.NOT_FOUND.withInfo("Invalid input or output items."));
        }
        ItemStack pattern = PatternDetailsHelper.encodeProcessingPattern(stackInputs, stackOutputs);
        return removeBlankPatternsAndInsertCreatedPatterns(List.of(pattern));
    }
    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getItem(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        MEStorage monitor = AEApi.getMonitor(node);
        Pair<ItemFilter, String> filter = ItemFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());

        return MethodResult.of(AEApi.parseAeStack(AEApi.findAEStackFromFilter(monitor, getCraftingService(), parsedFilter), getCraftingService()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getFluid(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<FluidFilter, String> filter = FluidFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());

        return MethodResult.of(AEApi.parseAeStack(AEApi.findAEFluidFromFilter(AEApi.getMonitor(node), getCraftingService(), parsedFilter), getCraftingService()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getChemical(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ChemicalFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());

        return MethodResult.of(AEApi.parseAeStack(AEApi.findAEChemicalFromFilter(AEApi.getMonitor(node), getCraftingService(), parsedFilter), getCraftingService()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getItems(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null)));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();

        return MethodResult.of(AEApi.listItems(AEApi.getMonitor(node), getCraftingService(), parsedFilter));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getFluids(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<FluidFilter, String> filter = FluidFilter.parse(EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null)));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();

        return MethodResult.of(AEApi.listFluids(AEApi.getMonitor(node), getCraftingService(), parsedFilter));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getChemicals(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        if (!APAddon.APP_MEKANISTICS.isLoaded())
            return MethodResult.of(null, StatusConstants.ADDON_NOT_LOADED.withInfo(APAddon.APP_MEKANISTICS.name()));

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null)));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ChemicalFilter parsedFilter = filter.getLeft();

        return MethodResult.of(AEApi.listChemicals(AEApi.getMonitor(node), getCraftingService(), parsedFilter));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getCraftableItems(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null)));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();

        return MethodResult.of(AEApi.listCraftableItems(AEApi.getMonitor(node), getCraftingService(), parsedFilter));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getCraftableFluids(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<FluidFilter, String> filter = FluidFilter.parse(EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null)));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();

        return MethodResult.of(AEApi.listCraftableFluids(AEApi.getMonitor(node), getCraftingService(), parsedFilter));
    }

    @Override
    public MethodResult getCraftableChemicals(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        if (!APAddon.APP_MEKANISTICS.isLoaded())
            return MethodResult.of(null, StatusConstants.ADDON_NOT_LOADED.withInfo(APAddon.APP_MEKANISTICS.name()));

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null)));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ChemicalFilter parsedFilter = filter.getLeft();

        return MethodResult.of(AEApi.listCraftableChemicals(AEApi.getMonitor(node), getCraftingService(), parsedFilter));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getCells() {
        if (!isAvailable())
            return notConnected(null);

        return MethodResult.of(AEApi.listCells(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getDrives() {
        if (!isAvailable())
            return notConnected(null);

        return MethodResult.of(AEApi.listDrives(node.getGrid()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult importItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        String side = arguments.getString(1);
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(side, owner);

        if (inventory == null) {
            inventory = InventoryUtil.getHandlerFromName(computer, side);
        }

        if (inventory == null)
            return MethodResult.of(0, StatusConstants.INVENTORY_NOT_FOUND.name());

        return importToME(arguments, inventory);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult exportItem(IComputerAccess computer, @NotNull IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        String side = arguments.getString(1);
        IItemHandler inventory = InventoryUtil.getHandlerFromDirection(side, owner);

        if (inventory == null) {
            inventory = InventoryUtil.getHandlerFromName(computer, side);
        }

        if (inventory == null)
            return MethodResult.of(0, StatusConstants.INVENTORY_NOT_FOUND.name());

        return exportToChest(arguments, inventory);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult importFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        String side = arguments.getString(1);
        IFluidHandler fluidHandler = FluidUtil.getHandlerFromDirection(side, owner);

        if (fluidHandler == null) {
            fluidHandler = FluidUtil.getHandlerFromName(computer, side);
        }

        if (fluidHandler == null)
            return MethodResult.of(0, StatusConstants.INVENTORY_NOT_FOUND.name());

        return importToME(arguments, fluidHandler);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult exportFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        String side = arguments.getString(1);
        IFluidHandler fluidHandler = FluidUtil.getHandlerFromDirection(side, owner);

        if (fluidHandler == null) {
            fluidHandler = FluidUtil.getHandlerFromName(computer, side);
        }

        if (fluidHandler == null)
            return MethodResult.of(0, StatusConstants.INVENTORY_NOT_FOUND.name());

        return exportToTank(arguments, fluidHandler);
    }


    @Override
    @LuaFunction(mainThread = true)
    public MethodResult importChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        if (!APAddon.APP_MEKANISTICS.isLoaded())
            return MethodResult.of(null, StatusConstants.ADDON_NOT_LOADED.withInfo(APAddon.APP_MEKANISTICS.name()));

        return AEMekanismApi.importToME(arguments, computer, this);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult exportChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        if (!APAddon.APP_MEKANISTICS.isLoaded())
            return MethodResult.of(null, StatusConstants.ADDON_NOT_LOADED.withInfo(APAddon.APP_MEKANISTICS.name()));

        return AEMekanismApi.exportToTank(arguments, computer, this);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getPatterns(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        // Expected input is a table with either an input table, an output table or both to filter for both
        // If no table is provided or it's empty, return every pattern
        LuaTable<?, ?> filterTable = EmptyLuaTable.orEmpty(arguments.optTable(0).orElse(null));

        if (filterTable.isEmpty()) {
            return MethodResult.of(AEApi.listPatterns(node.getGrid(), getLevel()));
        }

        boolean hasInputFilter = filterTable.containsKey("input");
        boolean hasOutputFilter = filterTable.containsKey("output");
        boolean hasAnyFilter = hasInputFilter || hasOutputFilter;

        // If the player tries to filter for nothing, return nothing.
        if (!hasAnyFilter)
            return MethodResult.of(null, "NO_FILTER");

        GenericFilter<?> inputFilter = null;
        GenericFilter<?> outputFilter = null;

        if (hasInputFilter) {
            LuaTable<?, ?> inputFilterTable = new ObjectLuaTable(filterTable.getTable("input"));

            inputFilter = GenericFilter.parseGeneric(inputFilterTable).getLeft();
        }
        if (hasOutputFilter) {
            LuaTable<?, ?> outputFilterTable = new ObjectLuaTable(filterTable.getTable("output"));

            outputFilter = GenericFilter.parseGeneric(outputFilterTable).getLeft();
        }

        Pair<Pair<EncodedPatternItem<?>, IPatternDetails>, String> pattern = AEApi.findPatternFromFilters(node.getGrid(), getLevel(), inputFilter, outputFilter);

        if (pattern.getRight() != null)
            return MethodResult.of(null, pattern.getRight());

        return MethodResult.of(AEApi.parsePattern(pattern.getLeft()));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getStoredEnergy() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(node.getGrid().getEnergyService().getStoredPower());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyCapacity() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(node.getGrid().getEnergyService().getMaxStoredPower());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getEnergyUsage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(node.getGrid().getEnergyService().getAvgPowerUsage());
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAverageEnergyInput() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(node.getGrid().getEnergyService().getAvgPowerInjection());
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalExternalItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getTotalExternalItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalExternalFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getTotalExternalFluidStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalExternalChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getTotalExternalChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getTotalItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getTotalFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getTotalFluidStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getTotalChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getTotalChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedExternalItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getUsedExternalItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedExternalFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getUsedExternalFluidStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedExternalChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getUsedExternalChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getUsedItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getUsedFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getUsedFluidStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getUsedChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getUsedChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableExternalItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getAvailableExternalItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableExternalFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getAvailableExternalFluidStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableExternalChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getAvailableExternalChemicalStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableItemStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getAvailableItemStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult getAvailableFluidStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getAvailableFluidStorage(node));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getAvailableChemicalStorage() {
        if (!isAvailable())
            return notConnected(0);

        return MethodResult.of(AEApi.getAvailableChemicalStorage(node));
    }

    @Override
    @LuaFunction
    public final MethodResult craftItem(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<ItemFilter, String> filter = ItemFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ItemFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());

        String cpuName = arguments.optString(1, "");

        ICraftingCPU target = AEApi.getCraftingCPU(node, cpuName);
        if (!cpuName.isEmpty() && target == null) {
            return MethodResult.of(null, StatusConstants.CPU_NOT_FOUND.withInfo(cpuName));
        }

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);
        Pair<Long, AEItemKey> stack = AEApi.findAEStackFromFilter(AEApi.getMonitor(bridge.getGridNode()), craftingGrid, parsedFilter);
        if (stack.getRight() == null && stack.getLeft() == 0) {
            return MethodResult.of(null, StatusConstants.NOT_CRAFTABLE.toString());
        }

        AECraftJob job = new AECraftJob(owner.getLevel(), computer, node, stack.getRight(), parsedFilter.getCount(), bridge, target);
        bridge.addJob(job);
        return MethodResult.of(job.withCPU(target));
    }

    @Override
    @LuaFunction
    public final MethodResult craftFluid(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        Pair<FluidFilter, String> filter = FluidFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        FluidFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());

        String cpuName = arguments.optString(1, "");
        ICraftingCPU target = AEApi.getCraftingCPU(node, cpuName);
        if (!cpuName.isEmpty() && target == null)
            return MethodResult.of(null, StatusConstants.CPU_NOT_FOUND.withInfo(cpuName));

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);
        Pair<Long, AEFluidKey> stack = AEApi.findAEFluidFromFilter(AEApi.getMonitor(bridge.getGridNode()), craftingGrid, parsedFilter);
        if (stack.getRight() == null && stack.getLeft() == 0)
            return MethodResult.of(false, StatusConstants.NOT_CRAFTABLE.toString());

        AECraftJob job = new AECraftJob(owner.getLevel(), computer, node, stack.getRight(), parsedFilter.getCount(), bridge, target);
        bridge.addJob(job);
        return MethodResult.of(job.withCPU(target));
    }

    @Override
    public MethodResult craftChemical(IComputerAccess computer, IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(null);

        if (!APAddon.APP_MEKANISTICS.isLoaded())
            return MethodResult.of(null, StatusConstants.ADDON_NOT_LOADED.withInfo(APAddon.APP_MEKANISTICS.name()));

        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(new ObjectLuaTable(arguments.getTable(0)));
        if (filter.rightPresent())
            return MethodResult.of(null, filter.getRight());

        ChemicalFilter parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(null, StatusConstants.EMPTY_FILTER.toString());

        String cpuName = arguments.optString(1, "");
        ICraftingCPU target = AEApi.getCraftingCPU(node, cpuName);
        if (!cpuName.isEmpty() && target == null)
            return MethodResult.of(null, StatusConstants.CPU_NOT_FOUND.withInfo(cpuName));

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);
        Pair<Long, MekanismKey> stack = AEApi.findAEChemicalFromFilter(AEApi.getMonitor(bridge.getGridNode()), craftingGrid, parsedFilter);
        if (stack.getRight() == null && stack.getLeft() == 0)
            return MethodResult.of(false, StatusConstants.NOT_CRAFTABLE.toString());

        AECraftJob job = new AECraftJob(owner.getLevel(), computer, node, stack.getRight(), parsedFilter.getCount(), bridge, target);
        bridge.addJob(job);
        return MethodResult.of(job.withCPU(target));
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getCraftingTasks() {
        if (!isAvailable())
            return notConnected(null);

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);

        List<Object> jobs = new ArrayList<>();

        for (AECraftJob job : bridge.getJobs()) {
            for (ICraftingCPU cpu : craftingGrid.getCpus()) {
                if (cpu.isBusy() && job.getToCraft().matches(cpu.getJobStatus().crafting()))
                    jobs.add(AEApi.parseCraftingJob(cpu.getJobStatus(), job, cpu));
            }
        }
        return MethodResult.of(jobs);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult getCraftingTask(int id) {
        if (!isAvailable())
            return notConnected(null);

        AECraftJob foundJob = null;

        for (AECraftJob job : bridge.getJobs()) {
            if (job.getId() == id) {
                foundJob = job;
                break;
            }
        }
        return MethodResult.of(foundJob);
    }

    @Override
    @LuaFunction(mainThread = true)
    public MethodResult cancelCraftingTasks(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(0);

        ICraftingService craftingGrid = node.getGrid().getService(ICraftingService.class);

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(new ObjectLuaTable(arguments.getTable(0)));
        if (filter.getRight() != null)
            return MethodResult.of(0, filter.getRight());

        GenericFilter<?> parsedFilter = filter.getLeft();

        int jobsCanceled = 0;
        for (ICraftingCPU cpu : craftingGrid.getCpus()) {
            if (cpu.getJobStatus() != null && parsedFilter.testAE(cpu.getJobStatus().crafting())) {
                cpu.cancelJob();
                jobsCanceled++;
            }
        }
        return MethodResult.of(jobsCanceled);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isCraftable(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(false);

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(new ObjectLuaTable(arguments.getTable(0)));
        if (filter.getRight() != null)
            return MethodResult.of(false, filter.getRight());

        GenericFilter<?> parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, StatusConstants.EMPTY_FILTER.toString());

        return MethodResult.of(AEApi.findPatternFromFilters(node.getGrid(), getLevel(), null, parsedFilter).getLeft() != null);
    }

    @Override
    @LuaFunction(mainThread = true)
    public final MethodResult isCrafting(IArguments arguments) throws LuaException {
        if (!isAvailable())
            return notConnected(false);

        ICraftingService grid = node.getGrid().getService(ICraftingService.class);

        Pair<? extends GenericFilter<?>, String> filter = GenericFilter.parseGeneric(new ObjectLuaTable(arguments.getTable(0)));
        if (filter.getRight() != null)
            return MethodResult.of(false, filter.getRight());

        GenericFilter<?> parsedFilter = filter.getLeft();
        if (parsedFilter.isEmpty())
            return MethodResult.of(false, StatusConstants.EMPTY_FILTER.toString());

        String cpuName = arguments.optString(1, "");
        ICraftingCPU craftingCPU = AEApi.getCraftingCPU(node, cpuName);

        return MethodResult.of(AEApi.isCrafting(grid, parsedFilter, craftingCPU));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getCraftingCPUs() {
        if (!isAvailable())
            return notConnected(null);

        ICraftingService grid = node.getGrid().getService(ICraftingService.class);
        List<Object> map = new ArrayList<>();

        for (ICraftingCPU iCraftingCPU : grid.getCpus()) {
            Object cpu = AEApi.parseCraftingCPU(iCraftingCPU, false);
            map.add(cpu);
        }
        return MethodResult.of(map);
    }
}
