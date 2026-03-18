package de.srendi.advancedperipherals.common.util.inventory;

import appeng.api.stacks.GenericStack;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RSApi;
import de.srendi.advancedperipherals.common.util.Pair;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ChemicalFilter extends GenericFilter<ChemicalStack> {

    public static final ChemicalFilter EMPTY = new ChemicalFilter();

    private Holder<Chemical> chemical = MekanismAPI.EMPTY_CHEMICAL_HOLDER;
    private TagKey<Chemical> tag = null;
    private long amount = 1000;
    private String fingerprint = "";
    public int fromSlot = -1;
    public int toSlot = -1;

    private ChemicalFilter() {
    }

    public static Pair<ChemicalFilter, String> parse(LuaTable<?, ?> item) throws LuaException {
        // If the map is empty, return a filter without any filters
        if (item.isEmpty())
            return Pair.of(EMPTY, null);

        ChemicalFilter chemicalFilter = createEmpty();

        if (item.containsKey("name")) {
            String name = item.getString("name");
            if (name.startsWith("#")) {
                chemicalFilter.tag = TagKey.create(MekanismAPI.CHEMICAL_REGISTRY_NAME, ResourceLocation.parse(name.substring(1)));
            } else if ((chemicalFilter.chemical = MekanismAPI.CHEMICAL_REGISTRY.getHolder(ResourceLocation.parse(name)).orElse(null)) == null) {
                return Pair.of(null, "CHEMICAL_NOT_FOUND");
            }
        }
        if (item.containsKey("fingerprint")) {
            chemicalFilter.fingerprint = item.getString("fingerprint");
        }
        if (item.containsKey("fromSlot")) {
            chemicalFilter.fromSlot = item.getInt("fromSlot");
        }
        if (item.containsKey("toSlot")) {
            chemicalFilter.toSlot = item.getInt("toSlot");
        }
        if (item.containsKey("amount")) {
            chemicalFilter.amount = item.getLong("amount");
        }

        AdvancedPeripherals.debug("Parsed item filter: " + chemicalFilter);
        return Pair.of(chemicalFilter, null);
    }

    public static ChemicalFilter fromStack(ChemicalStack stack) {
        return fromStackWithAmount(stack, stack.getAmount());
    }

    public static ChemicalFilter fromStackWithAmount(ChemicalStack stack, long amount) {
        ChemicalFilter filter = createEmpty();
        filter.chemical = stack.getChemicalHolder();
        filter.amount = amount;
        return filter;
    }

    public static ChemicalFilter createEmpty() {
        return new ChemicalFilter();
    }

    public boolean isEmpty() {
        return this == EMPTY || (fingerprint.isEmpty() && chemical.is(MekanismAPI.EMPTY_CHEMICAL_KEY) && tag == null);
    }

    @Override
    public boolean testAE(GenericStack genericStack) {
        return false;
    }

    @Override
    public boolean testRS(ResourceAmount resourceAmount) {
        if (!APAddon.REFINEDSTORAGE_MEKANISM.isLoaded())
            return false;
        if (resourceAmount.resource() instanceof ChemicalResource chemicalResource) {
            return test(RSApi.resourceToChemicalStack(chemicalResource));
        }
        return false;
    }

    @Override
    public ChemicalFilter copy() {
        ChemicalFilter newFilter = new ChemicalFilter();
        newFilter.chemical = this.chemical;
        newFilter.tag = this.tag;
        newFilter.amount = this.amount;
        newFilter.fingerprint = this.fingerprint;
        newFilter.fromSlot = this.fromSlot;
        newFilter.toSlot = this.toSlot;
        return newFilter;
    }

    public ChemicalFilter copyWithAmount(long amount) {
        ChemicalFilter newFilter = this.copy();
        newFilter.amount = amount;
        return newFilter;
    }

    public ChemicalStack toChemicalStack() {
        return new ChemicalStack(chemical, amount);
    }

    public boolean test(ChemicalStack stack) {
        if (isEmpty())
            return true;

        if (!fingerprint.isEmpty()) {
            String testFingerprint = ChemicalUtil.getFingerprint(stack);
            return fingerprint.equals(testFingerprint);
        }

        if (!chemical.is(MekanismAPI.EMPTY_CHEMICAL_KEY) && !stack.is(chemical)) {
            return false;
        }
        if (tag != null && !stack.is(tag)) {
            return false;
        }

        return true;
    }

    public long getAmount() {
        return amount;
    }

    public Holder<Chemical> getChemical() {
        return chemical;
    }

    public int getFromSlot() {
        return fromSlot;
    }

    public int getToSlot() {
        return toSlot;
    }

    @Override
    public String toString() {
        return "ChemicalFilter{" +
                "item=" + chemical.getRegisteredName() +
                ", tag=" + tag +
                ", amount=" + amount +
                ", fingerprint='" + fingerprint + '\'' +
                ", fromSlot=" + fromSlot +
                ", toSlot=" + toSlot +
                '}';
    }
}
