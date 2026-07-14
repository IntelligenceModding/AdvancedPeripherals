package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.filesystem.WritableMount;
import dan200.computercraft.api.peripheral.IComputerAccess;

import java.util.function.Function;

public final class CCMountUtil {
    private CCMountUtil() {}

    public static String mountDisk(IComputerAccess computer, Mount mount) {
        String path = null;
        Function<String, String> mounter;
        if (mount instanceof WritableMount writableMount) {
            mounter = (name) -> computer.mountWritable(name, writableMount);
        } else {
            mounter = (name) -> computer.mount(name, mount);
        }
        int i = 0;
        do {
            i++;
            String name = i == 1 ? "disk" : "disk" + i;
            path = mounter.apply(name);
        } while (path == null);
        return path;
    }
}
