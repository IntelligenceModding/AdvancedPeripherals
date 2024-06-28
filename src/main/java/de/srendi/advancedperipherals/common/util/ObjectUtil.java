package de.srendi.advancedperipherals.common.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ObjectUtil {
    /**
     * The same as {@link ObjectUtil#nullableValue(Object, Function)} but runs {@code onNotNull} if {@code value}
     * isn't null.
     *
     * @param value     nullable object to attempt to use
     * @param modifier  function to apply to {@code value} if {@code value} isn't null
     * @param onNotNull runnable to be run if {@code value} isn't null
     * @param <T>       nullable value type
     * @param <U>       returned type after modification of value
     * @return {@code value} with {@code modifier} applied to it if {@code value} isn't null, null otherwise
     */
    public static <T, U> U nullableValue(T value, @NotNull Function<T, U> modifier, @NotNull Runnable onNotNull) {
        if (value != null) {
            onNotNull.run();
            return modifier.apply(value);
        }
        return null;
    }

    /**
     * If {@code value} isn't null, {@code modifier} is applied to it before returning it. Useful to save
     * some lines of code and to stop {@code value} from polluting a higher scope.
     * <br><br>
     * Usage:
     * <pre>
     * {@code
     * nullableStringAcceptor(nullableValue(getNullableObject(), Object::toString));
     * }
     * </pre>
     * Alternative if not used (the null check is needed for the {@link Object#toString()} call):
     * <pre>
     * {@code
     * Object value = getNullableObject();
     * if (value != null)
     *     nullableStringAcceptor(value.toString());
     * }
     * </pre>
     * Alternative if not used that doesn't pollute scope (getNullableObject() is needlessly called twice):
     * <pre>
     * {@code
     * nullableStringAcceptor(getNullableObject() != null ? getNullableObject().toString() : null);
     * }
     * </pre>
     *
     * @param value    nullable object to attempt to use
     * @param modifier function to apply to {@code value} if {@code value} isn't null
     * @param <T>      nullable value type
     * @param <U>      returned type after modification of value
     * @return {@code value} with {@code modifier} applied to it if {@code value} isn't null, null otherwise
     */
    public static <T, U> U nullableValue(T value, @NotNull Function<T, U> modifier) {
        if (value != null) {
            return modifier.apply(value);
        }
        return null;
    }
}
