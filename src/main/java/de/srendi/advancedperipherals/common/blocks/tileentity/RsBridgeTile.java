package de.srendi.advancedperipherals.common.blocks.tileentity;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IRedstoneConfigurable;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RsBridgePeripheral;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorageNode;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class RsBridgeTile extends NetworkNodeTile<RefinedStorageNode> implements INetworkNodeProxy<RefinedStorageNode>, IRedstoneConfigurable, IPeripheralTileEntity {

    private static final String PERIPHERAL_SETTINGS = "AP_SETTINGS";

    protected CompoundNBT peripheralSettings;

    protected RsBridgePeripheral peripheral = new RsBridgePeripheral(this);
    private LazyOptional<IPeripheral> peripheralCap;

    public RsBridgeTile() {
        super(TileEntityTypes.RS_BRIDGE.get());
        peripheralSettings = new CompoundNBT();
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
        if (!peripheralSettings.isEmpty())
            compound.put(PERIPHERAL_SETTINGS, peripheralSettings);
        return compound;
    }

    @Override
    public void load(@NotNull BlockState state, CompoundNBT compound) {
        peripheralSettings = compound.getCompound(PERIPHERAL_SETTINGS);
        super.load(state, compound);
    }

    @Override
    public CompoundNBT getPeripheralSettings() {
        return peripheralSettings;
    }

    @Override
    public void markSettingsChanged() {
        setChanged();
    }
}