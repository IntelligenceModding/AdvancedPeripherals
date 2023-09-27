package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;


public abstract class OverlayObject {

    @ObjectProperty
    private boolean enabled = true;
    private final String id;
    private final OverlayModule module;

    public OverlayObject(String id, OverlayModule module, IArguments arguments) throws LuaException {
        this.id = id;
        this.module = module;
        mapProperties(arguments);
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
        if(arguments.optTable(1).isEmpty())
            return;

        try {

            Map<String, Object> properties = arguments.optTable(1).get().entrySet().stream()
                    .filter(entry -> entry.getKey() instanceof String)
                    .collect(Collectors.toMap(entry -> (String) entry.getKey(), Map.Entry::getValue));

            Field[] allFields = FieldUtils.getAllFields(this.getClass());

            for (Field field : allFields) {
                if (properties.containsKey(field.getName())) {
                    Object value = properties.get(field.getName());

                    Class<?> type = field.getType();
                    if (field.getAnnotation(ObjectProperty.class) == null)
                        AdvancedPeripherals.debug("The field " + field.getName() + " has no ObjectProperty annotation and can't be chaned.", Level.WARN);

                    if ((type == Integer.class || type == int.class) && value instanceof Integer) {
                        int min = field.getAnnotation(ObjectProperty.class).minInt();
                        int max = field.getAnnotation(ObjectProperty.class).maxInt();
                        value = Math.min(Math.max((int) value, min), max);
                    } else if ((type == Double.class || type == double.class) && value instanceof Double) {
                        double min = field.getAnnotation(ObjectProperty.class).minDecimal();
                        double max = field.getAnnotation(ObjectProperty.class).maxDecimal();
                        value = Math.min(Math.max((double) value, min), max);
                    } else if (type == String.class && value instanceof String) {
                        int max = field.getAnnotation(ObjectProperty.class).maxString();
                        value = ((String) value).substring(0, Math.min(((String) value).length(), max));
                    } else {
                        throw new LuaException("The type " + field.getType() + " of " + field.getName() + " is not supported.");
                    }

                    // Make the field accessible
                    field.setAccessible(true);

                    // Set the value of the field
                    field.set(this, value);
                }
            }
        } catch (LuaException | IllegalAccessException exception) {
            if (APConfig.GENERAL_CONFIG.enableDebugMode.get())
                AdvancedPeripherals.exception("An error occurred while mapping properties.", exception);
            throw new LuaException("An error occurred while mapping properties.");
        }
    }
}
