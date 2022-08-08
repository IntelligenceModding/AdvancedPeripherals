package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ARControllerPeripheral;
import de.srendi.advancedperipherals.common.argoggles.ARRenderAction;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ARControllerEntity extends PeripheralBlockEntity<ARControllerPeripheral> {
    private static final String CANVAS = "canvas";
    private static final String VIRTUAL_SCREEN_SIZE = "virtual_screen_size";
    private final List<ARRenderAction> canvas = new ArrayList<>();
    private Optional<int[]> virtualScreenSize = Optional.empty();

    public ARControllerEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.AR_CONTROLLER.get(), pos, state);
    }

    /**
     * Adds a rendering action to the canvas. Won't add an action if it is already
     * present with exactly the same parameters, to avoid clutter.
     *
     * @param action The action to add to the canvas.
     */
    public void addToCanvas(ARRenderAction action) {
        if (canvas.contains(action)) return;
        if (action.getId() != null) {
            canvas.removeIf(old -> action.getId().equals(old.getId()));
        }
        canvas.add(action);
        blockChanged();
    }

    public void clearCanvas() {
        canvas.clear();
        blockChanged();
    }

    public void clearElement(String id) {
        canvas.removeIf(old -> id.equals(old.getId()));
        blockChanged();
    }

    @NotNull
    @Override
    protected ARControllerPeripheral createPeripheral() {
        return new ARControllerPeripheral(this);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        deserializeNBT(nbt);
    }

    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.getIntArray(VIRTUAL_SCREEN_SIZE).length > 0)
            virtualScreenSize = Optional.of(nbt.getIntArray(VIRTUAL_SCREEN_SIZE));
        else virtualScreenSize = Optional.empty();
        ListTag list = nbt.getList(CANVAS, Tag.TAG_COMPOUND);
        canvas.clear();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag c = list.getCompound(i);
            ARRenderAction action = new ARRenderAction();
            action.deserializeNBT(c);
            canvas.add(action);
        }
    }

    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        if (virtualScreenSize.isPresent()) compound.putIntArray(VIRTUAL_SCREEN_SIZE, virtualScreenSize.get());
        else if (compound.contains(VIRTUAL_SCREEN_SIZE)) compound.remove(VIRTUAL_SCREEN_SIZE);
        ListTag list = new ListTag();
        for (ARRenderAction action : new ArrayList<>(canvas)) {
            list.add(action.serializeNBT());
        }
        compound.put(CANVAS, list);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        deserializeNBT(tag);
        super.handleUpdateTag(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbt = new CompoundTag();
        saveAdditional(nbt);
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        deserializeNBT(nbt);
    }

    public boolean isRelativeMode() {
        return virtualScreenSize.isPresent();
    }

    public int[] getVirtualScreenSize() {
        return virtualScreenSize.orElse(null);
    }

    public void setRelativeMode(int virtualScreenWidth, int virtualScreenHeight) {
        virtualScreenSize = Optional.of(new int[]{virtualScreenWidth, virtualScreenHeight});
        blockChanged();
    }

    public void disableRelativeMode() {
        virtualScreenSize = Optional.empty();
        blockChanged();
    }

    private void blockChanged() {
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    /**
     * Returns a copy of the canvas with the virtual screen size added.
     */
    public List<ARRenderAction> getCanvas() {
        List<ARRenderAction> list = new ArrayList<>();
        for (ARRenderAction a : canvas) {
            ARRenderAction action = a.copyWithVirtualScreenSize(virtualScreenSize);
            list.add(action);
        }
        return list;
    }

    @Override
    public void markSettingsChanged() {

    }
}
