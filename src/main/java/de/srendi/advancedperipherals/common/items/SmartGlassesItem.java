package de.srendi.advancedperipherals.common.items;

import com.google.common.base.Objects;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.impl.PocketUpgrades;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.util.DataComponentUtil;
import dan200.computercraft.shared.util.IDAssigner;
import dan200.computercraft.shared.util.NonNegativeId;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAPI;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesItemHandler;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesMenuProvider;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.common.capabilities.Capability;
import net.neoforged.common.capabilities.ForgeCapabilities;
import net.neoforged.common.util.LazyOptional;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.Map;

public class SmartGlassesItem extends ArmorItem implements IMedia {

    private static final String NBT_UPGRADE = "Upgrade";
    private static final String NBT_UPGRADE_INFO = "UpgradeInfo";
    public static final String NBT_LIGHT = "Light";
    public static final String NBT_ON = "On";

    private static final String NBT_INSTANCE = "InstanceId";
    private static final String NBT_SESSION = "SessionId";

    public SmartGlassesItem(ArmorMaterial material) {
        super(material, EquipmentSlot.HEAD, new Properties().stacksTo(1));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == ForgeCapabilities.ITEM_HANDLER) {
                    return LazyOptional.of(() -> {
                        SmartGlassesComputer computer = getServerComputer(ServerLifecycleHooks.getCurrentServer(), stack);
                        SmartGlassesItemHandler handler = new SmartGlassesItemHandler(stack, computer);
                        return handler;
                    }).cast();
                }
                return LazyOptional.empty();
            }
        };
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

        for (Map.Entry<ResourceLocation, IPeripheral> e : computer.getUpgrades().entrySet()) {
            IPocketUpgrade upgrade = PocketUpgrades.instance().get(e.getKey().toString());
            if (upgrade != null) {
                upgrade.update(computer, e.getValue());
            }
        }

        return changed;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slotNum, boolean selected) {
        LazyOptional<IItemHandler> optItemHandler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
        SmartGlassesItemHandler itemHandler = (SmartGlassesItemHandler) optItemHandler.orElse(null);
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack itemStack = itemHandler.getStackInSlot(slot);
            if (itemStack.getItem() instanceof IModuleItem iModuleItem) {
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
        if (id >= 0) {
            result.getOrCreateTag().putInt(NBT_ID, id);
        }
        if (label != null) {
            result.setHoverName(Component.literal(label));
        }
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
                computerID = NonNegativeId.getOrCreate(level.getServer(), stack, ModRegistry.DataComponents.COMPUTER_ID.get(), IDAssigner.COMPUTER);
            }

            computer = new SmartGlassesComputer(level, getComputerID(stack), getLabel(stack), getFamily(), stack.getOrCreateTag().getCompound(SmartGlassesComputer.UPGRADE_DATAS_TAG));

            setInstanceID(stack, computer.register());
            setSessionID(stack, registry.getSessionID());

            computer.addApi(new SmartGlassesAPI());

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
        return (SmartGlassesComputer) ServerContext.get(server).registry().get(getSessionID(stack), getInstanceID(stack));
    }

    private static int getComputerID(ItemStack stack) {
        return NonNegativeId.getId(stack.get(ModRegistry.DataComponents.COMPUTER_ID.get()));
    }

    private @Nullable String getLabel(ItemStack stack) {
        return DataComponentUtil.getCustomName(stack);
    }

    @Nullable
    @Override
    public String getLabel(HolderLookup.Provider registries, ItemStack stack) {
        return getLabel(stack);
    }

    @Override
    public boolean setLabel(ItemStack stack, @Nullable String label) {
        DataComponentUtil.setCustomName(stack, label);
        return true;
    }

    @Nullable
    @Override
    public Mount createDataMount(@NotNull ItemStack stack, @NotNull ServerLevel level) {
        int id = getComputerID(stack);
        if (id < 0) {
            return null;
        }
        return ComputerCraftAPI.createSaveDirMount(level.getServer(), "computer/" + id, dan200.computercraft.shared.config.Config.computerSpaceLimit);
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

    public static int getInstanceID(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.contains(NBT_INSTANCE) ? nbt.getInt(NBT_INSTANCE) : -1;
    }

    private static void setInstanceID(ItemStack stack, int instanceID) {
        stack.getOrCreateTag().putInt(NBT_INSTANCE, instanceID);
    }

    private static int getSessionID(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.contains(NBT_SESSION) ? nbt.getInt(NBT_SESSION) : -1;
    }

    private static void setSessionID(ItemStack stack, int sessionID) {
        stack.getOrCreateTag().putInt(NBT_SESSION, sessionID);
    }

    private static boolean isMarkedOn(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        return nbt != null && nbt.getBoolean(NBT_ON);
    }

}
