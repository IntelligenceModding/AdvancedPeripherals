package de.srendi.advancedperipherals.common.items;

import com.google.common.base.Objects;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.IWritableMount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.items.IComputerItem;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.util.IDAssigner;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.*;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import de.srendi.advancedperipherals.common.smartglasses.modules.ModulePeripheral;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmartGlassesItem extends ArmorItem implements IComputerItem, IMedia {

    private static final String NBT_UPGRADE = "Upgrade";
    private static final String NBT_UPGRADE_INFO = "UpgradeInfo";
    public static final String NBT_LIGHT = "Light";
    public static final String NBT_ON = "On";

    private static final String NBT_INSTANCE = "InstanceId";
    private static final String NBT_SESSION = "SessionId";

    public SmartGlassesItem(ArmorMaterial material) {
        super(material, EquipmentSlot.HEAD, new Properties().stacksTo(1).tab(AdvancedPeripherals.TAB));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == ForgeCapabilities.ITEM_HANDLER)
                    return LazyOptional.of(() -> new SmartGlassesItemHandler(stack, getServerComputer(ServerLifecycleHooks.getCurrentServer(), stack))).cast();
                return LazyOptional.empty();
            }
        };
    }

    private boolean tick(ItemStack stack, Level world, Entity entity, SmartGlassesComputer computer) {
        computer.setLevel((ServerLevel) world);

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

        ItemStack computerStack = computer.getStack();
        if (computerStack != stack) {
            changed = true;
            computer.setStack(stack);
        }

        return changed;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slotNum, boolean selected) {
        LazyOptional<IItemHandler> itemHandler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
        if(itemHandler.isPresent()) {
            itemHandler.resolve().ifPresent(iItemHandler -> {
                for(int slot = 0; slot < iItemHandler.getSlots(); slot++) {
                    ItemStack itemStack = iItemHandler.getStackInSlot(slot);
                    if(itemStack.getItem() instanceof IModuleItem iModuleItem) {
                        SmartGlassesAccess glassesAccess = null;
                        IModule module = null;
                        if (!world.isClientSide) {
                            SmartGlassesComputer computer = getOrCreateComputer((ServerLevel) world, entity, entity instanceof Player player ? player.getInventory() : null, stack);
                            module = computer.getModules().get(slot);
                            glassesAccess = computer.getSmartGlassesAccess();
                        }
                        iModuleItem.inventoryTick(itemStack, world, entity, slot, selected, glassesAccess, module);
                    }
                }
            });
        }

        if (world.isClientSide)
            return;
        Container inventory = entity instanceof Player player ? player.getInventory() : null;
        SmartGlassesComputer computer = getOrCreateComputer((ServerLevel) world, entity, inventory, stack);
        computer.keepAlive();

        var changed = tick(stack, world, entity, computer);
        if (changed && inventory != null)
            inventory.setChanged();
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.level.isClientSide || entity.level.getServer() == null) return false;

        SmartGlassesComputer computer = getServerComputer(entity.level.getServer(), stack);
        if (computer != null && tick(stack, entity.level, entity, computer))
            entity.setItem(stack.copy());
        return false;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
        ItemStack glasses = player.getItemInHand(hand);

        if (!world.isClientSide) {
            SmartGlassesComputer computer = getOrCreateComputer((ServerLevel) world, player, player.getInventory(), glasses);
            computer.turnOn();

            LazyOptional<IItemHandler> itemHandler = glasses.getCapability(ForgeCapabilities.ITEM_HANDLER);
            if (!itemHandler.isPresent() || itemHandler.resolve().isEmpty()) {
                AdvancedPeripherals.debug("There was an issue with the item handler of the glasses while trying to open the gui");
                return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), world.isClientSide);
            }
            new ComputerContainerData(computer, glasses).open(player, new SmartGlassesMenuProvider(computer, glasses, itemHandler.resolve().get()));
        }
        return super.use(world, player, hand);
    }

    public ItemStack create(int id, @Nullable String label) {
        ItemStack result = new ItemStack(this);
        if (id >= 0)
            result.getOrCreateTag().putInt(NBT_ID, id);
        if (label != null)
            result.setHoverName(Component.literal(label));
        return result;
    }

    @Override
    public ComputerFamily getFamily() {
        return ComputerFamily.ADVANCED;
    }

    @Override
    public ItemStack withFamily(ItemStack stack, ComputerFamily family) {
        return create(getComputerID(stack), getLabel(stack));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> list, TooltipFlag flag) {
        if (flag.isAdvanced() || getLabel(stack) == null) {
            int id = getComputerID(stack);
            if (id >= 0) {
                list.add(Component.translatable("gui.computercraft.tooltip.computer_id", id).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Override
    public String getCreatorModId(ItemStack stack) {
        return AdvancedPeripherals.MOD_ID;
    }

    public SmartGlassesComputer getOrCreateComputer(ServerLevel level, Entity entity, @Nullable Container inventory, ItemStack stack) {
        int sessionID = getSessionID(stack);

        ServerComputerRegistry registry = ServerContext.get(level.getServer()).registry();
        SmartGlassesComputer computer = (SmartGlassesComputer) registry.get(sessionID, getInstanceID(stack));
        if (computer == null) {
            int computerID = getComputerID(stack);
            if (computerID < 0) {
                computerID = ComputerCraftAPI.createUniqueNumberedSaveDir(level, IDAssigner.COMPUTER);
                setComputerID(stack, computerID);
            }

            computer = new SmartGlassesComputer(level, getComputerID(stack), getLabel(stack), getFamily());

            setInstanceID(stack, computer.register());
            setSessionID(stack, registry.getSessionID());

            computer.addAPI(new SmartGlassesAPI());

            // Only turn on when initially creating the computer, rather than each tick.
            if (isMarkedOn(stack) && entity instanceof Player)
                computer.turnOn();

            if (inventory != null)
                inventory.setChanged();
        }
        computer.setPeripheral(ComputerSide.BACK, new ModulePeripheral(computer));
        computer.setLevel(level);
        return computer;
    }

    @Nullable
    public static SmartGlassesComputer getServerComputer(MinecraftServer server, ItemStack stack) {
        if (server == null)
            return null;
        return (SmartGlassesComputer) ServerContext.get(server).registry().get(getSessionID(stack), getInstanceID(stack));
    }

    // IComputerItem implementation
    protected static void setComputerID(ItemStack stack, int computerID) {
        stack.getOrCreateTag().putInt(NBT_ID, computerID);
    }

    @Nullable
    @Override
    public String getLabel(ItemStack stack) {
        return IComputerItem.super.getLabel(stack);
    }

    @Override
    public boolean setLabel(ItemStack stack, @Nullable String label) {
        if (label != null) {
            stack.setHoverName(Component.literal(label));
        } else {
            stack.resetHoverName();
        }
        return true;
    }

    @Nullable
    @Override
    public IWritableMount createDataMount(@NotNull ItemStack stack, @NotNull Level level) {
        int id = getComputerID(stack);
        if (id >= 0) {
            return ComputerCraftAPI.createSaveDirMount(level, "computer/" + id, ComputerCraft.computerSpaceLimit);
        }
        return null;
    }

    public static int getInstanceID(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.contains(NBT_INSTANCE) ? nbt.getInt(NBT_INSTANCE) : -1;
    }

    protected static void setInstanceID(ItemStack stack, int instanceID) {
        stack.getOrCreateTag().putInt(NBT_INSTANCE, instanceID);
    }

    protected static int getSessionID(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.contains(NBT_SESSION) ? nbt.getInt(NBT_SESSION) : -1;
    }

    protected static void setSessionID(ItemStack stack, int sessionID) {
        stack.getOrCreateTag().putInt(NBT_SESSION, sessionID);
    }

    protected static boolean isMarkedOn(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.getBoolean(NBT_ON);
    }

}
