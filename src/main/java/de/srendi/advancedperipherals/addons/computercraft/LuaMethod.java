package de.srendi.advancedperipherals.addons.computercraft;

public class LuaMethod implements ILuaMethod {

    private final String methodName;

    protected LuaMethod(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Object[] call(Object[] args) {
        return new Object[0];
    }

    protected void requireArgs(Object[] args, int numArgs, String description) {
        if(!(args.length == numArgs)) {
            String.format("The Method %s needs %s arguments! %s", getMethodName(), numArgs, description);
        }
    }

    protected void requireNoArgs(Object[] args) {
        String.format("The Method %s needs no arguments! %s", getMethodName());
    }
}