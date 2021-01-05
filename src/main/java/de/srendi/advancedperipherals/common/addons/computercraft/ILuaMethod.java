package de.srendi.advancedperipherals.common.addons.computercraft;

public interface ILuaMethod {

    String getMethodName();

    Object[] call(Object[] args);

}
