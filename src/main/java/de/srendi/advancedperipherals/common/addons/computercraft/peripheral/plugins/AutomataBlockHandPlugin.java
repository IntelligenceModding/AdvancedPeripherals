package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralOperation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

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
    public final MethodResult digBlock() throws LuaException {
        return automataCore.withOperation(DIG, context -> {
            TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            Pair<Boolean, String> result = owner.withPlayer(apFakePlayer -> apFakePlayer.digBlock(owner.getFacing().getOpposite()));
            if (!result.getLeft()) {
                return MethodResult.of(null, result.getRight());
            }
            if (automataCore.hasAttribute(AutomataCorePeripheral.ATTR_STORING_TOOL_DURABILITY))
                selectedTool.setDamageValue(previousDamageValue);
            return MethodResult.of(true);
        });
    }

    @LuaFunction(mainThread = true)
    public final MethodResult useOnBlock() throws LuaException {
        return automataCore.withOperation(USE_ON_BLOCK, context -> {
            TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
            ItemStack selectedTool = owner.getToolInMainHand();
            int previousDamageValue = selectedTool.getDamageValue();
            InteractionResult result = owner.withPlayer(APFakePlayer::useOnBlock);
            if (automataCore.hasAttribute(AutomataCorePeripheral.ATTR_STORING_TOOL_DURABILITY))
                selectedTool.setDamageValue(previousDamageValue);
            return MethodResult.of(true, result.toString());
        });
    }

}
