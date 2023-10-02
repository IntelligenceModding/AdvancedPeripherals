package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes.PropertyType;
import de.srendi.advancedperipherals.common.util.StringUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Level;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


public abstract class OverlayObject {

    @BooleanProperty
    private boolean enabled = true;

    private final String id;
    private OverlayModule module;
    private UUID player;

    public OverlayObject(String id, OverlayModule module, IArguments arguments) {
        this.id = id;
        this.module = module;
    }

    /**
     * For clientside initialization
     */
    public OverlayObject(String id, UUID player) {
        this.id = id;
        this.player = player;
    }

    @LuaFunction
    public final String getId() {
        return id;
    }

    public OverlayModule getModule() {
        return module;
    }

    public UUID getPlayer() {
        return player;
    }

    @LuaFunction
    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @LuaFunction
    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * Maps properties from the provided arguments to the fields of this class.
     * <p>
     * This method uses Java Reflection to map properties from IArguments to the fields of the classes.
     * It only maps properties that have the annotation {@link ObjectProperty}. If a field does not have this annotation,
     * a warning message is logged and the method returns.
     * <p>
     * If a property is valid, its value is cast to the field type and set as the new value of the field.
     * If a property is not valid, a warning message is logged and the method returns.
     * <p>
     * If an error occurs during the mapping of properties, an exception message is logged and a LuaException is thrown.
     *
     * @param arguments the IArguments containing properties to be mapped
     * @throws LuaException if an error occurs during the mapping of properties
     * @see IArguments
     * @see ObjectProperty
     * @see PropertyType
     */
    public void reflectivelyMapProperties(IArguments arguments) throws LuaException {
        if (arguments.optTable(1).isEmpty())
            return;

        try {
            Map<String, Object> properties = arguments.optTable(1).get().entrySet().stream()
                    .filter(entry -> entry.getKey() instanceof String)
                    .collect(Collectors.toMap(entry -> (String) entry.getKey(), Map.Entry::getValue));

            Field[] allFields = FieldUtils.getAllFields(this.getClass());

            for (Field field : allFields) {
                if (properties.containsKey(field.getName())) {
                    var value = properties.get(field.getName());

                    Annotation[] fieldProperties = field.getAnnotations();
                    ObjectProperty objectProperty = null;
                    Annotation propertyAnnotation = null;

                    for (Annotation annotation : fieldProperties) {
                        objectProperty = annotation.annotationType().getAnnotation(ObjectProperty.class);
                        if (objectProperty != null) {
                            propertyAnnotation = annotation;
                            break;
                        }
                    }

                    if (objectProperty == null) {
                        AdvancedPeripherals.debug("The field " + field.getName() + " has no ObjectProperty annotation and can't be changed.", Level.WARN);
                        return;
                    }

                    PropertyType<?> propertyType = PropertyType.of(objectProperty);
                    if (propertyType != null) {
                        value = castValueToFieldType(field, value);
                        if (propertyType.checkIsValid(value)) {
                            propertyType.init(propertyAnnotation);
                            value = propertyType.mapValue(value);

                            // Make the field accessible
                            field.setAccessible(true);

                            // Set the value of the field
                            field.set(this, castValueToFieldType(field, value));

                        } else {
                            AdvancedPeripherals.debug("The value " + value + " is not valid for the field " + field.getName() + ".", Level.WARN);
                            return;
                        }
                    }
                }
            }
        } catch (LuaException | IllegalAccessException exception) {
            AdvancedPeripherals.exception("An error occurred while mapping properties.", exception);
            throw new LuaException("An error occurred while mapping properties.");
        }
    }

    /**
     * Casts the given value to the type of the provided field.
     * Can be overwritten if the desired casting is not supported.
     *
     * @param field the field object representing the type to cast to
     * @param value the value to be casted
     * @return the casted value
     */
    public Object castValueToFieldType(Field field, Object value) {
        Class<?> fieldType = field.getType();

        if (fieldType.isAssignableFrom(value.getClass())) {
            return value;
        } else if (fieldType.equals(Integer.TYPE)) {
            return Integer.valueOf(StringUtil.removeFloatingPoints(value.toString()));
        } else if (fieldType.equals(Double.TYPE)) {
            return Double.valueOf(value.toString());
        } else if (fieldType.equals(Boolean.TYPE)) {
            return Boolean.valueOf(value.toString());
        } else if (fieldType.equals(Long.TYPE)) {
            return Long.valueOf(StringUtil.removeFloatingPoints(value.toString()));
        } else if (fieldType.equals(Short.TYPE)) {
            return Short.valueOf(StringUtil.removeFloatingPoints(value.toString()));
        } else if (fieldType.equals(Byte.TYPE)) {
            return Byte.valueOf(StringUtil.removeFloatingPoints(value.toString()));
        } else if (fieldType.equals(Float.TYPE)) {
            return Float.valueOf(value.toString());
        } else {
            AdvancedPeripherals.debug("The field type " + fieldType.getName() + " is not supported for the value " + value + ".", Level.WARN);
        }
        return value;
    }

    protected void handle(NetworkEvent.Context context) {

    }

    protected void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(id);
        Entity entity = module.getAccess().getEntity();
        if(entity instanceof Player player) {
            buffer.writeBoolean(true);
            buffer.writeUUID(player.getUUID());
        } else {
            // Should theoretically never happen. But better safe than sorry
            buffer.writeBoolean(false);
        }
    }

}
