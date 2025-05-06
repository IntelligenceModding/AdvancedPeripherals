package de.srendi.advancedperipherals.common.util.inventory;

import appeng.api.stacks.GenericStack;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.apis.TableHelper;
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

import java.util.Map;

public class ChemicalFilter extends GenericFilter<ChemicalStack> {

    public static final ChemicalFilter EMPTY = new ChemicalFilter();

    private Holder<Chemical> chemical = MekanismAPI.EMPTY_CHEMICAL_HOLDER;
    private TagKey<Chemical> tag = null;
    private long count = 64;
    private String fingerprint = "";
    public int fromSlot = -1;
    public int toSlot = -1;

    private ChemicalFilter() {
    }

    public static Pair<ChemicalFilter, String> parse(Map<?, ?> item) {
        // If the map is empty, return a filter without any filters
        if (item.isEmpty())
            return Pair.of(EMPTY, null);

        ChemicalFilter chemicalFilter = createEmpty();

        if (item.containsKey("name")) {
            try {
                String name = TableHelper.getStringField(item, "name");
                if (name.startsWith("#")) {
                    chemicalFilter.tag = TagKey.create(MekanismAPI.CHEMICAL_REGISTRY_NAME, ResourceLocation.parse(name.substring(1)));
                } else if ((chemicalFilter.chemical = MekanismAPI.CHEMICAL_REGISTRY.getHolder(ResourceLocation.parse(name)).orElse(null)) == null) {
                    return Pair.of(null, "CHEMICAL_NOT_FOUND");
                }
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_ITEM");
            }
        }
        if (item.containsKey("fingerprint")) {
            try {
                chemicalFilter.fingerprint = TableHelper.getStringField(item, "fingerprint");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FINGERPRINT");
            }
        }
        if (item.containsKey("fromSlot")) {
            try {
                chemicalFilter.fromSlot = TableHelper.getIntField(item, "fromSlot");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FROMSLOT");
            }
        }
        if (item.containsKey("toSlot")) {
            try {
                chemicalFilter.toSlot = TableHelper.getIntField(item, "toSlot");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_TOSLOT");
            }
        }
        if (item.containsKey("count")) {
            try {
                chemicalFilter.count = TableHelper.getIntField(item, "count");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_COUNT");
            }
        }

        AdvancedPeripherals.debug("Parsed item filter: " + chemicalFilter);
        return Pair.of(chemicalFilter, null);
    }

    public static ChemicalFilter fromStack(ChemicalStack stack) {
        ChemicalFilter filter = createEmpty();
        filter.chemical = stack.getChemicalHolder();
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

    public ChemicalStack toChemicalStack() {
        return new ChemicalStack(chemical, count);
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

    public long getCount() {
        return count;
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
                ", count=" + count +
                ", fingerprint='" + fingerprint + '\'' +
                ", fromSlot=" + fromSlot +
                ", toSlot=" + toSlot +
                '}';
    }
}
