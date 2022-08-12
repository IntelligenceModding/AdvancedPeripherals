package de.srendi.advancedperipherals.common.addons.refinedstorage;


import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RefinedStorageNode extends NetworkNode {

    public RefinedStorageNode(Level level, BlockPos pos) {
        super(level, pos);
    }

    @Override
    public int getEnergyUsage() {
        return APConfig.PERIPHERALS_CONFIG.rsConsumption.get();
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "rs_bridge");
    }

    @NotNull
    @Override
    public ItemStack getItemStack() {
        return super.getItemStack();
    }
}
