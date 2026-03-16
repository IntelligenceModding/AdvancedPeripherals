package de.srendi.advancedperipherals.common.items;

import com.google.common.base.Objects;
import dan200.computercraft.api.pocket.IPocketUpgrade;
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
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesItemHandler;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesMenuProvider;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;

public class SmartGlassesItem extends ArmorItem {

    public SmartGlassesItem(Holder<ArmorMaterial> material) {
        super(material, ArmorItem.Type.HELMET, new Properties().stacksTo(1));
    }

    public IItemHandler createItemHandlerCap(ItemStack stack) {
        SmartGlassesComputer computer = getServerComputer(ServerLifecycleHooks.getCurrentServer(), stack);
        SmartGlassesItemHandler handler = new SmartGlassesItemHandler(stack, computer);
        return handler;
    }

    private boolean tick(ItemStack stack, Level world, Entity entity, SmartGlassesComputer computer) {
        computer.setPosition((ServerLevel) world, entity != null ? entity.blockPosition() : computer.getPosition());

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

        ItemStack computerStack = computer.getStack();
        if (computerStack != stack) {
            changed = true;
            computer.setStack(stack);
        }

        // TODO: maintain a constant array/list for vaild upgrade sides
        for (ComputerSide side : ComputerSide.values()) {
            if (side == ComputerSide.BACK) {
                continue;
            }
            SmartGlassesSideAccess access = computer.getSmartGlassesUpgradeAccess(side);
            IPocketUpgrade upgrade = access.getUpgrade().upgrade();
            if (upgrade != null) {
                upgrade.update(access, computer.getPeripheral(side));
            }
        }

        return changed;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slotNum, boolean selected) {
        SmartGlassesItemHandler itemHandler = (SmartGlassesItemHandler) stack.getCapability(Capabilities.ItemHandler.ITEM);
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack itemStack = itemHandler.getStackInSlot(slot);
            if (itemStack.getItem() instanceof IModuleItem iModuleItem) {
                SmartGlassesSideAccess glassesAccess = null;
                IModule module = null;
                if (!world.isClientSide) {
                    SmartGlassesComputer computer = getOrCreateComputer((ServerLevel) world, entity, entity instanceof Player player ? player.getInventory() : null, stack);
                    module = computer.getModuleBySlot(slot);
                    glassesAccess = computer.getSmartGlassesModuleAccess();
                }
                iModuleItem.inventoryTick(itemStack, world, entity, slot, selected, glassesAccess, module);
            }
        }

        if (world.isClientSide) {
            return;
        }
        Container inventory = entity instanceof Player player ? player.getInventory() : null;
        SmartGlassesComputer computer = getOrCreateComputer((ServerLevel) world, entity, inventory, stack);
        computer.keepAlive();
        computer.setItemHandler(itemHandler);

        boolean changed = tick(stack, world, entity, computer);
        if (changed && inventory != null) {
            inventory.setChanged();
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        final Level level = entity.level();
        if (level.isClientSide || level.getServer() == null) {
            return false;
        }

        SmartGlassesComputer computer = getServerComputer(level.getServer(), stack);
        if (computer != null && tick(stack, level, entity, computer)) {
            entity.setItem(stack.copy());
        }
        return false;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack glasses = player.getItemInHand(hand);

        if (!world.isClientSide) {
            SmartGlassesComputer computer = getOrCreateComputer((ServerLevel) world, player, player.getInventory(), glasses);
            computer.turnOn();

            IItemHandler itemHandler = glasses.getCapability(Capabilities.ItemHandler.ITEM);
            if (itemHandler == null) {
                AdvancedPeripherals.debug("There was an issue with the item handler of the glasses while trying to open the gui");
                return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), world.isClientSide);
            }
            player.openMenu(
                new SmartGlassesMenuProvider(computer, glasses, itemHandler),
                new ComputerContainerData(computer, glasses)::toBytes
            );
        }
        return super.use(world, player, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> list, TooltipFlag flag) {
        if (flag.isAdvanced() || getLabel(stack) == null) {
            int id = getComputerID(stack);
            if (id >= 0) {
                list.add(Component.translatable("gui.computercraft.tooltip.computer_id", id).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public SmartGlassesComputer getOrCreateComputer(ServerLevel level, Entity entity, @Nullable Container inventory, ItemStack stack) {
        ServerComputerRegistry registry = ServerContext.get(level.getServer()).registry();
        SmartGlassesComputer computer = (SmartGlassesComputer) ServerComputerReference.get(stack, registry);
        if (computer == null) {
            int computerID = getComputerID(stack);
            if (computerID < 0) {
                computerID = NonNegativeId.getOrCreate(level.getServer(), stack, ModRegistry.DataComponents.COMPUTER_ID.get(), IDAssigner.COMPUTER);
            }

            computer = new SmartGlassesComputer(
                level,
                entity.blockPosition(),
                ServerComputer.properties(getComputerID(stack), ComputerFamily.ADVANCED)
                    .label(getLabel(stack))
                    .storageCapacity(StorageCapacity.getOrDefault(stack.get(ModRegistry.DataComponents.STORAGE_CAPACITY.get()), -1)),
                stack.get(APDataComponents.UPGRADE_DATAS.get())
            );

            stack.set(ModRegistry.DataComponents.COMPUTER.get(), new ServerComputerReference(registry.getSessionID(), computer.register()));

            // Only turn on when initially creating the computer, rather than each tick.
            if (isMarkedOn(stack) && entity instanceof Player) {
                computer.turnOn();
            }
            if (inventory != null) {
                inventory.setChanged();
            }
        }
        // TODO: is this level update here really necessary?
        computer.setPosition(level, entity != null ? entity.blockPosition() : computer.getPosition());
        return computer;
    }

    @Nullable
    public static SmartGlassesComputer getServerComputer(MinecraftServer server, ItemStack stack) {
        if (server == null) {
            return null;
        }
        return (SmartGlassesComputer) ServerComputerReference.get(stack, ServerContext.get(server).registry());
    }

    private static int getComputerID(ItemStack stack) {
        return NonNegativeId.getId(stack.get(ModRegistry.DataComponents.COMPUTER_ID.get()));
    }

    private @Nullable String getLabel(ItemStack stack) {
        return DataComponentUtil.getCustomName(stack);
    }

    private void setLabel(ItemStack stack, @Nullable String label) {
        DataComponentUtil.setCustomName(stack, label);
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

    private static boolean isMarkedOn(ItemStack stack) {
        return stack.getOrDefault(ModRegistry.DataComponents.ON.get(), false);
    }

}
