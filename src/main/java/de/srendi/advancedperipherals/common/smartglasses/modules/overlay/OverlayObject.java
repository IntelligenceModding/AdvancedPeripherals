package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class OverlayObject {

    private boolean enabled = true;
    private final String id;
    private final OverlayModule module;

    public OverlayObject(String id, OverlayModule module) {
        this.id = id;
        this.module = module;
    }

    @LuaFunction
    public final String getId() {
        return id;
    }

    public OverlayModule getModule() {
        return module;
    }

    @LuaFunction
    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @LuaFunction
    public final boolean isEnabled() {
        return enabled;
    }

    public void mapProperties(IArguments arguments) throws LuaException {
        try {
            Map<String, Object> properties = arguments.getTable(1).entrySet().stream()
                    .filter(entry -> entry.getKey() instanceof String)
                    .collect(Collectors.toMap(entry -> (String) entry.getKey(), Map.Entry::getValue));

            // Get the fields of this class and the proper child class like `Panel`
            Field[] childFields = this.getClass().getDeclaredFields();
            Field[] superFields = this.getClass().getSuperclass().getDeclaredFields();

            // Combine the fields into one array
            Field[] allFields = ArrayUtils.addAll(childFields, superFields);

            for (Field field : allFields) {
                if (properties.containsKey(field.getName())) {
                    // Make the field accessible
                    field.setAccessible(true);

                    // Set the value of the field
                    field.set(this, properties.get(field.getName()));
                }
            }
        } catch (LuaException | IllegalAccessException exception) {
            if (APConfig.GENERAL_CONFIG.enableDebugMode.get())
                AdvancedPeripherals.exception("An error occurred while mapping properties.", exception);
            throw new LuaException("An error occurred while mapping properties.");
        }
    }
}
