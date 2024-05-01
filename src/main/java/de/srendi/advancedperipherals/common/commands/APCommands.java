package de.srendi.advancedperipherals.common.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.core.computer.Environment;
import dan200.computercraft.shared.command.UserLevel;
import dan200.computercraft.shared.command.text.ChatHelpers;
import dan200.computercraft.shared.command.text.TableBuilder;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChunkyPeripheral;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID)
public class APCommands {
    public static final String ROOT_LITERAL = "advancedperipherals";
    public static final String FORCELOAD_LITERAL = "forceload";
    static final String FORCELOAD_HELP =
        "/" + ROOT_LITERAL + " " + FORCELOAD_LITERAL + " help" + " - show this help message\n" +
        "/" + ROOT_LITERAL + " " + FORCELOAD_LITERAL + " dump" + " - show all chunky turtles\n";

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(ROOT_LITERAL)
                .then(Commands.literal("getHashItem")
                        .executes(context -> getHashItem(context.getSource()))).then(Commands.literal(FORCELOAD_LITERAL)
                .executes(context -> forceloadHelp(context.getSource()))
                .then(Commands.literal("help")
                    .executes(context -> forceloadHelp(context.getSource())))
                .then(Commands.literal("dump")
                    .requires(UserLevel.OWNER_OP)
                    .executes(context -> forceloadDump(context.getSource())))
            )
        );
    }

    private static int getHashItem(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer playerEntity = source.getPlayerOrException();
        if (playerEntity.getMainHandItem().isEmpty()) {
            source.sendFailure(Component.literal("You need an item in your main hand."));
            return 0;
        }
        String fingerprint = ItemUtil.getFingerprint(playerEntity.getMainHandItem());
        if (fingerprint.isEmpty()) {
            source.sendFailure(Component.literal("There was an issue while generating the hash. Report to Author"));
            return 0;
        }
        source.sendSuccess(Component.literal("Fingerprint of the item: "), true);
        source.sendSuccess(ComponentUtils.wrapInSquareBrackets(
                Component.literal(fingerprint)
                        .withStyle(style -> style.applyFormat(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, fingerprint))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Copy"))))), true);
        return 1;
    }

    private static int forceloadHelp(CommandSourceStack source) throws CommandSyntaxException {
        source.sendSuccess(Component.literal(FORCELOAD_HELP), true);
        return 1;
    }

    private static int forceloadDump(CommandSourceStack source) throws CommandSyntaxException {
        TableBuilder table = new TableBuilder("ChunkyTurtles", "Computer", "Position");

        ServerComputer[] computers = ServerContext.get(source.getServer()).registry().getComputers().stream().filter((computer) -> {
            Environment env = computer.getComputer().getEnvironment();
            for (ComputerSide side : ComputerSide.values()) {
                if (env.getPeripheral(side) instanceof ChunkyPeripheral) {
                    return true;
                }
            }
            return false;
        }).sorted((a, b) -> a.getID() - b.getID()).toArray(size -> new ServerComputer[size]);

        for (ServerComputer computer : computers) {
            table.row(
                makeComputerDumpCommand(computer),
                makeComputerPosCommand(computer)
            );
        }

        table.display(source);
        return computers.length;
    }


    private static Component makeComputerDumpCommand(ServerComputer computer) {
        return ChatHelpers.link(
            Component.literal("#" + computer.getID()),
            "/computercraft dump " + computer.getInstanceID(),
            Component.translatable("commands.computercraft.dump.action")
        );
    }

    private static Component makeComputerPosCommand(ServerComputer computer) {
        return ChatHelpers.link(
            ChatHelpers.position(computer.getPosition()),
            "/computercraft tp " + computer.getInstanceID(),
            Component.translatable("commands.computercraft.tp.action")
        );
    }
}
