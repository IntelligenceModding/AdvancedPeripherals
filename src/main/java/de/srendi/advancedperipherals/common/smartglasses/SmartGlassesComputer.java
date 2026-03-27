package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.impl.PocketUpgrades;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.TerminalSize;
import de.srendi.advancedperipherals.common.setup.APComputerComponents;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.ModulePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Basically just a {@link dan200.computercraft.shared.pocket.core.PocketServerComputer} but with some changes
 */
public class SmartGlassesComputer extends ServerComputer {

    @Nullable
    private Entity entity;
    private ItemStack stack = ItemStack.EMPTY;
    private final EnumMap<ComputerSide, SmartGlassesSideAccess> sideAccesses = new EnumMap<>(ComputerSide.class);
    @Nullable
    private SmartGlassesItemHandler itemHandler = null;

    private boolean peripheralOutdated = false;
    private boolean isDirty = true;

    @NotNull
    private final ModulePeripheral modulePeripheral;
    private final Map<Integer, IModule> modules = new HashMap<>();
    private DataComponentPatch moduleDatas;

    public SmartGlassesComputer(ServerLevel level, BlockPos pos, ServerComputer.Properties properties, DataComponentPatch moduleDatas) {
        super(
            level,
            pos,
            properties
                .terminalSize(new TerminalSize(39, 13))
                .addComponent(APComputerComponents.SMARTGLASSES, Boolean.TRUE)
        );
        this.modulePeripheral = new ModulePeripheral(this);
        this.moduleDatas = moduleDatas;
        for (ComputerSide side : ComputerSide.values()) {
            this.sideAccesses.put(side, new SmartGlassesSideAccess(side, this));
        }
        this.setPeripheral(ComputerSide.BACK, this.modulePeripheral);
    }

    @Nullable
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

    public void setStack(ItemStack stack) {
        this.stack = stack;
        this.invalidatePeripheral();
        this.isDirty = true;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setItemHandler(@Nullable SmartGlassesItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    @NotNull
    public DataComponentPatch getModulesData() {
        return this.moduleDatas;
    }

    public void setModulesData(DataComponentPatch data) {
        if (this.moduleDatas.equals(data)) {
            return;
        }
        this.moduleDatas = data;
        this.isDirty = true;
    }

    public UpgradeData<IPocketUpgrade> getUpgrade(@NotNull ComputerSide side) {
        if (this.itemHandler == null) {
            return null;
        }
        ItemStack stack = this.itemHandler.getStackInSlot(SmartGlassesSlot.sideToIndex(side));
        if (stack.isEmpty()) {
            return null;
        }
        RegistryAccess registryAccess = this.getLevel().registryAccess();
        return PocketUpgrades.instance().get(registryAccess, stack);
    }

    public void setUpgrade(@NotNull ComputerSide side, @Nullable UpgradeData<IPocketUpgrade> upgrade) {
        int slot = SmartGlassesSlot.sideToIndex(side);
        this.itemHandler.setStackInSlot(slot, upgrade == null ? ItemStack.EMPTY : upgrade.getUpgradeItem());
    }

    public void invalidatePeripheral() {
        this.peripheralOutdated = true;
    }

    private void updatePeripheralsAndModules(SmartGlassesItemHandler itemHandler) {
        RegistryAccess registryAccess = this.getLevel().registryAccess();
        for (int slot = 0; slot < SmartGlassesItemHandler.PERIPHERAL_SLOTS; slot++) {
            ComputerSide side = SmartGlassesSlot.indexToSide(slot);
            ItemStack peripheralItem = itemHandler.getStackInSlot(slot);
            UpgradeData<IPocketUpgrade> upgradeData = PocketUpgrades.instance().get(registryAccess, peripheralItem);
            if (upgradeData == null) {
                this.setPeripheral(side, null);
                continue;
            }
            IPocketUpgrade upgrade = upgradeData.upgrade();
            IPeripheral peripheral = upgrade.createPeripheral(this.sideAccesses.get(side));
            this.setPeripheral(side, peripheral);
        }
        SmartGlassesSideAccess smartGlassesModuleAccess = this.getSmartGlassesModuleAccess();
        for (int slot = SmartGlassesItemHandler.PERIPHERAL_SLOTS; slot < SmartGlassesItemHandler.SLOTS; slot++) {
            ItemStack moduleItem = itemHandler.getStackInSlot(slot);
            IModule oldModule = this.modules.get(slot);
            if (!moduleItem.isEmpty() && moduleItem.getItem() instanceof IModuleItem module) {
                IModule newModule = module.createModule(smartGlassesModuleAccess, moduleItem);
                if (oldModule != null) {
                    if (oldModule.getName().equals(newModule.getName())) {
                        continue;
                    }
                    oldModule.onUnequipped(smartGlassesModuleAccess);
                }
                this.modules.put(slot, newModule);
            } else if (oldModule != null) {
                oldModule.onUnequipped(smartGlassesModuleAccess);
                this.modules.remove(slot);
            }
        }
        this.modulePeripheral.updateModules();
        this.setPeripheral(ComputerSide.BACK, null);
        this.setPeripheral(ComputerSide.BACK, this.modulePeripheral);
        if (this.entity instanceof Player player) {
            player.getInventory().setChanged();
        }
    }

    @Override
    public void tickServer() {
        if (this.entity != null) {
            this.setPosition((ServerLevel) this.entity.level(), BlockPos.containing(this.entity.getEyePosition()));
        }

        super.tickServer();

        boolean shouldUpdateInventory = this.peripheralOutdated || this.isDirty;
        if (this.peripheralOutdated && this.itemHandler != null) {
            this.peripheralOutdated = false;
            this.updatePeripheralsAndModules(this.itemHandler);
        }
        if (this.isDirty) {
            this.isDirty = false;
            this.stack.set(APDataComponents.MODULE_DATAS.get(), this.moduleDatas);
        }
        if (shouldUpdateInventory && entity instanceof Player player) {
            player.getInventory().setChanged();
        }

        SmartGlassesSideAccess smartGlassesModuleAccess = this.getSmartGlassesModuleAccess();
        this.getModules().forEach(module -> module.tick(smartGlassesModuleAccess));
    }

    public void setEntity(@Nullable Entity entity) {
        if (this.entity == entity) {
            return;
        }
        this.entity = entity;
        if (entity == null) {
            return;
        }
        this.setPosition((ServerLevel) this.entity.level(), BlockPos.containing(this.entity.getEyePosition()));
    }

    public Collection<IModule> getModules() {
        return this.modules.values();
    }

    @Nullable
    public IModule getModuleBySlot(int slot) {
        return this.modules.get(slot);
    }

    @Nullable
    public <T extends IModule> T getModule(Class<T> moduleClass) {
        return this.getModules().stream().filter(moduleClass::isInstance).map(moduleClass::cast).findFirst().orElse(null);
    }

    @NotNull
    public SmartGlassesSideAccess getSmartGlassesUpgradeAccess(ComputerSide side) {
        return this.sideAccesses.get(side);
    }

    @NotNull
    public SmartGlassesSideAccess getSmartGlassesModuleAccess() {
        return this.sideAccesses.get(ComputerSide.BACK);
    }
}
