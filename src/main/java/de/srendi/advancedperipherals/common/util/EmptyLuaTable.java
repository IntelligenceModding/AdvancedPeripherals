package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.ObjectLuaTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmptyLuaTable implements LuaTable<Object, Object> {

    public static final EmptyLuaTable INSTANCE = new EmptyLuaTable();

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Nullable
    @Override
    public Object get(Object key) {
        return null;
    }

    @NotNull
    @Override
    public Set<Object> keySet() {
        return Set.of();
    }

    @NotNull
    @Override
    public Collection<Object> values() {
        return List.of();
    }

    @NotNull
    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return Set.of();
    }

    public static LuaTable<Object, Object> orEmpty(@Nullable Map<?, ?> table) {
        if (table == null)
            return INSTANCE;
        return new ObjectLuaTable(table);
    }
}
