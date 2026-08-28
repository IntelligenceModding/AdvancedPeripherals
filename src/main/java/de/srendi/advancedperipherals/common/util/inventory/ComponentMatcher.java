package de.srendi.advancedperipherals.common.util.inventory;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.util.StringRepresentable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public enum ComponentMatcher implements StringRepresentable {
    PATCH("patch") {
        @Override
        public boolean test(DataComponentMap data, DataComponentPatch matcher) {
            if (data instanceof PatchedDataComponentMap patched) {
                return patched.asPatch().equals(matcher);
            }
            return EXACT.test(data, matcher);
        }
    },
    EXACT("exact") {
        @Override
        public boolean test(DataComponentMap data, DataComponentPatch matcher) {
            if (data.size() != matcher.size()) {
                return false;
            }
            for (TypedDataComponent<?> comp : data) {
                Optional<?> v = matcher.get(comp.type());
                if (v == null || v.isEmpty()) {
                    return false;
                }
                if (!v.get().equals(comp.value())) {
                    return false;
                }
            }
            return true;
        }
    },
    SUBSET("subset") {
        @Override
        public boolean test(DataComponentMap data, DataComponentPatch matcher) {
            for (Map.Entry<DataComponentType<?>, Optional<?>> entry : matcher.entrySet()) {
                // TODO: subset sub maps as well. Might require codec encode into java map & values first.
                if (!Objects.equals(data.get(entry.getKey()), entry.getValue().orElse(null))) {
                    return false;
                }
            }
            return true;
        }
    };

    public static final EnumCodec<ComponentMatcher> CODEC = StringRepresentable.fromEnum(ComponentMatcher::values);

    private String name;

    ComponentMatcher(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public abstract boolean test(DataComponentMap data, DataComponentPatch matcher);
}
