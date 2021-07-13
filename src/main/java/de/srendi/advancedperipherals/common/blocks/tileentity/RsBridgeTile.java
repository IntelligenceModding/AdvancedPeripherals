package de.srendi.advancedperipherals.common.blocks.tileentity;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IRedstoneConfigurable;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.api.peripheral.IPeripheralTileEntity;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RsBridgePeripheral;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorageNode;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class RsBridgeTile extends NetworkNodeTile<RefinedStorageNode> implements INetworkNodeProxy<RefinedStorageNode>, IRedstoneConfigurable, IPeripheralTileEntity {

    private static final String AP_SETTINGS_KEY = "AP_SETTINGS";

    protected CompoundNBT apSettings;

    protected RsBridgePeripheral peripheral = new RsBridgePeripheral("rsBridge", this);
    private LazyOptional<IPeripheral> peripheralCap;

    public RsBridgeTile() {
        super(TileEntityTypes.RS_BRIDGE.get());
    }

    @NotNull
    public <T1> LazyOptional<T1> getCapability(@NotNull Capability<T1> cap, @Nullable Direction direction) {
        if (cap == CAPABILITY_PERIPHERAL) {
            if (peripheral.isEnabled()) {
                if (peripheralCap == null) {
                    peripheralCap = LazyOptional.of(() -> peripheral);
                }
                return peripheralCap.cast();
            } else {
                AdvancedPeripherals.debug(peripheral.getType() + " is disabled, you can enable it in the Configuration.");
            }
        }
        return super.getCapability(cap, direction);
    }

    public RefinedStorageNode createNode(World world, BlockPos blockPos) {
        return new RefinedStorageNode(world, blockPos);
    }

    @Override
    public @NotNull CompoundNBT save(@NotNull CompoundNBT compound) {
        super.save(compound);
        if (!apSettings.isEmpty())
            compound.put(AP_SETTINGS_KEY, apSettings);
        return compound;
    }

    @Override
    public void load(@NotNull BlockState state, CompoundNBT compound) {
        apSettings = compound.getCompound(AP_SETTINGS_KEY);
        super.load(state, compound);
    }

    @Override
    public CompoundNBT getApSettings() {
        return apSettings;
    }
}