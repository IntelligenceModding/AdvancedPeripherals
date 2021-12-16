package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PocketEnvironmentUpgrade extends BasePocketUpgrade<EnvironmentDetectorPeripheral> {

    public PocketEnvironmentUpgrade(ResourceLocation id, ItemStack stack) {
        super(id, stack);
    }

    @Nullable
    @Override
    public EnvironmentDetectorPeripheral getPeripheral(@NotNull IPocketAccess iPocketAccess) {
        return new EnvironmentDetectorPeripheral(iPocketAccess);
    }

}
