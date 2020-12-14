package de.srendi.advancedperipherals.addons.computercraft;

public interface ILuaMethod {
    /**
     * Returns the name of the Lua Method.
     *
     * @return the Name as String
     */
    String getMethodName();

    /**
     * Used to call the Method.
     *
     * @param args
     * @return
     */
    Object[] call(Object[] args);

}
