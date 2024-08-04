package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ObjectProperty(type = StringType.class)
public @interface StringProperty {

}
