package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.PocketUpgrades;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.ModulePeripheral;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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

    @Nullable
    private Entity entity;
    private ItemStack stack = ItemStack.EMPTY;
    private final SmartGlassesAccess smartGlassesAccess = new SmartGlassesAccess(this);
    @Nullable
    private SmartGlassesItemHandler itemHandler;

    private int lightColour = -1;
    private boolean lightChanged = false;
    private boolean isDirty = false;

    private final Set<ServerPlayer> tracking = new HashSet<>();
    private final Map<Integer, IModule> modules = new HashMap<>();

    public SmartGlassesComputer(ServerLevel world, int computerID, @Nullable String label, ComputerFamily family) {
        super(world, computerID, label, family, 39, 13);
    }

    @Nullable
    @Override
    public Entity getEntity() {
        if (entity == null || stack.isEmpty() || !entity.isAlive())
            return null;

        if (entity instanceof Player player) {
            var inventory = player.getInventory();
            return inventory.items.contains(stack) || inventory.armor.contains(stack) || inventory.offhand.contains(stack) ? entity : null;
        } else if (entity instanceof ItemEntity itemEntity) {
            return itemEntity.getItem() == stack ? entity : null;
        } else {
            return null;
        }
    }

    @Override
    public int getColour() {
        return 0;
    }

    @Override
    public void setColour(int colour) {
        // We don't have a color.
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public int getLight() {
        return lightColour;
    }

    @Override
    public void setLight(int colour) {
        if (colour < 0 || colour > 0xFFFFFF) colour = -1;

        if (lightColour == colour)
            return;
        lightColour = colour;
        lightChanged = true;
    }

    public void setItemHandler(@Nullable SmartGlassesItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    public void markDirty() {
        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    @Override
    @NotNull
    public CompoundTag getUpgradeNBTData() {
        return new CompoundTag();
    }

    @Override
    public void updateUpgradeNBTData() {
        if (entity instanceof Player player)
            player.getInventory().setChanged();
    }

    @Override
    public void invalidatePeripheral() {
    }

    @Override
    @NotNull
    public Map<ResourceLocation, IPeripheral> getUpgrades() {
        return Collections.emptyMap();
    }

    public void updatePeripheralsAndModules(IItemHandler itemHandler) {
        for (int slot = 0; slot < 4; slot++) {
            ItemStack peripheralItem = itemHandler.getStackInSlot(slot);
            if (!peripheralItem.isEmpty()) {
                IPocketUpgrade upgrade = PocketUpgrades.instance().get(peripheralItem);
                if (upgrade != null) {
                    IPeripheral peripheral = upgrade.createPeripheral(smartGlassesAccess);
                    if (peripheral != null) {
                        setPeripheral(SmartGlassesSlot.indexToSide(slot), peripheral);
                        continue;
                    }
                }
            }
            setPeripheral(SmartGlassesSlot.indexToSide(slot), null);
        }
        for (int slot = 4; slot < 11; slot++) {
            ItemStack peripheralItem = itemHandler.getStackInSlot(slot);
            if (!peripheralItem.isEmpty() && peripheralItem.getItem() instanceof IModuleItem module) {
                if (modules.get(slot) != null && modules.get(slot).getName().equals(module.createModule(smartGlassesAccess).getName()))
                    continue;

                modules.put(slot, module.createModule(smartGlassesAccess));
            } else {
                if (modules.get(slot) != null) {
                    modules.get(slot).onUnequipped(smartGlassesAccess);
                    modules.remove(slot);
                }
            }
        }
        setPeripheral(ComputerSide.BACK, new ModulePeripheral(this));
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
            tracking.addAll(getLevel().players());
        }

        modules.values().forEach(module -> module.tick(smartGlassesAccess));

        if (isDirty())
            updatePeripheralsAndModules(itemHandler);

        isDirty = false;
    }

    public void setEntity(@Nullable Entity entity) {
        this.entity = entity;
    }

    public Map<Integer, IModule> getModules() {
        return modules;
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

    @NotNull
    public SmartGlassesAccess getSmartGlassesAccess() {
        return smartGlassesAccess;
    }
}
