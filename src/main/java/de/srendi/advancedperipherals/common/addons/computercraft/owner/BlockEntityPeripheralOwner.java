package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.base.VarNameable;
import de.srendi.advancedperipherals.common.util.StringUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
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

public class BlockEntityPeripheralOwner<T extends BlockEntity & IPeripheralBlockEntity> extends BasePeripheralOwner {
    @NotNull
    private final T blockEntity;

    public BlockEntityPeripheralOwner(@NotNull T blockEntity) {
        super();
        this.blockEntity = blockEntity;
    }

    @NotNull
    public final T getBlockEntity() {
        return blockEntity;
    }

    @Override
    @Nullable
    public String getCustomName() {
        if (!(blockEntity instanceof Nameable nameableEntity)) {
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
        if (!(blockEntity instanceof VarNameable nameableEntity)) {
            return;
        }
        name = StringUtil.validateName(name);
        nameableEntity.setName(name == null ? null : Component.literal(name));
    }

    @Override
    @NotNull
    public Level getLevel() {
        return blockEntity.getLevel();
    }

    @Override
    @NotNull
    public BlockPos getPos() {
        return blockEntity.getBlockPos();
    }

    @Override
    @NotNull
    public Direction getFacing() {
        return getFrontAndTop().front();
    }

    @Override
    @NotNull
    public FrontAndTop getFrontAndTop() {
        return blockEntity.getBlockState().getValue(BaseBlock.ORIENTATION);
    }

    @Override
    @NotNull
    public Direction getRightDirection() {
        return super.getRightDirection().getOpposite();
    }

    @Override
    @Nullable
    public Entity getHoldingEntity() {
        return null;
    }

    @Override
    @Nullable
    public Player getOwner() {
        return null;
    }

    @Override
    public CompoundTag getDataStorage() {
        return blockEntity.getPeripheralSettings();
    }

    @Override
    public void putDataStorage(CompoundTag dataStorage) {
        blockEntity.setPeripheralSettings(dataStorage);
    }

    @Override
    public <U> U withPlayer(APFakePlayer.Action<U> function) throws LuaException {
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
        getLevel().removeBlock(blockEntity.getBlockPos(), false);
    }

    public BlockEntityPeripheralOwner<T> attachFuel() {
        attachAbility(PeripheralOwnerAbility.FUEL, new BlockEntityFuelAbility<>(this));
        return this;
    }

    @Override
    @Nullable
    public <U extends IPeripheral> U getConnectedPeripheral(Class<U> type) {
        throw new NotImplementedException();
    }
}
