/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperation;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SingleOperationContext;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.lib.metaphysics.IAutomataCoreTier;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AutomataCorePeripheral extends BasePeripheral<TurtlePeripheralOwner> {

    public static final String ATTR_STORING_TOOL_DURABILITY = "storingToolDurability";

    private final IAutomataCoreTier tier;
    private final Map<String, Boolean> attributes = new HashMap<>();

    protected AutomataCorePeripheral(String type, ITurtleAccess turtle, TurtleSide side, IAutomataCoreTier tier) {
        super(type, new TurtlePeripheralOwner(turtle, side));
        owner.attachFuel(tier.getMaxFuelConsumptionRate());
        owner.attachOperation(possibleOperations());
        this.tier = tier;
    }

    public void addRotationCycle() {
        addRotationCycle(1);
    }

    public void addRotationCycle(int count) {
        DataStorageUtil.RotationCharge.addCycles(owner, count);
    }

    public List<IPeripheralOperation<?>> possibleOperations() {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> data = super.getPeripheralConfiguration();
        data.put("interactionRadius", getInteractionRadius());
        return data;
    }

    public final int getInteractionRadius() {
        return tier.getInteractionRadius();
    }

    public SingleOperationContext forUnknownDistance() {
        return new SingleOperationContext(1, getInteractionRadius());
    }

    public SingleOperationContext toDistance(BlockPos pos) {
        return new SingleOperationContext(1, getPos().distManhattan(pos));
    }

    public <T> MethodResult withOperation(IPeripheralOperation<T> operation, T context,
            IPeripheralFunction<T, MethodResult> function, IPeripheralCheck<T> check) throws LuaException {
        return withOperation(operation, context, check, function, ignored -> addRotationCycle());
    }

    public MethodResult withOperation(SingleOperation operation,
            IPeripheralFunction<SingleOperationContext, MethodResult> function) throws LuaException {
        return withOperation(operation, forUnknownDistance(), function, null);
    }

    public MethodResult withOperation(SingleOperation operation,
            IPeripheralFunction<SingleOperationContext, MethodResult> function,
            IPeripheralCheck<SingleOperationContext> check) throws LuaException {
        return withOperation(operation, forUnknownDistance(), function, check);
    }

    public boolean hasAttribute(String attribute) {
        return attributes.getOrDefault(attribute, false);
    }

    public void setAttribute(String attribute) {
        attributes.put(attribute, true);
    }
}
