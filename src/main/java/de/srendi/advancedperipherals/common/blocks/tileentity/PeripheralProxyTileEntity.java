package de.srendi.advancedperipherals.common.blocks.tileentity;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegrationRegistry;
import de.srendi.advancedperipherals.common.blocks.PeripheralProxyBlock;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PeripheralProxyTileEntity extends TileEntity {

    public PeripheralProxyTileEntity() {
        super(TileEntityTypes.PERIPHERAL_PROXY.get());
    }

    private ProxyIntegration integration;
    private LazyOptional<IPeripheral> peripheralCap = LazyOptional.empty();

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.CAPABILITY_PERIPHERAL) {
            PeripheralProxyBlock block = (PeripheralProxyBlock) getBlockState().getBlock();
            if(block.getTileEntityInFront(getWorld(), getPos()) != null) {
                if(integration == null) {
                    if(ProxyIntegrationRegistry.getIntegration(block.getTileEntityInFront(getWorld(), getPos())) != null) {
                        integration = ProxyIntegrationRegistry.getIntegration(block.getTileEntityInFront(getWorld(), getPos()));
                        integration.setTileEntity(block.getTileEntityInFront(getWorld(), getPos()));
                        peripheralCap = LazyOptional.of(() -> integration);
                    }
                }
                return peripheralCap.cast();
            }
        }
        return super.getCapability(cap, side);
    }

}
