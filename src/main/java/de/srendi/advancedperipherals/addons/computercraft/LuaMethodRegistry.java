package de.srendi.advancedperipherals.addons.computercraft;

import org.apache.commons.lang3.Validate;

import java.util.*;

public class LuaMethodRegistry {

    private final List<ILuaMethod> luaMethods = new ArrayList<>();
    private final Map<String, Integer> luaMethodMap = new HashMap<>();  // index into luaMethods list
    private final ILuaMethodProvider luaMethodProvider;
    private String[] luaMethodNames = null;
    private boolean initiated = false;

    public LuaMethodRegistry(ILuaMethodProvider provider) {
        this.luaMethodProvider = provider;
    }

    private void initiate() {
        if (!initiated) {
            luaMethodProvider.addLuaMethods(this);
            initiated = true;
        }
    }

    public void registerLuaMethod(ILuaMethod method) {
        Integer index = luaMethodMap.get(method.getMethodName());

        if (index == null) {
            luaMethods.add(method);
            luaMethodMap.put(method.getMethodName(), luaMethods.size() - 1);
        } else {
            luaMethods.set(index, method);
        }
    }

    public String[] getMethodNames() {
        initiate();
        if (luaMethodNames == null) {
            luaMethodNames = new String[luaMethods.size()];
            Arrays.setAll(luaMethodNames, i -> luaMethods.get(i).getMethodName());
        }
        return luaMethodNames;
    }

    public ILuaMethod getMethod(String methodName) {
        initiate();
        Validate.isTrue(luaMethodMap.containsKey(methodName), "Attempt to get unregistered method '" + methodName + "'.");
        return luaMethods.get(luaMethodMap.get(methodName));
    }

    public ILuaMethod getMethod(int methodIndex) {
        initiate();
        return luaMethods.get(methodIndex);
    }

}
