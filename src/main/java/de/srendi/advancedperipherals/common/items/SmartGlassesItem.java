package de.srendi.advancedperipherals.common.items;

import com.google.common.base.Objects;
import dan200.computercraft.annotations.ForgeOverride;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.items.IComputerItem;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.util.IDAssigner;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAPI;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesItemHandler;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesMenuProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
        super(material, EquipmentSlot.HEAD, new Properties().stacksTo(1));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if(cap == ForgeCapabilities.ITEM_HANDLER)
                    return LazyOptional.of(() -> new SmartGlassesItemHandler(stack)).cast();
                return LazyOptional.empty();
            }
        };
    }

    private boolean tick(ItemStack stack, Level world, Entity entity, SmartGlassesComputer computer) {
        computer.setLevel((ServerLevel) world);

        var changed = false;

        // Sync ID
        var id = computer.getID();
        if (id != getComputerID(stack)) {
            changed = true;
            setComputerID(stack, id);
        }

        // Sync label
        var label = computer.getLabel();
        if (!Objects.equal(label, getLabel(stack))) {
            changed = true;
            setLabel(stack, label);
        }

        var on = computer.isOn();
        if (on != isMarkedOn(stack)) {
            changed = true;
            stack.getOrCreateTag().putBoolean(NBT_ON, on);
        }

        return changed;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, Level world, @NotNull Entity entity, int slotNum, boolean selected) {
        if (world.isClientSide) return;
        Container inventory = entity instanceof Player player ? player.getInventory() : null;
        var computer = createServerComputer((ServerLevel) world, entity, inventory, stack);
        computer.keepAlive();

        var changed = tick(stack, world, entity, computer);
        if (changed && inventory != null) inventory.setChanged();
    }

    @ForgeOverride
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.level.isClientSide || entity.level.getServer() == null) return false;

        var computer = getServerComputer(entity.level.getServer(), stack);
        if (computer != null && tick(stack, entity.level, entity, computer)) entity.setItem(stack.copy());
        return false;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            var computer = createServerComputer((ServerLevel) world, player, player.getInventory(), stack);
            computer.turnOn();

            var stop = false;
            if (!stop) {
                var isTypingOnly = hand == InteractionHand.OFF_HAND;
                LazyOptional<IItemHandler> itemHandler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
                new ComputerContainerData(computer, stack).open(player, new SmartGlassesMenuProvider(computer, stack, this, hand, itemHandler.resolve().get()));
            }
        }
        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(world.isClientSide), stack);
    }

    public ItemStack create(int id, @Nullable String label) {
        var result = new ItemStack(this);
        if (id >= 0) result.getOrCreateTag().putInt(NBT_ID, id);
        if (label != null) result.setHoverName(Component.literal(label));
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
    public void appendHoverText(@NotNull ItemStack stack, @javax.annotation.Nullable Level world, @NotNull List<Component> list, TooltipFlag flag) {
        if (flag.isAdvanced() || getLabel(stack) == null) {
            var id = getComputerID(stack);
            if (id >= 0) {
                list.add(Component.translatable("gui.computercraft.tooltip.computer_id", id)
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public String getCreatorModId(ItemStack stack) {
        return AdvancedPeripherals.MOD_ID;
    }

    public SmartGlassesComputer createServerComputer(ServerLevel level, Entity entity, @javax.annotation.Nullable Container inventory, ItemStack stack) {
        var sessionID = getSessionID(stack);

        var registry = ServerContext.get(level.getServer()).registry();
        var computer = (SmartGlassesComputer) registry.get(sessionID, getInstanceID(stack));
        if (computer == null) {
            var computerID = getComputerID(stack);
            if (computerID < 0) {
                computerID = ComputerCraftAPI.createUniqueNumberedSaveDir(level.getServer(), IDAssigner.COMPUTER);
                setComputerID(stack, computerID);
            }

            computer = new SmartGlassesComputer(level, entity.blockPosition(), getComputerID(stack), getLabel(stack), getFamily());

            setInstanceID(stack, computer.register());
            setSessionID(stack, registry.getSessionID());

            computer.addAPI(new SmartGlassesAPI());

            // Only turn on when initially creating the computer, rather than each tick.
            if (isMarkedOn(stack) && entity instanceof Player) computer.turnOn();

            if (inventory != null) inventory.setChanged();
        }
        computer.setLevel(level);
        return computer;
    }

    @Nullable
    public static SmartGlassesComputer getServerComputer(MinecraftServer server, ItemStack stack) {
        return (SmartGlassesComputer) ServerContext.get(server).registry().get(getSessionID(stack), getInstanceID(stack));
    }

    // IComputerItem implementation
    private static void setComputerID(ItemStack stack, int computerID) {
        stack.getOrCreateTag().putInt(NBT_ID, computerID);
    }

    @Override
    public @javax.annotation.Nullable String getLabel(ItemStack stack) {
        return IComputerItem.super.getLabel(stack);
    }

    @Override
    public boolean setLabel(ItemStack stack, @javax.annotation.Nullable String label) {
        if (label != null) {
            stack.setHoverName(Component.literal(label));
        } else {
            stack.resetHoverName();
        }
        return true;
    }

    @Nullable
    @Override
    public Mount createDataMount(ItemStack stack, ServerLevel level) {
        var id = getComputerID(stack);
        if (id >= 0) {
            return ComputerCraftAPI.createSaveDirMount(level.getServer(), "computer/" + id, Config.computerSpaceLimit);
        }
        return null;
    }

    public static int getInstanceID(ItemStack stack) {
        var nbt = stack.getTag();
        return nbt != null && nbt.contains(NBT_INSTANCE) ? nbt.getInt(NBT_INSTANCE) : -1;
    }

    private static void setInstanceID(ItemStack stack, int instanceID) {
        stack.getOrCreateTag().putInt(NBT_INSTANCE, instanceID);
    }

    private static int getSessionID(ItemStack stack) {
        var nbt = stack.getTag();
        return nbt != null && nbt.contains(NBT_SESSION) ? nbt.getInt(NBT_SESSION) : -1;
    }

    private static void setSessionID(ItemStack stack, int sessionID) {
        stack.getOrCreateTag().putInt(NBT_SESSION, sessionID);
    }

    private static boolean isMarkedOn(ItemStack stack) {
        var nbt = stack.getTag();
        return nbt != null && nbt.getBoolean(NBT_ON);
    }

}
