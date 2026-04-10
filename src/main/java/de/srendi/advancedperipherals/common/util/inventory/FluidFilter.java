package de.srendi.advancedperipherals.common.util.inventory;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.neoforge.support.resource.VariantUtil;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.LuaValues;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.util.DataComponentUtil;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.RegistryUtil;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Map;

public class FluidFilter extends GenericFilter<FluidStack> {

    public static final FluidFilter EMPTY = new FluidFilter();

    private Fluid fluid = Fluids.EMPTY;
    private TagKey<Fluid> tag = null;
    private DataComponentPatch components = null;
    private int amount = Integer.MAX_VALUE;
    private String fingerprint = "";

    private FluidFilter() {
    }

    public static Pair<FluidFilter, String> parse(LuaTable<?, ?> item) throws LuaException {
        // If the map is empty, return a filter without any filters
        if (item.isEmpty()) {
            return Pair.of(EMPTY, null);
        }

        FluidFilter fluidFilter = createEmpty();

        if (item.containsKey("name")) {
            String name = item.getString("name");
            if (name.startsWith("#")) {
                fluidFilter.tag = TagKey.create(Registries.FLUID, ResourceLocation.parse(name.substring(1)));
            } else {
                fluidFilter.fluid = RegistryUtil.getRegistryEntry(name, BuiltInRegistries.FLUID);
                if (fluidFilter.fluid == null) {
                    return Pair.of(null, "FLUID_NOT_FOUND");
                }
            }
        }
        if (item.containsKey("components")) {
            Object components = item.get("components");
            CompoundTag componentsAsNbt;
            if (components instanceof String snbt) {
                componentsAsNbt = NBTUtil.fromSNBT(snbt);
            } else if (components instanceof Map<?, ?> map) {
                componentsAsNbt = NBTUtil.mapToNBT(map);
            } else {
                throw LuaValues.badField("components", "string or table", LuaValues.getType(components));
            }
            fluidFilter.components = DataComponentUtil.nbtToPatch(componentsAsNbt);
        }
        if (item.containsKey("fingerprint")) {
            fluidFilter.fingerprint = item.getString("fingerprint");
        }
        if (item.containsKey("amount")) {
            fluidFilter.amount = item.getInt("amount");
        }
        AdvancedPeripherals.debug("Parsed fluid filter: {}", fluidFilter);

        return Pair.of(fluidFilter, null);
    }

    public static FluidFilter fromStack(FluidStack stack) {
        return fromStackWithAmount(stack, stack.getAmount());
    }

    public static FluidFilter fromStackWithAmount(FluidStack stack, int amount) {
        FluidFilter filter = createEmpty();
        filter.fluid = stack.getFluid();
        filter.amount = amount;
        filter.components = stack.getComponentsPatch();
        return filter;
    }

    public static FluidFilter createEmpty() {
        return new FluidFilter();
    }

    @Override
    public boolean isEmpty() {
        return this == EMPTY || (fingerprint.isEmpty() && fluid == Fluids.EMPTY && tag == null && components == null);
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
        if (components != null && !components.isEmpty()) {
            result.applyComponents(components);
        }
        return result;
    }

    @Override
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
        if (components != null && !stack.getComponentsPatch().equals(components)) {
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

    public DataComponentPatch getComponents() {
        return components;
    }

    @Override
    public String toString() {
        return "FluidFilter{" +
                "fluid=" + FluidUtil.getRegistryKey(fluid) +
                ", tag=" + tag +
                ", components=" + components +
                ", amount=" + amount +
                ", fingerprint='" + fingerprint + '\'' +
                '}';
    }
}
