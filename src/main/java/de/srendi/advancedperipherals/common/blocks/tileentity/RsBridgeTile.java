package de.srendi.advancedperipherals.common.blocks.tileentity;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.config.IRedstoneConfigurable;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.IPeripheralTileEntity;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.RsBridgePeripheral;
import de.srendi.advancedperipherals.common.addons.refinedstorage.RefinedStorageNode;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class RsBridgeTile extends NetworkNodeTile<RefinedStorageNode> implements INetworkNodeProxy<RefinedStorageNode>, IRedstoneConfigurable, IPeripheralTileEntity {

    private static final String AP_SETTINGS_KEY = "AP_SETTINGS";

    protected CompoundTag apSettings;

    protected RsBridgePeripheral peripheral = new RsBridgePeripheral("rsBridge", this);
    private LazyOptional<IPeripheral> peripheralCap;

    public RsBridgeTile(BlockPos pos, BlockState state) {
        super(TileEntityTypes.RS_BRIDGE.get(), pos, state);
        apSettings = new CompoundTag();
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

    public RefinedStorageNode createNode(Level world, BlockPos blockPos) {
        return new RefinedStorageNode(world, blockPos);
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compound) {
        super.save(compound);
        if (!apSettings.isEmpty())
            compound.put(AP_SETTINGS_KEY, apSettings);
        return compound;
    }

    @Override
    public void load(CompoundTag compound) {
        apSettings = compound.getCompound(AP_SETTINGS_KEY);
        super.load(compound);
    }

    @Override
    public CompoundTag getApSettings() {
        return apSettings;
    }
}