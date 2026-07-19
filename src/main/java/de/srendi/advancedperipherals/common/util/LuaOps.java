package de.srendi.advancedperipherals.common.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LuaOps implements DynamicOps<Object> {
    public static final LuaOps INSTANCE = new LuaOps();

    protected LuaOps() {}

    @Override
    public Object empty() {
        return null;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, Object input) {
        if (input == null) {
            return outOps.empty();
        }
        if (input instanceof List<?> list) {
            return this.convertList(outOps, list);
        }
        if (input instanceof Map<?, ?> map) {
            return this.convertMap(outOps, map);
        }
        if (input instanceof Number number) {
            return outOps.createNumeric(number);
        }
        if (input instanceof String string) {
            return outOps.createString(string);
        }
        throw new IllegalStateException("Unknown Lua type: " + input.getClass());
    }

    @Override
    public DataResult<Number> getNumberValue(Object input) {
        if (input instanceof Number number) {
            return DataResult.success(number);
        }
        if (input instanceof String strNum) {
            try {
                return DataResult.success(Long.valueOf(strNum));
            } catch (NumberFormatException e) {
                return DataResult.error(() -> "Not a number");
            }
        }
        return DataResult.error(() -> "Not a number");
    }

    @Override
    public Object createNumeric(Number number) {
        if (number instanceof Long longNum) {
            return this.createLong(longNum.longValue());
        }
        return number;
    }

    @Override
    public Object createLong(long value) {
        double dValue = value;
        if ((long) dValue != value) {
            return Long.toString(value);
        }
        return Double.valueOf(dValue);
    }

    @Override
    public DataResult<String> getStringValue(Object input) {
        return input instanceof String string
            ? DataResult.success(string)
            : DataResult.error(() -> "Not a string");
    }

    @Override
    public Object createString(String value) {
        return value;
    }

    @Override
    public DataResult<Object> mergeToList(Object list, Object value) {
        if (list instanceof List<?> list1) {
            List<Object> result = new ArrayList<>(list1.size() + 1);
            result.addAll(list1);
            result.add(value);
            return DataResult.success(result);
        }
        if (list == null) {
            return DataResult.success(List.of(value));
        }
        return DataResult.error(() -> "mergeToList called with not a list");
    }

    @Override
    public DataResult<Object> mergeToList(Object list, List<Object> value) {
        if (list instanceof List<?> list1) {
            List<Object> result = new ArrayList<>(list1.size() + 1);
            result.addAll(list1);
            result.addAll(value);
            return DataResult.success(result);
        }
        if (list == null) {
            return DataResult.success(List.copyOf(value));
        }
        return DataResult.error(() -> "mergeToList called with not a list");
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, Object key, Object value) {
        if (map instanceof Map<?, ?> map1) {
            Map<Object, Object> result = new HashMap<>(map1);
            result.put(key, value);
            return DataResult.success(result);
        }
        if (map == null) {
            return DataResult.success(Map.of(key, value));
        }
        return DataResult.error(() -> "mergeToMap called with not a map");
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, Map<Object, Object> values) {
        if (map instanceof Map<?, ?> map1) {
            Map<Object, Object> result = new HashMap<>(map1);
            result.putAll(values);
            return DataResult.success(result);
        }
        if (map == null) {
            return DataResult.success(Map.copyOf(values));
        }
        return DataResult.error(() -> "mergeToMap called with not a map");
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, MapLike<Object> values) {
        if (map instanceof Map<?, ?> map1) {
            Map<Object, Object> result = new HashMap<>(map1);
            values.entries().forEach((pair) -> result.put(pair.getFirst(), pair.getSecond()));
            return DataResult.success(result);
        }
        if (map == null) {
            return DataResult.success(this.createMap(values.entries()));
        }
        return DataResult.error(() -> "mergeToMap called with not a map");
    }

    @Override
    public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object input) {
        return input instanceof Map<?, ?> map
            ? DataResult.success(map.entrySet().stream().map((entry) -> Pair.of(entry.getKey(), entry.getValue())))
            : DataResult.error(() -> "Not a map");
    }

    @Override
    public Object createMap(Stream<Pair<Object, Object>> map) {
        return Map.ofEntries(map.map((pair) -> Map.entry(pair.getFirst(), pair.getSecond())).toArray(Map.Entry[]::new));
    }

    @Override
    public DataResult<Stream<Object>> getStream(Object input) {
        return input instanceof List<?> list
            ? DataResult.success((Stream<Object>) list.stream())
            : DataResult.error(() -> "Not a list");
    }

    @Override
    public Object createList(Stream<Object> input) {
        return input.toList();
    }

    @Override
    public Object remove(Object input, String key) {
        if (input instanceof Map<?, ?> map) {
            return Map.ofEntries(map.entrySet().stream().filter((entry) -> !key.equals(entry.getKey())).toArray(Map.Entry[]::new));
        }
        return input;
    }
}
