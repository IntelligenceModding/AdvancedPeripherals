package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.core.apis.TableHelper;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.Map;

import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.DIG;
import static de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation.USE_ON_BLOCK;

public class AutomataBlockHandPlugin extends AutomataCorePlugin {

    public AutomataBlockHandPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @Override
    public @Nullable IPeripheralOperation<?>[] getOperations() {
        return new IPeripheralOperation[]{DIG, USE_ON_BLOCK};
    }

    @LuaFunction(mainThread = true)
    public final MethodResult digBlock(@NotNull IArguments arguments) throws LuaException {
        Map<?, ?> opts = arguments.count() > 0 ? arguments.getTable(0) : Collections.emptyMap();
        boolean sneak = TableHelper.optBooleanField(opts, "sneak", false);
        float yaw = opts != null ? (float) TableHelper.optNumberField(opts, "yaw", 0) : 0;
        float pitch = opts != null ? (float) TableHelper.optNumberField(opts, "pitch", 0) : 0;
        return automataCore.withOperation(DIG, context -> {
            TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            Pair<Boolean, String> result = owner.withPlayer(APFakePlayer.wrapActionWithShiftKey(sneak, APFakePlayer.wrapActionWithRot(yaw, pitch, APFakePlayer::digBlock)));
            if (!result.getLeft()) {
                return MethodResult.of(false, result.getRight());
            }
            if (automataCore.hasAttribute(AutomataCorePeripheral.ATTR_STORING_TOOL_DURABILITY)) {
                selectedTool.setDamageValue(previousDamageValue);
            }
            return MethodResult.of(true, result.getRight());
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult useOnBlock(@NotNull IArguments arguments) throws LuaException {
        Map<?, ?> opts = arguments.count() > 0 ? arguments.getTable(0) : Collections.emptyMap();
        boolean sneak = TableHelper.optBooleanField(opts, "sneak", false);
        float yaw = opts != null ? (float) TableHelper.optNumberField(opts, "yaw", 0) : 0;
        float pitch = opts != null ? (float) TableHelper.optNumberField(opts, "pitch", 0) : 0;
        return automataCore.withOperation(USE_ON_BLOCK, context -> {
            TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            InteractionResult result = owner.withPlayer(APFakePlayer.wrapActionWithShiftKey(sneak, APFakePlayer.wrapActionWithRot(yaw, pitch, APFakePlayer::useOnBlock)));
            if (automataCore.hasAttribute(AutomataCorePeripheral.ATTR_STORING_TOOL_DURABILITY)) {
                selectedTool.setDamageValue(previousDamageValue);
            }
            return MethodResult.of(result.consumesAction(), result.toString());
        });
    }

}
