package de.srendi.advancedperipherals.common.blocks.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ARControllerPeripheral;
import de.srendi.advancedperipherals.common.argoggles.ARRenderAction;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.common.util.Constants.NBT;

public class ARControllerTileEntity extends PeripheralTileEntity<ARControllerPeripheral> {
	private static final String CANVAS = "canvas";
	private static final String VIRTUAL_SCREEN_SIZE = "virtual_screen_size";
	private Optional<int[]> virtualScreenSize = Optional.empty();
	private List<ARRenderAction> canvas = new ArrayList<>();

	public ARControllerTileEntity() {
		this(TileEntityTypes.AR_CONTROLLER.get());
	}

	public ARControllerTileEntity(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	/**
	 * Adds a rendering action to the canvas. Won't add an action if it is already
	 * present with exactly the same parameters, to avoid clutter.
	 * 
	 * @param action The action to add to the canvas.
	 */
	public void addToCanvas(ARRenderAction action) {
		if (canvas.contains(action))
			return;
		canvas.add(action);
		blockChanged();
	}

	public void clearCanvas() {
		canvas.clear();
		blockChanged();
	}

	@Override
	protected ARControllerPeripheral createPeripheral() {
		return new ARControllerPeripheral("arController", this);
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		read(nbt);
	}
	
	public void read(CompoundNBT nbt) {
		if (nbt.getIntArray(VIRTUAL_SCREEN_SIZE).length > 0)
			virtualScreenSize = Optional.of(nbt.getIntArray(VIRTUAL_SCREEN_SIZE));
		else
			virtualScreenSize = Optional.empty();
		ListNBT list = nbt.getList(CANVAS, NBT.TAG_COMPOUND);
		canvas.clear();
		for (int i = 0; i < list.size(); i++) {
			CompoundNBT c = list.getCompound(i);
			ARRenderAction action = new ARRenderAction();
			action.deserializeNBT(c);
			canvas.add(action);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (virtualScreenSize.isPresent())
			compound.putIntArray(VIRTUAL_SCREEN_SIZE, virtualScreenSize.get());
		else if (compound.contains(VIRTUAL_SCREEN_SIZE))
			compound.remove(VIRTUAL_SCREEN_SIZE);
		ListNBT list = new ListNBT();
		for (ARRenderAction action : canvas) {
			list.add(action.serializeNBT());
		}
		compound.put(CANVAS, list);
		return super.write(compound);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		write(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		read(state, tag);
		super.handleUpdateTag(state, tag);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		write(nbt);
		return new SUpdateTileEntityPacket(getPos(), -1, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		read(nbt);
	}

	public boolean isRelativeMode() {
		return virtualScreenSize.isPresent();
	}

	public int[] getVirtualScreenSize() {
		if (virtualScreenSize.isPresent())
			return virtualScreenSize.get();
		else
			return null;
	}

	public void setRelativeMode(int virtualScreenWidth, int virtualScreenHeight) {
		virtualScreenSize = Optional.of(new int[] { virtualScreenWidth, virtualScreenHeight });
		blockChanged();
	}

	public void disableRelativeMode() {
		virtualScreenSize = Optional.empty();
		blockChanged();
	}

	private void blockChanged() {
		markDirty();
		world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), BlockFlags.BLOCK_UPDATE);
	}

	/**
	 * Returns a copy of the canvas with the virtual screen size added. This method
	 * is intended for the HudOverlayHandler only.
	 */
	public List<ARRenderAction> getCanvas() {
		List<ARRenderAction> list = new ArrayList<>();
		for (ARRenderAction a : canvas) {
			ARRenderAction action = a.copyWithVirtualScreenSize(virtualScreenSize);
			list.add(action);
		}
		return list;
	}
}
