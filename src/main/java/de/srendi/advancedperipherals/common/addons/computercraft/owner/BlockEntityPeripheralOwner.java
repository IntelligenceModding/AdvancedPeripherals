package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.base.VarNameable;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.common.util.StringUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockEntityPeripheralOwner<T extends BlockEntity & IPeripheralBlockEntity> extends BasePeripheralOwner {

    public final T tileEntity;

    public BlockEntityPeripheralOwner(T tileEntity) {
        super();
        this.tileEntity = tileEntity;
    }

    @Nullable
    @Override
    public String getCustomName() {
        if (!(tileEntity instanceof Nameable nameableEntity)) {
            return null;
        }
        Component name = nameableEntity.getCustomName();
        if (name != null) {
            return name.getString();
        }
        return null;
    }

    @Override
    public void setCustomName(String name) {
        if (!(tileEntity instanceof VarNameable nameableEntity)) {
            return;
        }
        name = StringUtil.validateName(name);
        nameableEntity.setName(name == null ? null : Component.literal(name));
    }

    @NotNull
    @Override
    public Level getLevel() {
        return Objects.requireNonNull(tileEntity.getLevel());
    }

    @NotNull
    @Override
    public BlockPos getPos() {
        return tileEntity.getBlockPos();
    }

    @NotNull
    @Override
    public Direction getFacing() {
        return getOrientation().front();
    }

    @NotNull
    @Override
    public FrontAndTop getOrientation() {
        return tileEntity.getBlockState().getValue(BaseBlock.ORIENTATION);
    }

    @Nullable
    @Override
    public Entity getHoldingEntity() {
        return null;
    }

    @Nullable
    @Override
    public Player getOwner() {
        return null;
    }

    @Override
    public DataComponentPatch getDataStorage() {
        return DataStorageUtil.getDataStorage(tileEntity);
    }

    @Override
    public void putDataStorage(DataComponentPatch dataStorage) {
        DataStorageUtil.putDataStorage(tileEntity, dataStorage);
    }

    @Override
    public <T1> T1 withPlayer(APFakePlayer.Action<T1> function) throws LuaException {
        throw new NotImplementedException();
    }

    @Override
    public ItemStack getToolInMainHand() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack storeItem(ItemStack stored) {
        // TODO: tricks with capability needed
        throw new NotImplementedException();
    }

    @Override
    public void destroyUpgrade() {
        getLevel().removeBlock(tileEntity.getBlockPos(), false);
    }

    @Override
    public boolean isMovementPossible(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean move(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }

    public BlockEntityPeripheralOwner<T> attachFuel() {
        attachAbility(PeripheralOwnerAbility.FUEL, new TileEntityFuelAbility<>(this));
        return this;
    }

    @Override
    public <U extends IPeripheral> U getConnectedPeripheral(Class<U> type) {
        throw new NotImplementedException();
    }
}
