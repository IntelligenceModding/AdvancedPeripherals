package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.OverlayObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public class FloatingNumberType implements PropertyType<Number, FloatingNumberProperty> {
    public double min;
    public double max;
    public boolean continous;
    public boolean lerp;

    @Override
    public void init(FloatingNumberProperty property) {
        min = property.min();
        max = property.max();
        continous = property.continous();
        lerp = property.lerp();
    }

    @Override
    public boolean checkIsValid(Object value) {
        return value instanceof Float || value instanceof Double;
    }

    @Override
    public StreamCodec<ByteBuf, ? extends Number> codec(Class<? extends Number> type) {
        if (type == Float.TYPE || type == Float.class) {
            return ByteBufCodecs.FLOAT;
        }
        if (type == Double.TYPE || type == Double.class) {
            return ByteBufCodecs.DOUBLE;
        }
        throw new IllegalArgumentException("Unexpected field type: " + type);
    }

    @Override
    public Number fixValue(Number value) {
        if (value instanceof Float) {
            float v = value.floatValue();
            if (continous) {
                float range = (float) (max - min);
                return Float.valueOf(((v - (float) min) % range + range) % range + (float) min);
            }
            return Math.min(Math.max(value.floatValue(), (float) min), (float) max);
        }
        double v = value.doubleValue();
        if (continous) {
            double range = max - min;
            return ((v - min) % range + range) % range + min;
        }
        return Math.min(Math.max(v, min), max);
    }

    @Override
    @Nullable
    public OverlayObject.FieldLerper<? extends Number> getLerper(Class<? extends Number> type) {
        if (!lerp) {
            return null;
        }
        if (type == Float.TYPE || type == Float.class) {
            return continous ? OverlayObject.FieldLerper.continousFloat((float) min, (float) max) : OverlayObject.FieldLerper.FLOAT;
        }
        if (type == Double.TYPE || type == Double.class) {
            return continous ? OverlayObject.FieldLerper.continousDouble(min, max) : OverlayObject.FieldLerper.DOUBLE;
        }
        return null;
    }
}
