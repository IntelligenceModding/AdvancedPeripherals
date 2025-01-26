package de.srendi.advancedperipherals.common.addons.valkyrienskies;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.List;

public final class ValkyrienSkies {
    private ValkyrienSkies() {}

    public static List<ServerShip> getNearbyShips(ServerLevel level, Vec3 pos, double radius) {
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(level, new BlockPos(pos));
        if (ship != null) {
            Vector3d newPos = ship.getShipToWorld().transformPosition(new Vector3d(pos.x, pos.y, pos.z));
            pos = new Vec3(newPos.x, newPos.y, newPos.z);
        }
        List<Vector3d> shipPoses = VSGameUtilsKt.transformToNearbyShipsAndWorld(level, pos.x, pos.y, pos.z, radius);
        List<ServerShip> ships = new ArrayList<>(shipPoses.size());
        for (Vector3d p : shipPoses) {
            ServerShip s = VSGameUtilsKt.getShipManagingPos(level, p.x, p.y, p.z);
            if (ship == null || s.getId() != ship.getId()) {
                ships.add(s);
            }
        }
        return ships;
    }
}
