package de.srendi.advancedperipherals.common.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public class APCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("advancedperipherals").then(Commands.literal("getHashItem").executes(context -> getHashItem(context.getSource()))));
    }

    private static int getHashItem(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer playerEntity = source.getPlayerOrException();
        if (playerEntity.getMainHandItem().isEmpty()) {
            source.sendFailure(new TextComponent("You need an item in your main hand."));
            return 0;
        }
        String fingerprint = ItemUtil.getFingerprint(playerEntity.getMainHandItem());
        if (fingerprint.isEmpty()) {
            source.sendFailure(new TextComponent("That item does not have NBT data"));
            return 0;
        }
        source.sendSuccess(new TextComponent("Fingerprint of the item: "), true);
        source.sendSuccess(ComponentUtils.wrapInSquareBrackets(
                new TextComponent(fingerprint)
                        .withStyle(style -> style.applyFormat(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, fingerprint))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Copy"))))), true);
        return 1;
    }
}
