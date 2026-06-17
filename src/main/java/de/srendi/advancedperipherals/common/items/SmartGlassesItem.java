package de.srendi.advancedperipherals.common.items;

import java.util.function.Predicate;

import com.google.common.base.Objects;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.items.ServerComputerReference;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.util.DataComponentUtil;
import dan200.computercraft.shared.util.IDAssigner;
import dan200.computercraft.shared.util.NonNegativeId;
import dan200.computercraft.shared.util.StorageCapacity;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.curios.SmartGlassesCurio;
import de.srendi.advancedperipherals.common.component.ItemStackStorage;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesItemHandler;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesMenuProvider;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSlot;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class SmartGlassesItem extends ArmorItem {

    public SmartGlassesItem(Holder<ArmorMaterial> material) {
        super(material, ArmorItem.Type.HELMET, new Properties().stacksTo(1));
    }

    public IItemHandler createItemHandlerCap(ItemStack stack) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }
        SmartGlassesComputer computer = getServerComputer(server, stack);
        if (computer == null) {
            return null;
        }
        return new SmartGlassesItemHandler(stack, computer);
    }

    public Object createCurioCap(ItemStack stack) {
        return new SmartGlassesCurio(this, stack);
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        if (!super.canEquip(stack, armorType, entity)) {
            return false;
        }
        if (!getEquippedCurios(entity).isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> swapWithEquipmentSlot(Item item, Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!item.canEquip(stack, EquipmentSlot.HEAD, player)) {
            return InteractionResultHolder.pass(stack);
        }
        return super.swapWithEquipmentSlot(item, level, player, hand);
    }

    private boolean postInventoryTick(ItemStack stack, ServerLevel level, Entity entity, SmartGlassesComputer computer) {
        computer.setPosition(level, entity != null ? entity.blockPosition() : computer.getPosition());

        boolean changed = false;

        // Sync ID
        int id = computer.getID();
        if (id != getComputerID(stack)) {
            changed = true;
            stack.set(ModRegistry.DataComponents.COMPUTER_ID.get(), NonNegativeId.of(id));
        }

        // Sync label
        String label = computer.getLabel();
        if (!Objects.equal(label, getLabel(stack))) {
            changed = true;
            setLabel(stack, label);
        }

        boolean on = computer.isOn();
        if (on != isMarkedOn(stack)) {
            changed = true;
            stack.set(ModRegistry.DataComponents.ON.get(), on);
        }

        Entity computerEntity = computer.getEntity();
        if (computerEntity != entity) {
            changed = true;
            computer.setEntity(entity);
        }

        if (computer.updateStack(stack)) {
            changed = true;
        }

        return changed;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotNum, boolean selected) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        SmartGlassesComputer computer = null;
        if (level instanceof ServerLevel serverLevel) {
            computer = getOrCreateComputer(serverLevel, livingEntity, stack);
            computer.keepAlive();

            for (ComputerSide side : SmartGlassesSlot.UPGRADE_SIDES) {
                SmartGlassesSideAccess access = computer.getSmartGlassesUpgradeAccess(side);
                UpgradeData<IPocketUpgrade> upgrade = access.getUpgrade();
                if (upgrade != null) {
                    upgrade.upgrade().update(access, computer.getPeripheral(side));
                }
            }
        }
        if (livingEntity.getItemBySlot(EquipmentSlot.HEAD) == stack) {
            this.onEquippedTick(stack, level, livingEntity);
        }
    }

    public void onEquippedTick(ItemStack stack, Level level, LivingEntity entity) {
        SmartGlassesComputer computer = null;
        if (level instanceof ServerLevel serverLevel) {
            computer = getOrCreateComputer(serverLevel, entity, stack);
        }

        ItemStackStorage items = SmartGlassesItemHandler.loadItems(stack);

        for (int slot = 0; slot < SmartGlassesSlot.MODULE_SLOTS; slot++) {
            Item item = items.getItem(slot + SmartGlassesSlot.MODULE_SLOT_OFFSET);
            if (!(item instanceof IModuleItem moduleItem)) {
                continue;
            }
            SmartGlassesSideAccess glassesAccess = null;
            IModule module = null;
            if (computer != null) {
                glassesAccess = computer.getSmartGlassesModuleAccess();
                module = computer.getModuleBySlot(slot);
            }
            moduleItem.moduleTick(level, entity, slot, glassesAccess, module);
        }
        if (computer != null && postInventoryTick(stack, (ServerLevel) level, entity, computer) && entity instanceof Player player) {
            player.getInventory().setChanged();
        }
    }

    public void onUnequip(ItemStack stack, ServerLevel level, LivingEntity entity) {
        SmartGlassesComputer computer = getServerComputer(level.getServer(), stack);
        if (computer == null) {
            return;
        }

        SmartGlassesSideAccess smartGlassesModuleAccess = computer.getSmartGlassesModuleAccess();
        for (int i = 0; i < SmartGlassesSlot.MODULE_SLOTS; i++) {
            IModule module = computer.getModuleBySlot(i);
            if (module != null) {
                module.onUnequipped(smartGlassesModuleAccess);
            }
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        SmartGlassesComputer computer = getServerComputer(serverLevel.getServer(), stack);
        if (computer != null && postInventoryTick(stack, serverLevel, entity, computer)) {
            entity.setItem(stack.copy());
        }
        return false;
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.isSecondaryUseActive()) {
            return super.use(level, player, hand);
        }

        ItemStack glasses = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(glasses);
        }

        SmartGlassesComputer computer = getOrCreateComputer(serverLevel, player, glasses);
        if (!computer.isOn()) {
            computer.turnOn();
        }

        SmartGlassesItemHandler itemHandler = new SmartGlassesItemHandler(glasses, computer);
        player.openMenu(
            new SmartGlassesMenuProvider(computer, glasses, itemHandler),
            new ComputerContainerData(computer, glasses)::toBytes
        );
        return InteractionResultHolder.consume(glasses);
    }

    public SmartGlassesComputer getOrCreateComputer(ServerLevel level, Entity entity, ItemStack stack) {
        ServerComputerRegistry registry = ServerContext.get(level.getServer()).registry();
        SmartGlassesComputer computer = (SmartGlassesComputer) ServerComputerReference.get(stack, registry);
        if (computer != null) {
            return computer;
        }

        int computerID = getComputerID(stack);
        if (computerID < 0) {
            computerID = NonNegativeId.getOrCreate(level.getServer(), stack, ModRegistry.DataComponents.COMPUTER_ID.get(), IDAssigner.COMPUTER);
        }

        SmartGlassesComputer newComputer = SmartGlassesComputer.create(
            level,
            BlockPos.containing(entity.getEyePosition()),
            ServerComputer.properties(getComputerID(stack), ComputerFamily.ADVANCED)
                .label(getLabel(stack))
                .storageCapacity(StorageCapacity.getOrDefault(stack.get(ModRegistry.DataComponents.STORAGE_CAPACITY.get()), -1)),
            stack
        );

        stack.set(ModRegistry.DataComponents.COMPUTER.get(), new ServerComputerReference(registry.getSessionID(), newComputer.register()));

        if (entity instanceof Player player) {
            // Only turn on when initially creating the computer, rather than each tick.
            if (isMarkedOn(stack)) {
                newComputer.turnOn();
            }
            player.getInventory().setChanged();
        }
        return newComputer;
    }

    @Nullable
    public static SmartGlassesComputer getServerComputer(MinecraftServer server, ItemStack stack) {
        return (SmartGlassesComputer) ServerComputerReference.get(stack, ServerContext.get(server).registry());
    }

    public static int getComputerID(ItemStack stack) {
        return NonNegativeId.getId(stack.get(ModRegistry.DataComponents.COMPUTER_ID.get()));
    }

    private @Nullable String getLabel(ItemStack stack) {
        return DataComponentUtil.getCustomName(stack);
    }

    private void setLabel(ItemStack stack, @Nullable String label) {
        DataComponentUtil.setCustomName(stack, label);
    }

    private static boolean isMarkedOn(ItemStack stack) {
        return stack.getOrDefault(ModRegistry.DataComponents.ON.get(), false);
    }

    public static ItemStack getEquipped(final LivingEntity entity) {
        final ItemStack glasses = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (glasses.getItem() instanceof SmartGlassesItem) {
            return glasses;
        }
        if (APAddon.CURIOS.isLoaded()) {
            return getEquippedCurios(entity);
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getEquippedCurios(final LivingEntity entity) {
        if (!APAddon.CURIOS.isLoaded()) {
            return ItemStack.EMPTY;
        }
        final ICuriosItemHandler curiosInv = CuriosApi.getCuriosInventory(entity).orElse(null);
        if (curiosInv == null) {
            return ItemStack.EMPTY;
        }
        final SlotResult glassesSlot = curiosInv.findFirstCurio((stack) -> stack.getItem() instanceof SmartGlassesItem).orElse(null);
        if (glassesSlot == null) {
            return ItemStack.EMPTY;
        }
        return glassesSlot.stack();
    }

    public static boolean containsGlassesStack(final Player player, final Predicate<ItemStack> tester) {
        if (player.getInventory().contains(tester)) {
            return true;
        }
        return APAddon.CURIOS.isLoaded() && containsGlassesStackCurios(player, tester);
    }

    private static boolean containsGlassesStackCurios(final Player player, final Predicate<ItemStack> tester) {
        final ICuriosItemHandler curiosInv = CuriosApi.getCuriosInventory(player).orElse(null);
        if (curiosInv == null) {
            return false;
        }
        return curiosInv.findFirstCurio(tester).isPresent();
    }
}
