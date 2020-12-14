package de.srendi.advancedperipherals.addons.computercraft;

public interface ILuaMethodProvider {

    void addLuaMethods(LuaMethodRegistry registry);

    LuaMethodRegistry getLuaMethodRegistry();

    String getPeripheralType();

}
