package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;

import java.util.HashMap;
import java.util.Map;

public final class LuaArgsHelper {
    private LuaArgsHelper() {}

    public static final class Args {
        private final Map<Class<?>, Object> map = new HashMap<>();

        private Args() {}

        public <T> T get(Class<T> clazz) {
            return this.get(clazz, null);
        }

        public <T> T get(Class<T> clazz, T defaultValue) {
            return (T) this.map.getOrDefault(clazz, defaultValue);
        }
    }

    public static Args getUnorderedArgs(IArguments args, int start, Class<?>... classes) throws LuaException {
        int count = args.count();
        Args uargs = new Args();
        for (int i = start; i < count; i++) {
            Object v = args.get(i);
            if (v == null) {
                continue;
            }
            for (Class<?> c : classes) {
                if (c.isInstance(v)) {
                    if (uargs.map.put(c, v) != null) {
                        throw new LuaException("unexpected second argument instance with type " + c.getName() + " at #" + (i + 1));
                    }
                    break;
                }
            }
        }
        return uargs;
    }
}
