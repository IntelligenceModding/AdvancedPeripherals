package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ObjectProperty(type = BooleanType.class)
public @interface BooleanProperty {

}
