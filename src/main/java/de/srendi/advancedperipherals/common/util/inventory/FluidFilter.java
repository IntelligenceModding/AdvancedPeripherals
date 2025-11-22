package de.srendi.advancedperipherals.common.util.inventory;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.neoforge.support.resource.VariantUtil;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.util.DataComponentUtil;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidFilter extends GenericFilter<FluidStack> {

    public static final FluidFilter EMPTY = new FluidFilter();

    private Fluid fluid = Fluids.EMPTY;
    private TagKey<Fluid> tag = null;
    private Tag componentsAsNbt = null;
    private PatchedDataComponentMap components = null;
    private int amount = 1000;
    private String fingerprint = "";

    private FluidFilter() {
    }

    public static Pair<FluidFilter, String> parse(LuaTable<?, ?> item) throws LuaException {
        // If the map is empty, return a filter without any filters
        if (item.isEmpty())
            return Pair.of(EMPTY, null);

        FluidFilter fluidFilter = createEmpty();

        if (item.containsKey("name")) {
            String name = item.getString("name");
            if (name.startsWith("#")) {
                fluidFilter.tag = TagKey.create(Registries.FLUID, ResourceLocation.parse(name.substring(1)));
            } else {
                fluidFilter.fluid = ItemUtil.getRegistryEntry(name, BuiltInRegistries.FLUID);
                if (fluidFilter.fluid == null) {
                    return Pair.of(null, "FLUID_NOT_FOUND");
                }
            }
        }
        if (item.containsKey("components")) {
            try {
                fluidFilter.componentsAsNbt = NBTUtil.fromText(item.getString("components"));
            } catch (LuaException e1) {
                try {
                    fluidFilter.componentsAsNbt = NBTUtil.fromText(item.getTable("components").toString());
                } catch (LuaException e2) {
                    throw new LuaException("bad field \"components\", expect NBT string or table");
                }
            }
        }
        if (item.containsKey("fingerprint")) {
            fluidFilter.fingerprint = item.getString("fingerprint");
        }
        // TODO: rename count to amount in 0.8
        if (item.containsKey("amount")) {
            fluidFilter.amount = item.getInt("amount");
        } else if (item.containsKey("count")) {
            fluidFilter.amount = item.getInt("count");
        }
        AdvancedPeripherals.debug("Parsed fluid filter: " + fluidFilter);

        return Pair.of(fluidFilter, null);
    }

    public static FluidFilter fromStack(FluidStack stack) {
        return fromStackWithAmount(stack, stack.getAmount());
    }

    public static FluidFilter fromStackWithAmount(FluidStack stack, int amount) {
        FluidFilter filter = createEmpty();
        filter.fluid = stack.getFluid();
        filter.amount = amount;
        filter.componentsAsNbt = DataComponentUtil.toNbt(stack.getComponentsPatch());
        filter.components = stack.getComponents();
        return filter;
    }

    public static FluidFilter createEmpty() {
        return new FluidFilter();
    }

    public boolean isEmpty() {
        return this == EMPTY || (fingerprint.isEmpty() && fluid == Fluids.EMPTY && tag == null && componentsAsNbt == null);
    }

    @Override
    public boolean testAE(GenericStack genericStack) {
        if (!APAddon.AE2.isLoaded()) {
            return false;
        }

        if (genericStack.what() instanceof AEFluidKey aeFluidKey) {
            return test(aeFluidKey.toStack(1));
        }
        return false;
    }

    @Override
    public boolean testRS(ResourceAmount resourceAmount) {
        if (!APAddon.REFINEDSTORAGE.isLoaded()) {
            return false;
        }

        if (resourceAmount.resource() instanceof FluidResource fluidResource) {
            return test(VariantUtil.toFluidStack(fluidResource, 1));
        }
        return false;
    }

    @Override
    public FluidFilter copy() {
        FluidFilter newFilter = new FluidFilter();
        newFilter.fluid = this.fluid;
        newFilter.tag = this.tag;
        newFilter.componentsAsNbt = this.componentsAsNbt;
        newFilter.components = this.components;
        newFilter.amount = this.amount;
        newFilter.fingerprint = this.fingerprint;
        return newFilter;
    }

    public FluidFilter copyWithAmount(int amount) {
        FluidFilter newFilter = this.copy();
        newFilter.amount = amount;
        return newFilter;
    }

    public FluidStack toFluidStack() {
        FluidStack result = new FluidStack(fluid, amount);
        if (componentsAsNbt != null) {
            result.applyComponents(components);
        }
        return result;
    }

    public boolean test(FluidStack stack) {
        if (!fingerprint.isEmpty()) {
            String testFingerprint = FluidUtil.getFingerprint(stack);
            return fingerprint.equals(testFingerprint);
        }

        if (fluid != Fluids.EMPTY && !stack.getFluid().isSame(fluid)) {
            return false;
        }
        if (tag != null && !stack.getFluid().is(tag)) {
            return false;
        }
        if (componentsAsNbt != null && !DataComponentUtil.toNbt(stack.getComponentsPatch()).equals(componentsAsNbt)) {
            return false;
        }
        return true;
    }

    public int getAmount() {
        return amount;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public Tag getComponentsAsNbt() {
        return componentsAsNbt;
    }

    @Override
    public String toString() {
        return "FluidFilter{" +
                "fluid=" + FluidUtil.getRegistryKey(fluid) +
                ", tag=" + tag +
                ", components=" + componentsAsNbt +
                ", amount=" + amount +
                ", fingerprint='" + fingerprint + '\'' +
                '}';
    }
}
