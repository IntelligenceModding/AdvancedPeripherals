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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Basically just a {@link dan200.computercraft.shared.pocket.core.PocketServerComputer} but with some changes
 */
public class SmartGlassesComputer extends ServerComputer implements IPocketAccess {

    @Nullable
    private Entity entity;
    private ItemStack stack = ItemStack.EMPTY;
    private final SmartGlassesAccess smartGlassesAccess = new SmartGlassesAccess(this);
    @Nullable
    private SmartGlassesItemHandler itemHandler = null;
    @NotNull
    private final ModulePeripheral modulePeripheral;

    private boolean isDirty = true;

    private Map<ResourceLocation, IPeripheral> upgrades = Collections.emptyMap();
    private final Map<Integer, IModule> modules = new HashMap<>();

    public SmartGlassesComputer(ServerLevel world, int computerID, @Nullable String label, ComputerFamily family) {
        super(world, computerID, label, family, 39, 13);
        this.addAPI(new SmartGlassesAPI());
        this.modulePeripheral = new ModulePeripheral(this);
        this.setPeripheral(ComputerSide.BACK, this.modulePeripheral);
    }

    @Nullable
    @Override
    public Entity getEntity() {
        if (stack.isEmpty() || entity == null || !entity.isAlive()) {
            return null;
        }

        if (entity instanceof Player player) {
            Inventory inventory = player.getInventory();
            if (inventory.contains(stack)) {
                return player;
            }
            return null;
        }
        if (entity instanceof ItemEntity itemEntity) {
            return itemEntity.getItem() == stack ? entity : null;
        }
        return null;
    }

    @Override
    public ServerLevel getLevel() {
        return this.entity == null ? super.getLevel() : (ServerLevel) this.entity.getCommandSenderWorld();
    }

    @Override
    public BlockPos getPosition() {
        return this.entity == null ? super.getPosition() : this.entity.blockPosition();
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
        return 0;
    }

    @Override
    public void setLight(int colour) {
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
        if (entity instanceof Player player) {
            player.getInventory().setChanged();
        }
    }

    @Override
    public void invalidatePeripheral() {
        updatePeripheralsAndModules(this.itemHandler);
    }

    @Override
    @NotNull
    public Map<ResourceLocation, IPeripheral> getUpgrades() {
        return this.upgrades;
    }

    @Override
    public void setPeripheral(ComputerSide side, IPeripheral peripheral) {
        super.setPeripheral(side, peripheral);
    }

    public void updatePeripheralsAndModules(SmartGlassesItemHandler itemHandler) {
        Set<ResourceLocation> upgradesIdSet = new HashSet<>();
        ImmutableMap.Builder<ResourceLocation, IPeripheral> upgradesBuilder = new ImmutableMap.Builder<>();
        for (int slot = 0; slot < SmartGlassesItemHandler.PERIPHERAL_SLOTS; slot++) {
            ComputerSide side = SmartGlassesSlot.indexToSide(slot);
            ItemStack peripheralItem = itemHandler.getStackInSlot(slot);
            IPocketUpgrade upgrade = PocketUpgrades.instance().get(peripheralItem);
            IPeripheral peripheral = upgrade != null ? upgrade.createPeripheral(smartGlassesAccess) : null;
            setPeripheral(side, peripheral);
            if (peripheral != null && upgradesIdSet.add(upgrade.getUpgradeID())) {
                upgradesBuilder.put(upgrade.getUpgradeID(), peripheral);
            }
        }
        this.upgrades = upgradesBuilder.build();
        for (int slot = SmartGlassesItemHandler.PERIPHERAL_SLOTS; slot < SmartGlassesItemHandler.SLOTS; slot++) {
            ItemStack peripheralItem = itemHandler.getStackInSlot(slot);
            IModule oldModule = modules.get(slot);
            if (!peripheralItem.isEmpty() && peripheralItem.getItem() instanceof IModuleItem module) {
                IModule newModule = module.createModule(smartGlassesAccess);
                if (oldModule != null && oldModule.getName().equals(newModule.getName())) {
                    continue;
                }
                modules.put(slot, newModule);
            } else if (oldModule != null) {
                oldModule.onUnequipped(smartGlassesAccess);
                modules.remove(slot);
            }
        }
        this.modulePeripheral.updateModules();
        setPeripheral(ComputerSide.BACK, null);
        setPeripheral(ComputerSide.BACK, this.modulePeripheral);
        if (this.entity instanceof Player player) {
            player.getInventory().setChanged();
        }
    }

    @Override
    public void tickServer() {
        super.tickServer();

        if (isDirty()) {
            invalidatePeripheral();
            isDirty = false;
        }

        modules.values().forEach(module -> {
            module.tick(smartGlassesAccess);
        });
    }

    public void setEntity(@Nullable Entity entity) {
        if (this.entity == entity) {
            return;
        }
        this.entity = entity;
        if (entity == null) {
            return;
        }
        this.setLevel((ServerLevel) this.entity.getCommandSenderWorld());
        this.setPosition(this.entity.blockPosition());
    }

    public Map<Integer, IModule> getModules() {
        return modules;
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
