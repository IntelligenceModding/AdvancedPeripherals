package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.google.gson.JsonSyntaxException;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralFunction;
import de.srendi.advancedperipherals.lib.peripherals.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.owner.PocketPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.owner.TileEntityPeripheralOwner;
import de.srendi.advancedperipherals.lib.peripherals.owner.TurtlePeripheralOwner;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SimpleFreeOperation.CHAT_MESSAGE;

public class ChatBoxPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String TYPE = "chatBox";

    private final static String PREFIX_FORMAT = "[%s] ";

    protected ChatBoxPeripheral(IPeripheralOwner owner) {
        super(TYPE, owner);
        owner.attachOperation(CHAT_MESSAGE);
    }

    public ChatBoxPeripheral(PeripheralTileEntity<?> tileEntity) {
        this(new TileEntityPeripheralOwner<>(tileEntity));
    }

    public ChatBoxPeripheral(ITurtleAccess turtle, TurtleSide side) {
        this(new TurtlePeripheralOwner(turtle, side));
    }

    public ChatBoxPeripheral(IPocketAccess pocket) {
        this(new PocketPeripheralOwner(pocket));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.ENABLE_CHAT_BOX.get();
    }

    protected MethodResult withChatOperation(IPeripheralFunction<Object, MethodResult> function) throws LuaException {
        return withOperation(CHAT_MESSAGE, null, null, function, null);
    }

    private String appendPrefix(String prefix, String brackets, String color) {
        if(prefix.isEmpty())
            prefix = APConfig.PERIPHERALS_CONFIG.DEFAULT_CHAT_BOX_PREFIX.get();
        if(brackets.isEmpty())
            prefix = "[]";
        IFormattableTextComponent formattablePrefix = null;
        try {
            formattablePrefix = ITextComponent.Serializer.fromJson(prefix);
        } catch (JsonSyntaxException exception) {
            AdvancedPeripherals.debug("Non json prefix, using plain text instead.");
        } finally {
            if (formattablePrefix != null)
                prefix = formattablePrefix.getString();
        }
        return String.format(PREFIX_FORMAT, prefix).replace("[", color + (brackets.isEmpty() ? "" : brackets.charAt(0)) + "\u00a7r").replace("]", color + (brackets.isEmpty() ? "" : brackets.charAt(1)) + "\u00a7r");
    }

    /**
     * @param player   the current player to check
     * @param argument the uuid or the name of the player
     * @return true if the player belongs to the name or the uuid
     */
    private boolean isPlayer(ServerPlayerEntity player, String argument) {
        if (player.getUUID().toString().equals(argument))
            return true;
        return player.getName().getString().equals(argument);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedMessage(@Nonnull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            int range = arguments.optInt(4, -1);
            IFormattableTextComponent component = ITextComponent.Serializer.fromJson(message);
            if (component == null)
                return MethodResult.of(null, "incorrect json");
            IFormattableTextComponent preparedMessage = new StringTextComponent(appendPrefix(arguments.optString(1, APConfig.PERIPHERALS_CONFIG.DEFAULT_CHAT_BOX_PREFIX.get()), arguments.optString(2, "[]"), arguments.optString(3, ""))).append(component);
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (range != -1) {
                    if (CoordUtil.isInRange(getPos(), getWorld(), player, range))
                        player.sendMessage(preparedMessage, Util.NIL_UUID);
                } else {
                    player.sendMessage(preparedMessage, Util.NIL_UUID);
                }
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendMessage(@Nonnull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            int range = arguments.optInt(4, -1);
            IFormattableTextComponent preparedMessage = new StringTextComponent(appendPrefix(arguments.optString(1, APConfig.PERIPHERALS_CONFIG.DEFAULT_CHAT_BOX_PREFIX.get()), arguments.optString(2, "[]"), arguments.optString(3, ""))).append(message);
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (range != -1) {
                    if (CoordUtil.isInRange(getPos(), getWorld(), player, range))
                        player.sendMessage(preparedMessage, Util.NIL_UUID);
                } else {
                    player.sendMessage(preparedMessage, Util.NIL_UUID);
                }
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendFormattedMessageToPlayer(@Nonnull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            String playerName = arguments.getString(1);
            int range = arguments.optInt(5, -1);
            IFormattableTextComponent component = ITextComponent.Serializer.fromJson(message);
            if (component == null)
                return MethodResult.of(null, "incorrect json");
            IFormattableTextComponent preparedMessage = new StringTextComponent(appendPrefix(arguments.optString(2, APConfig.PERIPHERALS_CONFIG.DEFAULT_CHAT_BOX_PREFIX.get()), arguments.optString(3, "[]"), arguments.optString(4, ""))).append(component);
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (isPlayer(player, playerName)) {
                    if (range != -1) {
                        if (CoordUtil.isInRange(getPos(), getWorld(), player, range))
                            player.sendMessage(preparedMessage, Util.NIL_UUID);
                    } else {
                        player.sendMessage(preparedMessage, Util.NIL_UUID);
                    }
                }
            }
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult sendMessageToPlayer(@Nonnull IArguments arguments) throws LuaException {
        return withChatOperation(ignored -> {
            String message = arguments.getString(0);
            String playerName = arguments.getString(1);
            int range = arguments.optInt(5, -1);
            IFormattableTextComponent preparedMessage = new StringTextComponent(appendPrefix(arguments.optString(2, APConfig.PERIPHERALS_CONFIG.DEFAULT_CHAT_BOX_PREFIX.get()), arguments.optString(3, "[]"), arguments.optString(4, ""))).append(message);
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (isPlayer(player, playerName)) {
                    if (range != -1) {
                        if (CoordUtil.isInRange(getPos(), getWorld(), player, range))
                            player.sendMessage(preparedMessage, Util.NIL_UUID);
                    } else {
                        player.sendMessage(preparedMessage, Util.NIL_UUID);
                    }
                }
            }
            return MethodResult.of(true);
        });
    }


}
