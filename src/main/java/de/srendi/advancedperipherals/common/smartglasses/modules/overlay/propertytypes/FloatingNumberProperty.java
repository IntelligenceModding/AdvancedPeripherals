package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ObjectProperty(FloatingNumberType.class)
public @interface FloatingNumberProperty {
    double min() default Double.NEGATIVE_INFINITY;
    double max() default Double.POSITIVE_INFINITY;
}
