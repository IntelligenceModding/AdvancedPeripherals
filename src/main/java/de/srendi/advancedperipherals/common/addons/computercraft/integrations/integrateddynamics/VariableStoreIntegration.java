package de.srendi.advancedperipherals.common.addons.computercraft.integrations.integrateddynamics;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Integration;
import net.minecraftforge.common.util.LazyOptional;
import org.cyclops.integrateddynamics.api.block.IVariableContainer;
import org.cyclops.integrateddynamics.api.evaluate.EvaluationException;
import org.cyclops.integrateddynamics.api.evaluate.variable.IValue;
import org.cyclops.integrateddynamics.api.evaluate.variable.IVariable;
import org.cyclops.integrateddynamics.api.item.IVariableFacade;
import org.cyclops.integrateddynamics.capability.variablecontainer.VariableContainerConfig;
import org.cyclops.integrateddynamics.core.helper.NetworkHelpers;
import org.cyclops.integrateddynamics.core.item.OperatorVariableFacade;
import org.cyclops.integrateddynamics.tileentity.TileVariablestore;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class VariableStoreIntegration extends Integration<TileVariablestore> {
    @Override
    protected Class<TileVariablestore> getTargetClass() {
        return TileVariablestore.class;
    }

    @Override
    public VariableStoreIntegration getNewInstance() {
        return new VariableStoreIntegration();
    }

    @Override
    public String getType() {
        return "variableStore";
    }

    @LuaFunction
    public final Map<Integer, HashMap<String, Object>> list() {
        LazyOptional<IVariableContainer> lazyContainer = tileEntity.getCapability(VariableContainerConfig.CAPABILITY);
        return lazyContainer.map(container -> container.getVariableCache().entrySet().stream().map(entry -> {
            HashMap<String, Object> variableData = new HashMap<>(3);
            variableData.put("id", entry.getValue().getId());
            variableData.put("label", entry.getValue().getLabel());
            variableData.put("type", entry.getValue().getOutputType().getTypeName());
            variableData.put("dynamic", entry.getValue() instanceof OperatorVariableFacade);
            return new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), variableData);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))).orElse(new HashMap<>());
    }

    @LuaFunction
    public final MethodResult read(int slot) {
        LazyOptional<IVariableContainer> lazyContainer = tileEntity.getCapability(VariableContainerConfig.CAPABILITY);
        return lazyContainer.map(container -> {
            IVariableFacade facade = container.getVariableCache().get(slot);
            if (facade == null) {
                return MethodResult.of(null, String.format("Slot %d is empty", slot));
            }
            if (tileEntity.getNetwork() == null) {
                return MethodResult.of(null, "Integred Dynamic network is configured incorrect");
            }
            IVariable<IValue> variable = facade.getVariable(NetworkHelpers.getPartNetworkChecked(tileEntity.getNetwork()));
            if (variable == null) {
                return MethodResult.of(null, "Variable cannot be accessed");
            }
            IValue value;
            try {
                value = variable.getValue();
            } catch (EvaluationException e) {
                return MethodResult.of(null, e.getErrorMessage().toString());
            }
            HashMap<String, Object> valueData = new HashMap<>(4);
            valueData.put("type", value.getType().getTypeName());
            valueData.put("id", facade.getId());
            valueData.put("label", facade.getLabel());
            valueData.put("value", NBTUtil.toLua(value.getType().serialize(value)));
            valueData.put("dynamic", facade instanceof OperatorVariableFacade);
            return MethodResult.of(valueData);
        }).orElse(MethodResult.of(null, "Problem with access to variable container"));
    }
}
