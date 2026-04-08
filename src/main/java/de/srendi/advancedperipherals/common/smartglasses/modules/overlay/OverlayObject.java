package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import com.google.common.collect.ImmutableMap;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.IDynamicLuaObject;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanProperty;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.BooleanType;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes.PropertyType;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public abstract class OverlayObject implements IDynamicLuaObject {

    private final FieldWithPropertyType[] fields;
    private final String[] getterSetterNames;
    private final Map<String, FieldWithPropertyType> propertiesMap;

    @BooleanProperty
    private boolean enabled = true;

    private int id;
    private OverlayModule module;
    private UUID player;

    public OverlayObject(OverlayModule module) {
        ImmutableMap.Builder<String, FieldWithPropertyType> properties = ImmutableMap.builder();
        for (Field field : FieldUtils.getAllFieldsList(this.getClass())) {
            String fieldName = field.getName();
            ObjectProperty objectProperty = null;
            Annotation propertyAnnotation = null;
            for (Annotation annotation : field.getAnnotations()) {
                objectProperty = annotation.annotationType().getAnnotation(ObjectProperty.class);
                if (objectProperty != null) {
                    propertyAnnotation = annotation;
                    break;
                }
            }
            if (objectProperty == null) {
                continue;
            }
            @SuppressWarnings("rawtypes") PropertyType propertyType = PropertyType.of(objectProperty);
            if (propertyType == null) {
                throw new IllegalStateException("Invalid property type for field " + fieldName);
            }
            propertyType.init(propertyAnnotation);
            try {
                field.setAccessible(true);
            } catch (InaccessibleObjectException exception) {
                AdvancedPeripherals.exception("An error occurred while initializing properties.", exception);
                throw new IllegalStateException("Field " + fieldName + " is inaccessible");
            }
            properties.put(fieldName, new FieldWithPropertyType(field, propertyType));
        }
        this.propertiesMap = properties.build();
        this.fields = this.propertiesMap.values().toArray(FieldWithPropertyType[]::new);
        List<String> getterSetterNames = new ArrayList<>();
        for (FieldWithPropertyType propField : this.fields) {
            String name = propField.field().getName();
            String nameCap = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
            getterSetterNames.add(
                propField.type() instanceof BooleanType bType
                    ? bType.getGetterPrefix() == null
                        ? name
                        : bType.getGetterPrefix() + nameCap
                    : "get" + nameCap
            );
            getterSetterNames.add("set" + nameCap);
        }
        this.getterSetterNames = getterSetterNames.toArray(String[]::new);
        this.module = module;
    }

    /**
     * For clientside initialization
     */
    public OverlayObject(UUID player) {
        this.player = player;
        this.fields = null;
        this.getterSetterNames = null;
        this.propertiesMap = null;
    }

    @NotNull
    public abstract OverlayObjectType<?> getType();

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OverlayModule getModule() {
        return this.module;
    }

    public UUID getPlayer() {
        return this.player;
    }

    @LuaFunction("type")
    public final String getTypeLua() {
        return APRegistration.OVERLAY_OBJECTS.getRegistry().get().getKey(this.getType()).toString();
    }

    @LuaFunction("getId")
    public final int getIdLua() {
        return this.getId();
    }

    protected abstract void tryAutoUpdate();

    @Override
    public String[] getMethodNames() {
        return this.getterSetterNames;
    }

    @Override
    public MethodResult callMethod(ILuaContext context, int methodIndex, IArguments args) throws LuaException {
        boolean isGetter = methodIndex % 2 == 0;
        int fieldIndex = methodIndex / 2;
        FieldWithPropertyType propField = this.fields[fieldIndex];
        if (isGetter) {
            try {
                return MethodResult.of(propField.field().get(this));
            } catch (IllegalAccessException e) {
                throw new LuaException("Cannot read field " + propField.field().getName());
            }
        }
        Object value = args.get(0);
        propField.setFor(this, value);
        this.tryAutoUpdate();
        return MethodResult.of();
    }

    /**
     * Maps properties from the provided table to the fields of this class.
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
     * @param initFields the LuaTable containing properties to be mapped
     * @throws LuaException if an error occurs during the mapping of properties
     * @see IArguments
     * @see ObjectProperty
     * @see PropertyType
     */
    public void setPropertiesFromTable(LuaTable<?, ?> initFields) throws LuaException {
        for (Map.Entry<?, ?> entry : initFields.entrySet()) {
            if (!(entry.getKey() instanceof String fieldName)) {
                continue;
            }
            FieldWithPropertyType propField = this.propertiesMap.get(fieldName);
            if (propField == null) {
                AdvancedPeripherals.debug("Unknown field name {} for class {}", fieldName, this.getClass());
                continue;
            }
            propField.setFor(this, entry.getValue());
        }
    }

    @MustBeInvokedByOverriders
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.id);
        buffer.writeBoolean(this.enabled);
    }

    @MustBeInvokedByOverriders
    public void decode(FriendlyByteBuf buffer) {
        this.id = buffer.readInt();
        this.enabled = buffer.readBoolean();
    }

    @Override
    public String toString() {
        return "OverlayObject{" +
                "id=" + id +
                ", enabled=" + enabled +
                ", module=" + module +
                ", player=" + player +
                '}';
    }

    /**
     * Casts the given value to the type of the provided field.
     * Can be overwritten if the desired casting is not supported.
     *
     * @param field the field object representing the type to cast to
     * @param value the value to be casted
     * @return the casted value
     */
    private static Object castValueToFieldType(Field field, Object value) {
        if (value == null) {
            return null;
        }

        Class<?> fieldType = field.getType();

        if (fieldType.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (value instanceof Boolean bool) {
            if (fieldType == Boolean.TYPE) {
                return bool;
            }
        } else if (value instanceof Number number) {
            if (fieldType == Double.TYPE) {
                return number.doubleValue();
            }
            if (fieldType == Float.TYPE) {
                return number.floatValue();
            }
            if (fieldType == Long.TYPE) {
                return Math.round(number.doubleValue());
            }
            if (fieldType == Integer.TYPE) {
                return (int) Math.round(number.doubleValue());
            }
            if (fieldType == Short.TYPE) {
                return (short) Math.round(number.doubleValue());
            }
            if (fieldType == Byte.TYPE) {
                return (byte) Math.round(number.doubleValue());
            }
        }
        AdvancedPeripherals.debug(Level.WARN, "The field type {} is not supported for the value {}.", fieldType.getName(), value);
        return value;
    }

    private record FieldWithPropertyType(Field field, PropertyType<?, ?> type) {
        public void setFor(OverlayObject obj, Object value) throws LuaException {
            value = castValueToFieldType(this.field, value);
            if (!this.type.checkIsValid(value)) {
                throw new LuaException("The value " + value + " is not valid for " + this.field.getName());
            }
            value = ((PropertyType) this.type).fixValue(value);

            try {
                this.field.set(obj, value);
            } catch (IllegalAccessException exception) {
                throw new IllegalStateException("Cannot set value for " + this.field.getName(), exception);
            }
        }
    }
}
