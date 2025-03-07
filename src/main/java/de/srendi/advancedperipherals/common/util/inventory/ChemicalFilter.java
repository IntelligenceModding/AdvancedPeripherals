package de.srendi.advancedperipherals.common.util.inventory;

import appeng.api.stacks.GenericStack;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.apis.TableHelper;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.Pair;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Map;

public class ChemicalFilter extends GenericFilter<ChemicalStack> {

    private Chemical chemical = MekanismAPI.EMPTY_CHEMICAL;
    private TagKey<Chemical> tag = null;
    private int count = 64;
    private String fingerprint = "";
    public int fromSlot = -1;
    public int toSlot = -1;

    private ChemicalFilter() {
    }

    public static Pair<ChemicalFilter, String> parse(Map<?, ?> item) {
        ChemicalFilter itemFilter = empty();
        // If the map is empty, return a filter without any filters
        if (item.isEmpty())
            return Pair.of(itemFilter, null);
        if (item.containsKey("name")) {
            try {
                String name = TableHelper.getStringField(item, "name");
                if (name.startsWith("#")) {
                    itemFilter.tag = TagKey.create(MekanismAPI.CHEMICAL_REGISTRY_NAME, ResourceLocation.parse(name.substring(1)));
                } else if ((itemFilter.chemical = ItemUtil.getRegistryEntry(name, MekanismAPI.CHEMICAL_REGISTRY)) == null) {
                    return Pair.of(null, "CHEMICAL_NOT_FOUND");
                }
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_ITEM");
            }
        }
        if (item.containsKey("fingerprint")) {
            try {
                itemFilter.fingerprint = TableHelper.getStringField(item, "fingerprint");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FINGERPRINT");
            }
        }
        if (item.containsKey("fromSlot")) {
            try {
                itemFilter.fromSlot = TableHelper.getIntField(item, "fromSlot");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FROMSLOT");
            }
        }
        if (item.containsKey("toSlot")) {
            try {
                itemFilter.toSlot = TableHelper.getIntField(item, "toSlot");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_TOSLOT");
            }
        }
        if (item.containsKey("count")) {
            try {
                itemFilter.count = TableHelper.getIntField(item, "count");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_COUNT");
            }
        }

        AdvancedPeripherals.debug("Parsed item filter: " + itemFilter);
        return Pair.of(itemFilter, null);
    }

    public static ChemicalFilter fromStack(ChemicalStack stack) {
        ChemicalFilter filter = empty();
        filter.chemical = stack.getChemical();
        return filter;
    }

    public static ChemicalFilter empty() {
        return new ChemicalFilter();
    }

    public boolean isEmpty() {
        return fingerprint.isEmpty() && chemical == MekanismAPI.EMPTY_CHEMICAL && tag == null;
    }

    @Override
    public boolean testAE(GenericStack genericStack) {
        //if (genericStack.what() instanceof  aeItemKey) {
        //    return test(aeItemKey.toStack());
        //}
        return false;
    }

    @Override
    public boolean testRS(ResourceAmount resourceAmount) {
        if (resourceAmount.resource() instanceof ChemicalResource chemicalResource) {
            return test(new ChemicalStack(chemical.getChemical(), resourceAmount.amount()));
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

        if (chemical != MekanismAPI.EMPTY_CHEMICAL && !stack.is(chemical)) {
            return false;
        }
        if (tag != null && !stack.is(tag)) {
            return false;
        }

        return true;
    }

    public int getCount() {
        return count;
    }

    public Chemical getChemical() {
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
        return "ItemFilter{" +
                "item=" + ChemicalUtil.getRegistryKey(chemical) +
                ", tag=" + tag +
                ", count=" + count +
                ", fingerprint='" + fingerprint + '\'' +
                ", fromSlot=" + fromSlot +
                ", toSlot=" + toSlot +
                '}';
    }
}
