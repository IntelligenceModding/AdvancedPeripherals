package de.srendi.advancedperipherals.common.items;

import dan200.computercraft.shared.network.container.ComputerContainerData;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddons;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.setup.APTags;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class SmartGlassesInterfaceItem extends BaseItem {

    public SmartGlassesInterfaceItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player player, InteractionHand hand) {
        if (world.isClientSide)
            return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));

        // In case this method gets executed by the smart glasses interface, we need to check if the glasses may be in the
        // curio slot or on the head
        ItemStack findGlasses = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!findGlasses.is(APTags.Items.SMART_GLASSES))
            if (APAddons.curiosLoaded)
                findGlasses = APAddons.getCurioGlasses(player);

        if (!findGlasses.is(APTags.Items.SMART_GLASSES)) {
            player.displayClientMessage(Component.translatable("item.advancedperipherals.smartglasses.dontwear"), false);
            return super.use(world, player, hand);
        }

        // The constructor of the ComputerContainerData in the lambda wants a final version of this var
        ItemStack glasses = findGlasses;

        SmartGlassesItem smartGlasses = (SmartGlassesItem) glasses.getItem();

        SmartGlassesComputer computer = smartGlasses.getOrCreateComputer((ServerLevel) world, player, player.getInventory(), glasses);
        computer.turnOn();

        LazyOptional<IItemHandler> itemHandler = glasses.getCapability(ForgeCapabilities.ITEM_HANDLER);
        if (!itemHandler.isPresent() || itemHandler.resolve().isEmpty()) {
            AdvancedPeripherals.debug("There was an issue with the item handler of the glasses while trying to open the gui");
            return super.use(world, player, hand);
        }
        NetworkHooks.openScreen((ServerPlayer) player, new SmartGlassesMenuProvider(computer, glasses, itemHandler.resolve().get()), bytes -> new ComputerContainerData(computer, glasses).toBytes(bytes));

        return super.use(world, player, hand);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
