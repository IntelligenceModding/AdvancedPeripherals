package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PlayerDetectorPeripheral;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PocketPlayerDetectorUpgrade extends BasePocketUpgrade<PlayerDetectorPeripheral> {

    public PocketPlayerDetectorUpgrade(ResourceLocation id, ItemStack stack) {
        super(id, stack);
    }

    @Nullable
    @Override
    public PlayerDetectorPeripheral getPeripheral(@NotNull IPocketAccess iPocketAccess) {
        return new PlayerDetectorPeripheral(iPocketAccess);
    }

}
