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

    public static final String UPGRADE_DATAS_TAG = "UpgradeDatas";

    @Nullable
    private Entity entity;
    private ItemStack stack = ItemStack.EMPTY;
    private final SmartGlassesAccess smartGlassesAccess = new SmartGlassesAccess(this);
    @Nullable
    private SmartGlassesItemHandler itemHandler = null;
    @NotNull
    private final ModulePeripheral modulePeripheral;
    private final CompoundTag upgradeDatas;

    private boolean peripheralOutdated = false;
    private boolean isDirty = true;

    private Map<ResourceLocation, IPeripheral> upgrades = Collections.emptyMap();
    private final Map<Integer, IModule> modules = new HashMap<>();

    public SmartGlassesComputer(ServerLevel world, int computerID, @Nullable String label, ComputerFamily family, @NotNull CompoundTag upgradeDatas) {
        super(world, computerID, label, family, 39, 13);
        this.addAPI(new SmartGlassesAPI());
        this.modulePeripheral = new ModulePeripheral(this);
        this.upgradeDatas = upgradeDatas;
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
        return this.entity == null ? super.getPosition() : new BlockPos(this.entity.getEyePosition());
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
        this.invalidatePeripheral();
        this.updateUpgradeNBTData();
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

    @Override
    @NotNull
    public CompoundTag getUpgradeNBTData() {
        return this.upgradeDatas;
    }

    public void setUpgradeData(@NotNull ComputerSide side, @NotNull ResourceLocation id, @NotNull CompoundTag data) {
        data.putString("UpgradeSide", side.getName());
        this.upgradeDatas.put(id.toString(), data);
        this.updateUpgradeNBTData();
    }

    public void removeUpgradeData(@NotNull ComputerSide side) {
        for (String id : this.upgradeDatas.getAllKeys()) {
            if (side.getName().equals(this.upgradeDatas.getCompound(id).getString("UpgradeSide"))) {
                this.upgradeDatas.remove(id);
                this.updateUpgradeNBTData();
                return;
            }
        }
    }

    @Override
    public void updateUpgradeNBTData() {
        this.isDirty = true;
    }

    @Override
    public void invalidatePeripheral() {
        this.peripheralOutdated = true;
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

    private void updatePeripheralsAndModules(SmartGlassesItemHandler itemHandler) {
        Set<ResourceLocation> upgradesIdSet = new HashSet<>();
        ImmutableMap.Builder<ResourceLocation, IPeripheral> upgradesBuilder = new ImmutableMap.Builder<>();
        for (int slot = 0; slot < SmartGlassesItemHandler.PERIPHERAL_SLOTS; slot++) {
            ComputerSide side = SmartGlassesSlot.indexToSide(slot);
            ItemStack peripheralItem = itemHandler.getStackInSlot(slot);
            IPocketUpgrade upgrade = PocketUpgrades.instance().get(peripheralItem);
            IPeripheral peripheral = upgrade != null ? upgrade.createPeripheral(smartGlassesAccess) : null;
            setPeripheral(side, peripheral);
            if (peripheral != null) {
                ResourceLocation id = upgrade.getUpgradeID();
                if (upgradesIdSet.add(id)) {
                    upgradesBuilder.put(id, peripheral);
                    setUpgradeData(side, id, this.upgradeDatas.getCompound(id.toString()));
                    continue;
                }
            }
            removeUpgradeData(side);
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

        boolean shouldUpdateInventory = this.peripheralOutdated || this.isDirty;
        if (this.peripheralOutdated && this.itemHandler != null) {
            this.peripheralOutdated = false;
            this.updatePeripheralsAndModules(this.itemHandler);
        }
        if (this.isDirty) {
            this.isDirty = false;
            CompoundTag data = this.stack.getOrCreateTag();
            data.put(UPGRADE_DATAS_TAG, this.upgradeDatas.copy());
        }
        if (shouldUpdateInventory && entity instanceof Player player) {
            player.getInventory().setChanged();
        }

        this.modules.values().forEach(module -> {
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
        this.setPosition(new BlockPos(this.entity.getEyePosition()));
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
