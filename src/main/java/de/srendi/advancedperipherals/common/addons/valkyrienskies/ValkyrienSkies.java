package de.srendi.advancedperipherals.common.addons.valkyrienskies;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ValkyrienSkies {
    private ValkyrienSkies() {}

    public static boolean isBlockOnShip(Level level, BlockPos pos) {
        return VSGameUtilsKt.isBlockInShipyard(level, pos);
    }

    public static Vec3 transformToWorldPos(Level level, BlockPos blockPos, Vec3 pos) {
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(level, blockPos);
        if (ship == null) {
            return pos;
        }
        Vector3d newPos = ship.getShipToWorld().transformPosition(new Vector3d(pos.x, pos.y, pos.z));
        return new Vec3(newPos.x, newPos.y, newPos.z);
    }

    public static Vec3 transformToWorldDir(Level level, BlockPos blockPos, Vec3 dir) {
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(level, blockPos);
        if (ship == null) {
            return dir;
        }
        Vector3d newDir = ship.getShipToWorld().transformDirection(new Vector3d(dir.x, dir.y, dir.z));
        return new Vec3(newDir.x, newDir.y, newDir.z);
    }

    public static Matrix4dc getTransformation(Level level, BlockPos blockPos) {
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(level, blockPos);
        if (ship == null) {
            return new Matrix4d();
        }
        return ship.getShipToWorld();
    }

    public static void putShipInfo(Level level, BlockPos blockPos, Map<String, Object> data) {
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(level, blockPos);
        if (ship == null) {
            return;
        }
        data.put("shipId", ship.getId());
        data.put("shipName", ship.getSlug());
    }

    public static List<ServerShip> getNearbyShips(ServerLevel level, Vec3 pos, double radius) {
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(level, BlockPos.containing(pos));
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
