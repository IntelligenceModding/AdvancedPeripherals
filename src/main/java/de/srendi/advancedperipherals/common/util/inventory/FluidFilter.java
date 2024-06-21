package de.srendi.advancedperipherals.common.util.inventory;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.apis.TableHelper;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class FluidFilter extends GenericFilter {

    private Fluid fluid = Fluids.EMPTY;
    private TagKey<Fluid> tag = null;
    private CompoundTag nbt = null;
    private int count = 1000;
    private String fingerprint = "";

    private FluidFilter() {
    }

    public static Pair<FluidFilter, String> parse(Map<?, ?> item) {
        FluidFilter fluidFilter = empty();
        // If the map is empty, return a filter without any filters
        if (item.isEmpty())
            return Pair.of(fluidFilter, null);
        if (item.containsKey("name")) {
            try {
                String name = TableHelper.getStringField(item, "name");
                if (name.startsWith("#")) {
                    fluidFilter.tag = TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(name.substring(1)));
                } else if ((fluidFilter.fluid = ItemUtil.getRegistryEntry(name, ForgeRegistries.FLUIDS)) == null) {
                    return Pair.of(null, "FLUID_NOT_FOUND");
                }
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FLUID");
            }
        }
        if (item.containsKey("nbt")) {
            try {
                fluidFilter.nbt = NBTUtil.fromText(TableHelper.getStringField(item, "nbt"));
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_NBT");
            }
        }
        if (item.containsKey("fingerprint")) {
            try {
                fluidFilter.fingerprint = TableHelper.getStringField(item, "fingerprint");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_FINGERPRINT");
            }
        }
        if (item.containsKey("count")) {
            try {
                fluidFilter.count = TableHelper.getIntField(item, "count");
            } catch (LuaException luaException) {
                return Pair.of(null, "NO_VALID_COUNT");
            }
        }
        AdvancedPeripherals.debug("Parsed fluid filter: " + fluidFilter);

        return Pair.of(fluidFilter, null);
    }

    public static FluidFilter fromStack(FluidStack stack) {
        FluidFilter filter = empty();
        filter.fluid = stack.getFluid();
        filter.nbt = stack.hasTag() ? stack.getTag() : null;
        return filter;
    }

    public static FluidFilter empty() {
        return new FluidFilter();
    }

    public boolean isEmpty() {
        return fingerprint.isEmpty() && fluid == Fluids.EMPTY && tag == null && nbt == null;
    }

    public FluidStack toFluidStack() {
        var result = new FluidStack(fluid, count);
        result.setTag(nbt != null ? nbt.copy() : null);
        return result;
    }

    public FluidFilter setCount(int count) {
        this.count = count;
        return this;
    }

    @Override
    public boolean test(GenericStack genericStack) {
        if (genericStack.what() instanceof AEFluidKey aeFluidKey) {
            return test(aeFluidKey.toStack(1));
        }
        return false;
    }

    public boolean test(FluidStack stack) {
        if (!fingerprint.isEmpty()) {
            String testFingerprint = FluidUtil.getFingerprint(stack);
            return fingerprint.equals(testFingerprint);
        }

        // If the filter does not have nbt values, a tag or a fingerprint, just test if the items are the same
        if (fluid != Fluids.EMPTY) {
            if (tag == null && nbt == null && fingerprint.isEmpty())
                return stack.getFluid().isSame(fluid);
        }
        if (tag != null && !stack.getFluid().is(tag))
            return false;
        if (nbt != null && !stack.getOrCreateTag().equals(nbt) && (fluid == Fluids.EMPTY || stack.getFluid().isSame(fluid)))
            return false;

        return true;
    }

    public int getCount() {
        return count;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public Tag getNbt() {
        return nbt;
    }

    @Override
    public String toString() {
        return "FluidFilter{" +
                "fluid=" + FluidUtil.getRegistryKey(fluid) +
                ", tag=" + tag +
                ", nbt=" + nbt +
                ", count=" + count +
                ", fingerprint='" + fingerprint + '\'' +
                '}';
    }
}
