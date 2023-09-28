package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes.PropertyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a field as a property. It is used for the properties of lua objects.
 * Fields annotated with @ObjectProperty will be accessible in lua via the arguments/the filter table for the specific object.
 * @see PropertyType
 * @see OverlayObject
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ObjectProperty {

    Class<? extends PropertyType<?>> type();

}
