package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.networking.crafting.ICraftingService;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.AEKeyFilter;
import appeng.api.storage.MEStorage;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AEMekanismApi {
    @NotNull
    public static Pair<Long, MekanismKey> findAEChemicalFromFilter(MEStorage monitor, @Nullable ICraftingService crafting, ChemicalFilter filter) {
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof MekanismKey key && filter.test(key.getStack()))
                return Pair.of(temp.getLongValue(), key);
        }

        if (crafting == null)
            return Pair.of(0L, null);

        for (var temp : crafting.getCraftables(param -> true)) {
            if (temp instanceof MekanismKey key && filter.test(key.getStack()))
                return Pair.of(0L, key);
        }

        return Pair.of(0L, null);
    }

    @NotNull
    public static List<Pair<Long, MekanismKey>> findAEChemicalsFromFilter(MEStorage monitor, ChemicalFilter filter) {
        List<Pair<Long, MekanismKey>> chemicals = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> temp : monitor.getAvailableStacks()) {
            if (temp.getKey() instanceof MekanismKey key && filter.test(key.getStack())) {
                chemicals.add(Pair.of(temp.getLongValue(), key));
            }
        }
        return chemicals;
    }

    public static List<Object> listChemicals(MEStorage monitor, ICraftingService service, ChemicalFilter filter) {
        List<Object> items = new ArrayList<>();
        for (Object2LongMap.Entry<AEKey> aeKey : monitor.getAvailableStacks()) {
            if (APAddon.APP_MEKANISTICS.isLoaded() && aeKey.getKey() instanceof MekanismKey mekanismKey && filter.test(mekanismKey.getStack())) {
                items.add(AEApi.parseAeStack(Pair.of(aeKey.getLongValue(), mekanismKey), service));
            }
        }
        return items;
    }

    public static List<Object> listCraftableChemicals(MEStorage monitor, ICraftingService service, ChemicalFilter filter) {
        List<Object> items = new ArrayList<>();
        KeyCounter keyCounter = monitor.getAvailableStacks();
        Set<AEKey> craftables = service.getCraftables(AEKeyFilter.none());
        for (AEKey aeKey : craftables) {
            if (aeKey instanceof MekanismKey mekanismKey && filter.test(mekanismKey.getStack())) {
                items.add(AEApi.parseAeStack(Pair.of(keyCounter.get(aeKey), aeKey), service));
            }
        }
        return items;
    }

    public static Map<String, Object> parseChemStack(Pair<Long, MekanismKey> stack, @Nullable ICraftingService craftingService) {
        Map<String, Object> properties = LuaConverter.chemicalStackToLua(stack.right().withAmount(stack.left()));
        properties.put("isCraftable", craftingService != null && craftingService.isCraftable(stack.right()));
        return properties;
    }
}
