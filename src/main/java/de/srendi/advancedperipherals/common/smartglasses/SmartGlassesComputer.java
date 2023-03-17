package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.impl.PocketUpgrades;
import dan200.computercraft.shared.common.IColouredItem;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Basically just a {@link dan200.computercraft.shared.pocket.core.PocketServerComputer} but with some changes
 */
public class SmartGlassesComputer extends ServerComputer implements IPocketAccess {

    private @Nullable Entity entity;
    private ItemStack stack = ItemStack.EMPTY;
    private final SmartGlassesAccess smartGlassesAccess = new SmartGlassesAccess(this);

    private int lightColour = -1;
    private boolean lightChanged = false;

    private final Set<ServerPlayer> tracking = new HashSet<>();

    public SmartGlassesComputer(ServerLevel world, BlockPos position, int computerID, @Nullable String label, ComputerFamily family) {
        super(world, position, computerID, label, family, 39, 13);
    }

    @Nullable
    @Override
    public Entity getEntity() {
        var entity = this.entity;
        if (entity == null || stack.isEmpty() || !entity.isAlive()) return null;

        if (entity instanceof Player) {
            var inventory = ((Player) entity).getInventory();
            return inventory.items.contains(stack) || inventory.offhand.contains(stack) ? entity : null;
        } else if (entity instanceof LivingEntity living) {
            return living.getMainHandItem() == stack || living.getOffhandItem() == stack ? entity : null;
        } else if (entity instanceof ItemEntity itemEntity) {
            return itemEntity.getItem() == stack ? entity : null;
        } else {
            return null;
        }
    }

    @Override
    public int getColour() {
        return IColouredItem.getColourBasic(stack);
    }

    @Override
    public void setColour(int colour) {
        IColouredItem.setColourBasic(stack, colour);
        updateUpgradeNBTData();
    }

    @Override
    public int getLight() {
        return lightColour;
    }

    @Override
    public void setLight(int colour) {
        if (colour < 0 || colour > 0xFFFFFF) colour = -1;

        if (lightColour == colour) return;
        lightColour = colour;
        lightChanged = true;
    }

    @Override
    public CompoundTag getUpgradeNBTData() {
        return new CompoundTag();
    }

    @Override
    public void updateUpgradeNBTData() {
        if (entity instanceof Player player) player.getInventory().setChanged();
    }

    @Override
    public void invalidatePeripheral() {
    }

    @Override
    public Map<ResourceLocation, IPeripheral> getUpgrades() {
        return Collections.emptyMap();
    }

    public void updatePeripherals(@NotNull ItemStack glasses, IItemHandler itemHandler) {
        for (int slot = 0; slot < 6; slot++) {
            ItemStack peripheralItem = itemHandler.getStackInSlot(slot);
            if (!peripheralItem.isEmpty()) {
                IPocketUpgrade upgrade = PocketUpgrades.instance().get(peripheralItem);
                if (upgrade != null) {
                    IPeripheral peripheral = upgrade.createPeripheral(smartGlassesAccess);
                    if (peripheral != null)
                        setPeripheral(SmartGlassesSlot.indexToSide(slot), peripheral);
                }
            }
        }
    }

    public synchronized void updateValues(@Nullable Entity entity, ItemStack stack, @Nullable IPocketUpgrade upgrade) {
        if (entity != null) {
            setLevel((ServerLevel) entity.getCommandSenderWorld());
            setPosition(entity.blockPosition());
        }

        // If a new entity has picked it up then rebroadcast the terminal to them
        if (entity != this.entity && entity instanceof ServerPlayer) markTerminalChanged();

        this.entity = entity;
        this.stack = stack;

        invalidatePeripheral();

    }

    @Override
    public void tickServer() {
        super.tickServer();

        // Find any players which have gone missing and remove them from the tracking list.
        tracking.removeIf(player -> !player.isAlive() || player.level != getLevel());

        // And now find any new players, add them to the tracking list, and broadcast state where appropriate.
        var sendState = hasOutputChanged() || lightChanged;
        lightChanged = false;
        if (sendState) {
            // Broadcast the state to all players
            tracking.addAll(getLevel().players());
        } else {
            // Broadcast the state to new players.
            List<ServerPlayer> added = new ArrayList<>();
            for (var player : getLevel().players()) {
                if (tracking.add(player)) added.add(player);
            }
        }
    }

    @Override
    protected void onTerminalChanged() {
        super.onTerminalChanged();

        /*if (entity instanceof ServerPlayer player && entity.isAlive()) {
            // Broadcast the terminal to the current player.
        }*/
    }

    @Override
    protected void onRemoved() {
        super.onRemoved();
    }

}
