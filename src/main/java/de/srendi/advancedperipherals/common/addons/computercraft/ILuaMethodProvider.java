package de.srendi.advancedperipherals.common.addons.computercraft;

public interface ILuaMethodProvider {

    void addLuaMethods(LuaMethodRegistry registry);

    LuaMethodRegistry getLuaMethodRegistry();

    String getPeripheralType();

}
