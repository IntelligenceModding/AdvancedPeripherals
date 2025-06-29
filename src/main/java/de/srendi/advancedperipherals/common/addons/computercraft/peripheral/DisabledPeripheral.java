package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.IDynamicLuaObject;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

public class DisabledPeripheral implements IDynamicPeripheral {
	private static final MethodResult TRUE_RESULT = MethodResult.of(true);

	private final IPeripheral basePeripheral;
	private final String[] methods;

	public DisabledPeripheral(IPeripheral basePeripheral) {
		this.basePeripheral = basePeripheral;
		Stream.Builder<String> builder = Stream.builder();
		builder.add("peripheralDisabled");
		for (Method method : basePeripheral.getClass().getMethods()) {
			LuaFunction annotation = method.getAnnotation(LuaFunction.class);
			if (annotation == null) {
				continue;
			}
			String[] names = annotation.value();
			if (names.length == 0) {
				builder.add(method.getName());
			} else {
				for (String name : names) {
					builder.add(name);
				}
			}
		}
		Stream<String> methodStream = builder.build();
		if (basePeripheral instanceof IDynamicPeripheral dynPeripheral) {
			methodStream = Stream.concat(methodStream, Stream.of(dynPeripheral.getMethodNames()));
		}
		this.methods = methodStream.toArray(String[]::new);
	}

	@Override
	public String getType() {
		return this.basePeripheral.getType();
	}

	@Override
	public Object getTarget() {
		return this.basePeripheral.getTarget();
	}

	@Override
	public boolean equals(IPeripheral other) {
		return other instanceof DisabledPeripheral disabled && this.basePeripheral.equals(disabled.basePeripheral);
	}

	@Override
	public String[] getMethodNames() {
		return this.methods;
	}

	@Override
	public MethodResult callMethod(IComputerAccess computer, ILuaContext context, int method, IArguments arguments) throws LuaException {
		if (method == 0) {
			return TRUE_RESULT;
		}
		throw new LuaException("This peripheral is disabled, please contact server administrator if you want to use it");
	}
}
