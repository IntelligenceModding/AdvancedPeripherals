package de.srendi.advancedperipherals.common.items;

import java.util.UUID;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import com.google.common.base.Objects;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.items.IComputerItem;
import dan200.computercraft.shared.media.MountMedia;
import dan200.computercraft.shared.util.IDAssigner;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.curios.SmartGlassesCurio;
import de.srendi.advancedperipherals.common.component.ItemStackStorage;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesItemHandler;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesMenuProvider;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSlot;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class SmartGlassesItem extends ArmorItem implements IComputerItem, IMedia {

    private static final String NBT_UPGRADE = "Upgrade";
    private static final String NBT_UPGRADE_INFO = "UpgradeInfo";
    public static final String NBT_LIGHT = "Light";
    public static final String NBT_ON = "On";

    private static final String NBT_INSTANCE = "InstanceId";
    private static final String NBT_SESSION = "SessionId";

    public SmartGlassesItem(ArmorMaterial material) {
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

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == ForgeCapabilities.ITEM_HANDLER) {
                    return LazyOptional.of(() -> createItemHandlerCap(stack)).cast();
                }
                if (APAddon.CURIOS.isLoaded() && cap == CuriosCapability.ITEM) {
                    return LazyOptional.of(() -> createCurioCap(stack)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    // @Override // TODO: what's the replacement in 1.20.1?
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
            setComputerID(stack, id);
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
            stack.getOrCreateTag().putBoolean(NBT_ON, on);
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
        SmartGlassesComputer computer = level instanceof ServerLevel serverLevel
            ? getOrCreateComputer(serverLevel, entity, stack)
            : null;

        ItemStackStorage items = SmartGlassesItemHandler.loadItems(stack);
        IntFunction<Item> moduleInv = computer == null
                ? (slot) -> items.getItem(slot + SmartGlassesSlot.MODULE_SLOT_OFFSET)
                : (slot) -> computer.getModuleStack(slot).getItem();

        for (int slot = 0; slot < SmartGlassesSlot.MODULE_SLOTS; slot++) {
            Item item = moduleInv.apply(slot);
            if (!(item instanceof IModuleItem moduleItem)) {
                continue;
            }
            SmartGlassesSideAccess glassesAccess = null;
            IModule module = null;
            if (computer != null) {
                glassesAccess = computer.getSmartGlassesModuleAccess();
                module = computer.getModuleBySlot(slot);
                if (module == null) {
                    continue;
                }
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
            new SmartGlassesMenuProvider(computer, glasses, itemHandler)
        );
        return InteractionResultHolder.consume(glasses);
    }

    @Override
    public ItemStack changeItem(ItemStack oldStack, Item newItem) {
        if (!(newItem instanceof SmartGlassesItem glassesItem)) {
            return ItemStack.EMPTY;
        }
        ItemStack newStack = new ItemStack(glassesItem);
        setComputerID(newStack, getComputerID(oldStack));
        setLabel(newStack, getLabel(oldStack));
        SmartGlassesItemHandler.saveItems(newStack, SmartGlassesItemHandler.loadItems(oldStack));
        newStack.getOrCreateTag().put(APDataComponents.MODULE_DATAS, oldStack.getTagElement(APDataComponents.MODULE_DATAS));
        return newStack;
    }

    public SmartGlassesComputer getOrCreateComputer(ServerLevel level, Entity entity, ItemStack stack) {
        MinecraftServer server = level.getServer();
        ServerComputerRegistry registry = ServerContext.get(server).registry();

        SmartGlassesComputer computer = (SmartGlassesComputer) registry.get(getSessionID(stack), getInstanceID(stack));
        if (computer != null) {
            return computer;
        }

        int computerID = getComputerID(stack);
        if (computerID < 0) {
            computerID = ComputerCraftAPI.createUniqueNumberedSaveDir(server, IDAssigner.COMPUTER);
            setComputerID(stack, computerID);
        }

        SmartGlassesComputer newComputer = SmartGlassesComputer.create(
            level,
            BlockPos.containing(entity.getEyePosition()),
            ServerComputer.properties(getComputerID(stack), ComputerFamily.ADVANCED)
                .label(getLabel(stack)),
            stack
        );

        setInstanceID(stack, newComputer.register());
        setSessionID(stack, registry.getSessionID());

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
        if (server == null) {
            return null;
        }
        return (SmartGlassesComputer) ServerContext.get(server).registry().get(getSessionID(stack), getInstanceID(stack));
    }

    // IComputerItem implementation
    private static void setComputerID(ItemStack stack, int computerID) {
        stack.getOrCreateTag().putInt(NBT_ID, computerID);
    }

    @Override
    public String getLabel(ItemStack stack) {
        return MountMedia.COMPUTER.getLabel(stack);
    }

    @Override
    public boolean setLabel(ItemStack stack, @Nullable String label) {
        return MountMedia.COMPUTER.setLabel(stack, label);
    }

    @Nullable
    @Override
    public Mount createDataMount(ItemStack stack, ServerLevel level) {
        return MountMedia.COMPUTER.createDataMount(stack, level);
    }

    public static UUID getInstanceID(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.contains(NBT_INSTANCE) ? nbt.getUUID(NBT_INSTANCE) : null;
    }

    private static void setInstanceID(ItemStack stack, UUID instanceID) {
        stack.getOrCreateTag().putUUID(NBT_INSTANCE, instanceID);
    }

    private static int getSessionID(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.contains(NBT_SESSION) ? nbt.getInt(NBT_SESSION) : -1;
    }

    private static void setSessionID(ItemStack stack, int sessionID) {
        stack.getOrCreateTag().putInt(NBT_SESSION, sessionID);
    }

    public static boolean isMarkedOn(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.getBoolean(NBT_ON);
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
        for (NonNullList<ItemStack> list : player.getInventory().compartments) {
            for (ItemStack stack : list) {
                if (tester.test(stack)) {
                    return true;
                }
            }
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

    public static CompoundTag getModuleDatas(final ItemStack stack, final ResourceLocation moduleID) {
        CompoundTag moduleDatas = stack.getTagElement(APDataComponents.MODULE_DATAS);
        if (moduleDatas == null || moduleDatas.isEmpty()) {
            return null;
        }
        ItemStackStorage items = SmartGlassesItemHandler.loadItems(stack);
        for (int slot = 0; slot < SmartGlassesSlot.MODULE_SLOTS; slot++) {
            ItemStack moduleStack = items.get(slot + SmartGlassesSlot.MODULE_SLOT_OFFSET);
            if (moduleStack.getItem() instanceof IModuleItem moduleItem && moduleItem.moduleId().equals(moduleID)) {
                return moduleDatas;
            }
        }
        return null;
    }
}
