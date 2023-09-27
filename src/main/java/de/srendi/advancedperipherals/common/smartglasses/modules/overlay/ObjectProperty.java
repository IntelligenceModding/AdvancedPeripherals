package de.srendi.advancedperipherals.common.smartglasses.modules.overlay;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectProperty {

    int minInt() default Integer.MIN_VALUE;

    int maxInt() default Integer.MAX_VALUE;

    double maxDecimal() default Double.MAX_VALUE;

    double minDecimal() default Double.MIN_VALUE;

    int maxString() default Integer.MAX_VALUE;
}
