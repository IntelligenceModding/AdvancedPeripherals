package de.srendi.advancedperipherals.common.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public class APCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("advancedperipherals")
                .then(Commands.literal("getHashItem").executes(context -> getHashItem(context.getSource()))));
    }

    private static int getHashItem(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity playerEntity = source.getPlayerOrException();
        if (playerEntity.getMainHandItem() == ItemStack.EMPTY) {
            source.sendFailure(new StringTextComponent("You need an item in your main hand."));
            return 0;
        }
        CompoundNBT tag = playerEntity.getMainHandItem().getTag();
        String hash = NBTUtil.getNBTHash(tag);
        if (hash == null) {
            source.sendFailure(new StringTextComponent("That item does not have NBT data"));
            return 0;
        }
        source.sendSuccess(new StringTextComponent("Hash of you main hand item: "), true);
        source.sendSuccess(TextComponentUtils.wrapInSquareBrackets(new StringTextComponent(hash).withStyle((style) -> style.applyFormat(TextFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, hash)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Copy"))))), true);
        return 1;
    }
}
