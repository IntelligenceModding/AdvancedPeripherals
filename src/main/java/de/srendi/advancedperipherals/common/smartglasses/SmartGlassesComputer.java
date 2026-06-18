package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.impl.PocketUpgrades;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.TerminalSize;
import de.srendi.advancedperipherals.common.component.ItemStackStorage;
import de.srendi.advancedperipherals.common.items.SmartGlassesItem;
import de.srendi.advancedperipherals.common.setup.APComputerComponents;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.ModulePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

/**
 * Basically just a {@link dan200.computercraft.shared.pocket.core.PocketServerComputer} but with some changes
 */
public class SmartGlassesComputer extends ServerComputer {

    private ItemStack stack;
    @Nullable
    private Entity entity;
    private final SmartGlassesSideAccess[] sideAccesses = new SmartGlassesSideAccess[ComputerSide.values().length];
    private final UpgradeData<IPocketUpgrade>[] upgrades = new UpgradeData[SmartGlassesSlot.PERIPHERAL_SLOTS];
    private final ModulePeripheral modulePeripheral = new ModulePeripheral(this);
    private final NonNullList<ItemStack> moduleItems = NonNullList.withSize(SmartGlassesSlot.MODULE_SLOTS, ItemStack.EMPTY);
    private final IModule[] modules = new IModule[SmartGlassesSlot.MODULE_SLOTS];
    private DataComponentPatch moduleDatas;

    private volatile boolean upgradesUpdated = false;
    private volatile boolean modulesUpdated = false;
    private volatile boolean moduleDatasUpdated = false;

    protected SmartGlassesComputer(ServerLevel level, BlockPos pos, ServerComputer.Properties properties, ItemStack stack) {
        super(level, pos, properties);
        this.stack = stack;
        this.moduleDatas = stack.getOrDefault(APDataComponents.MODULE_DATAS.get(), DataComponentPatch.EMPTY);
        for (ComputerSide side : ComputerSide.values()) {
            this.sideAccesses[side.ordinal()] = new SmartGlassesSideAccess(side, this);
        }

        RegistryAccess registryAccess = level.registryAccess();
        ItemStackStorage items = SmartGlassesItemHandler.loadItems(stack);
        // build upgrades
        for (ComputerSide side : SmartGlassesSlot.UPGRADE_SIDES) {
            int slot = SmartGlassesSlot.sideToIndex(side);
            ItemStack upgradeStack = items.getAllUnsafe()[slot];
            UpgradeData<IPocketUpgrade> upgradeData = PocketUpgrades.instance().get(registryAccess, upgradeStack);
            this.upgrades[slot] = upgradeData;
            if (upgradeData == null) {
                this.setPeripheral(side, null);
                continue;
            }
            IPeripheral peripheral = upgradeData.upgrade().createPeripheral(this.getSmartGlassesUpgradeAccess(side));
            this.setPeripheral(side, peripheral);
        }
        // build modules
        SmartGlassesSideAccess smartGlassesModuleAccess = this.getSmartGlassesModuleAccess();
        for (int slot = 0; slot < SmartGlassesSlot.MODULE_SLOTS; slot++) {
            ItemStack moduleStack = items.get(slot + SmartGlassesSlot.MODULE_SLOT_OFFSET);
            this.moduleItems.set(slot, moduleStack);
            if (moduleStack.getItem() instanceof IModuleItem moduleItem) {
                IModule newModule = moduleItem.createModule(smartGlassesModuleAccess);
                this.modules[slot] = newModule;
            }
        }
        this.modulePeripheral.updateModules(Stream.of(this.modules).filter(Objects::nonNull).toList());
        this.setPeripheral(ComputerSide.BACK, this.modulePeripheral);
    }

    public static SmartGlassesComputer create(ServerLevel level, BlockPos pos, ServerComputer.Properties properties, ItemStack stack) {
        IsEquippedWrapper wrapper = new IsEquippedWrapper();
        SmartGlassesComputer computer = new SmartGlassesComputer(
            level,
            pos,
            properties
                .terminalSize(new TerminalSize(39, 13))
                .addComponent(APComputerComponents.SMARTGLASSES_EQUIPPED, wrapper),
            stack
        );
        wrapper.computer = computer;
        return computer;
    }

    @Nullable
    public Entity getEntity() {
        Entity entity = this.entity;
        if (entity == null || !entity.isAlive()) {
            return null;
        }
        return entity;
    }

    public boolean isEquipped() {
        Entity entity = this.getEntity();
        if (entity == null || !(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        return SmartGlassesItem.getEquipped(livingEntity) == this.stack;
    }

    public boolean updateStack(ItemStack stack) {
        boolean changed = false;
        this.stack = stack;
        ItemStackStorage items = SmartGlassesItemHandler.loadItems(stack);
        if (this.upgradesUpdated) {
            this.upgradesUpdated = false;
            changed = true;
            synchronized (this.upgrades) {
                for (int slot = 0; slot < SmartGlassesSlot.PERIPHERAL_SLOTS; slot++) {
                    UpgradeData<IPocketUpgrade> upgrade = this.upgrades[slot];
                    items = items.set(slot, upgrade == null ? ItemStack.EMPTY : upgrade.getUpgradeItem());
                }
            }
        }
        if (this.modulesUpdated) {
            this.modulesUpdated = false;
            changed = true;
            for (int slot = 0; slot < SmartGlassesSlot.MODULE_SLOTS; slot++) {
                ItemStack moduleStack = this.moduleItems.get(slot);
                items = items.set(slot + SmartGlassesSlot.MODULE_SLOT_OFFSET, moduleStack);
            }
        }
        if (changed) {
            SmartGlassesItemHandler.saveItems(stack, items);
        }
        if (this.moduleDatasUpdated) {
            this.moduleDatasUpdated = false;
            changed = true;
            stack.set(APDataComponents.MODULE_DATAS.get(), this.moduleDatas);
        }
        return changed;
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
        this.moduleDatasUpdated = true;
    }

    public UpgradeData<IPocketUpgrade> getUpgrade(@NotNull ComputerSide side) {
        synchronized (this.upgrades) {
            return this.upgrades[SmartGlassesSlot.sideToIndex(side)];
        }
    }

    public void setUpgrade(@NotNull ComputerSide side, @Nullable UpgradeData<IPocketUpgrade> upgradeData) {
        int slot = SmartGlassesSlot.sideToIndex(side);
        UpgradeData<IPocketUpgrade> oldUpgrade;
        synchronized (this.upgrades) {
            oldUpgrade = this.upgrades[slot];
            if (Objects.equals(oldUpgrade, upgradeData)) {
                return;
            }
            this.upgrades[slot] = upgradeData;
        }
        this.upgradesUpdated = true;
        if (upgradeData == null) {
            this.setPeripheral(side, null);
            return;
        }
        IPocketUpgrade upgrade = upgradeData.upgrade();
        if (oldUpgrade != null && oldUpgrade.upgrade() == upgrade) {
            return;
        }
        IPeripheral peripheral = upgrade.createPeripheral(this.getSmartGlassesUpgradeAccess(side));
        this.setPeripheral(side, peripheral);
    }

    public DataComponentPatch getUpgradeData(@NotNull ComputerSide side) {
        UpgradeData<IPocketUpgrade> upgradeData = this.getUpgrade(side);
        return upgradeData == null ? DataComponentPatch.EMPTY : upgradeData.data();
    }

    public void setUpgradeData(@NotNull ComputerSide side, DataComponentPatch data) {
        int slot = SmartGlassesSlot.sideToIndex(side);
        synchronized (this.upgrades) {
            UpgradeData<IPocketUpgrade> upgradeData = this.upgrades[slot];
            if (upgradeData == null) {
                return;
            }
            if (upgradeData.data().equals(data)) {
                return;
            }
            this.upgrades[slot] = UpgradeData.of(upgradeData.holder(), data);
        }
        this.upgradesUpdated = true;
    }

    public void invalidatePeripheral(@NotNull ComputerSide side) {
        UpgradeData<IPocketUpgrade> upgradeData = this.getUpgrade(side);
        if (upgradeData == null) {
            return;
        }
        IPeripheral peripheral = upgradeData.upgrade().createPeripheral(this.getSmartGlassesUpgradeAccess(side));
        this.setPeripheral(side, peripheral);
    }

    public ItemStack getModuleStack(int slot) {
        return this.moduleItems.get(slot);
    }

    public void setModuleStack(int slot, ItemStack stack) {
        this.moduleItems.set(slot, stack);
        this.modulesUpdated = true;

        boolean isEquipped = this.isEquipped();

        SmartGlassesSideAccess smartGlassesModuleAccess = this.getSmartGlassesModuleAccess();
        IModule oldModule = this.modules[slot];
        if (stack.getItem() instanceof IModuleItem moduleItem) {
            IModule newModule = moduleItem.createModule(smartGlassesModuleAccess);
            if (oldModule != null) {
                if (oldModule.getId().equals(newModule.getId())) {
                    return;
                }
                this.queueEvent(CCEvents.GLASSES_MODULE_DETACH, new Object[]{oldModule.getId().toString()});
                if (isEquipped) {
                    oldModule.onUnequipped(smartGlassesModuleAccess);
                }
            }
            this.queueEvent(CCEvents.GLASSES_MODULE, new Object[]{newModule.getId().toString()});
            this.modules[slot] = newModule;
        } else if (oldModule != null) {
            this.queueEvent(CCEvents.GLASSES_MODULE_DETACH, new Object[]{oldModule.getId().toString()});
            if (isEquipped) {
                oldModule.onUnequipped(smartGlassesModuleAccess);
            }
            this.modules[slot] = null;
        }
        this.modulePeripheral.updateModules(Stream.of(this.modules).filter(Objects::nonNull).toList());
    }

    @Override
    public void tickServer() {
        Entity entity = this.entity;
        if (entity != null) {
            this.setPosition((ServerLevel) entity.level(), BlockPos.containing(entity.getEyePosition()));
        }

        super.tickServer();

        SmartGlassesSideAccess smartGlassesModuleAccess = this.getSmartGlassesModuleAccess();
        for (IModule module : this.modules) {
            if (module != null) {
                module.serverTick(smartGlassesModuleAccess);
            }
        }
    }

    public void setEntity(@Nullable Entity entity) {
        if (this.entity == entity) {
            return;
        }
        this.entity = entity;
        if (entity == null) {
            return;
        }
        this.setPosition((ServerLevel) entity.level(), BlockPos.containing(entity.getEyePosition()));
    }

    @Nullable
    public IModule getModuleBySlot(int slot) {
        return this.modules[slot];
    }

    @Nullable
    public <T extends IModule> T getModule(Class<T> moduleClass) {
        for (IModule module : this.modules) {
            if (module != null && module.getClass() == moduleClass) {
                return (T) module;
            }
        }
        return null;
    }

    @NotNull
    public SmartGlassesSideAccess getSmartGlassesUpgradeAccess(ComputerSide side) {
        checkUpgradeSide(side);
        return this.sideAccesses[side.ordinal()];
    }

    @NotNull
    public SmartGlassesSideAccess getSmartGlassesModuleAccess() {
        return this.sideAccesses[ComputerSide.BACK.ordinal()];
    }

    private static void checkUpgradeSide(ComputerSide side) throws IllegalArgumentException {
        if (side == ComputerSide.BACK) {
            throw new IllegalArgumentException("upgrade side cannot be BACK");
        }
    }

    private static final class IsEquippedWrapper implements BooleanSupplier {
        private SmartGlassesComputer computer = null;

        @Override
        public boolean getAsBoolean() {
            return this.computer != null && this.computer.isEquipped();
        }
    }
}
