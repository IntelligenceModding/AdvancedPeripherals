package de.srendi.advancedperipherals.common.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.command.text.ChatHelpers;
import dan200.computercraft.shared.command.text.TableBuilder;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChunkyPeripheral;
import de.srendi.advancedperipherals.common.events.Events;
import de.srendi.advancedperipherals.common.util.ChunkManager;
import de.srendi.advancedperipherals.common.util.FingerprintUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Comparator;
import java.util.UUID;


@EventBusSubscriber
public class APCommands {
    public static final String ROOT_LITERAL = "advancedperipherals";
    public static final String FORCELOAD_LITERAL = "forceload";
    static final String FORCELOAD_HELP =
        "/" + ROOT_LITERAL + " " + FORCELOAD_LITERAL + " help" + " - show this help message\n" +
        "/" + ROOT_LITERAL + " " + FORCELOAD_LITERAL + " dump" + " - show all chunky turtles\n";
    public static final String EXEC_LITERAL = "safe-exec";
    public static final String CHATBOX_LITERAL = "chatbox";
    public static final String ROOT_SAFE_EXEC_LITERAL = "ap-safe-exec";
    public static final String ROOT_CHATBOX_LITERAL = "ap-chatbox";

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        LiteralCommandNode<CommandSourceStack> safeExecNode = Commands.literal(EXEC_LITERAL)
            .then(Commands.argument("command", StringArgumentType.greedyString())
                .executes(APCommands::safeExecute))
            .build();
        LiteralCommandNode<CommandSourceStack> chatBoxNode = Commands.literal(CHATBOX_LITERAL)
            .then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(APCommands::chatBox))
            .build();
        event.getDispatcher().register(Commands.literal(ROOT_LITERAL)
            .then(Commands.literal("getHashItem")
                .executes(context -> getHashItem(context.getSource())))
            .then(Commands.literal(FORCELOAD_LITERAL)
                .executes(context -> forceloadHelp(context.getSource()))
                .then(Commands.literal("help")
                    .executes(context -> forceloadHelp(context.getSource())))
                .then(Commands.literal("dump")
                    .requires(ModRegistry.Permissions.PERMISSION_DUMP)
                    .executes(context -> forceloadDump(context.getSource()))))
            .then(safeExecNode)
            .then(chatBoxNode)
        );
        event.getDispatcher().register(Commands.literal(ROOT_SAFE_EXEC_LITERAL).redirect(safeExecNode));
        event.getDispatcher().register(Commands.literal(ROOT_CHATBOX_LITERAL).redirect(chatBoxNode));
    }

    private static int getHashItem(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer playerEntity = source.getPlayerOrException();
        if (playerEntity.getMainHandItem().isEmpty()) {
            source.sendFailure(Component.literal("You need an item in your main hand."));
            return 0;
        }
        String hash = FingerprintUtil.hash(playerEntity.getMainHandItem().getComponentsPatch());

        source.sendSuccess(() -> Component.literal("NBT hash of the item: "), true);
        source.sendSuccess(() -> ComponentUtils.wrapInSquareBrackets(
                Component.literal(hash == null ? "nil" : hash)
                        .withStyle(style -> style.applyFormat(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, hash == null ? "nil" : hash))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Copy"))))), true);
        return 1;
    }

    private static int forceloadHelp(CommandSourceStack source) throws CommandSyntaxException {
        source.sendSuccess(() -> Component.literal(FORCELOAD_HELP), true);
        return 1;
    }

    private static int forceloadDump(CommandSourceStack source) throws CommandSyntaxException {
        TableBuilder table = new TableBuilder("ChunkyTurtles", "Computer", "Position");

        ServerComputer[] computers = ServerContext.get(source.getServer())
            .registry()
            .getComputers()
            .stream()
            .filter((computer) -> {
                for (ComputerSide side : ComputerSide.values()) {
                    if (computer.getPeripheral(side) instanceof ChunkyPeripheral) {
                        return true;
                    }
                }
                return false;
            })
            .sorted(Comparator.comparingInt(ServerComputer::getID))
            .toArray(ServerComputer[]::new);

        for (ServerComputer computer : computers) {
            table.row(
                makeComputerDumpCommand(computer),
                makeComputerPosCommand(computer)
            );
        }

        ChunkManager manager = ChunkManager.get(source.getServer());
        source.sendSuccess(() -> Component.literal("Forced " + manager.getForcedChunksCount() + " chunks"), true);
        table.display(source);
        return computers.length;
    }

    private static int safeExecute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource().withPermission(0);
        String command = StringArgumentType.getString(context, "command");
        try {
            source.getServer().getCommands().performPrefixedCommand(source, command);
            return 1;
        } catch (RuntimeException e) {
            source.sendFailure(Component.literal(e.getMessage()));
            return 0;
        }
    }

    private static int chatBox(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        UUID uuid = null;
        String username = "[say]";
        Entity sourceEntity = source.getEntity();
        if (sourceEntity != null) {
            uuid = sourceEntity.getUUID();
            username = sourceEntity instanceof Player player
                ? player.getGameProfile().getName()
                : sourceEntity.getName().getString();
        }
        String message = StringArgumentType.getString(context, "message");
        Events.putChatMessage(
            new Events.ChatMessageRecord(uuid, username, message, true, source.getLevel().dimension(), source.getPosition())
        );
        return 0;
    }

    private static Component makeComputerDumpCommand(ServerComputer computer) {
        return ChatHelpers.link(
            Component.literal("#" + computer.getID()),
            "/computercraft dump " + computer.getInstanceUUID(),
            Component.translatable("commands.computercraft.dump.action")
        );
    }

    private static Component makeComputerPosCommand(ServerComputer computer) {
        return ChatHelpers.link(
            ChatHelpers.position(computer.getPosition()),
            "/computercraft tp " + computer.getInstanceUUID(),
            Component.translatable("commands.computercraft.tp.action")
        );
    }
}
