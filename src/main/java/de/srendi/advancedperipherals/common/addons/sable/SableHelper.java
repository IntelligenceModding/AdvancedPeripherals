package de.srendi.advancedperipherals.common.addons.sable;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SableHelper {

    private SableHelper() {}

    public static Vec3 projectOutOfSubLevel(Level level, Vec3 pos) {
        return SableCompanion.INSTANCE.projectOutOfSubLevel(level, (Position) pos);
    }

    public static Vec3 transformDirectionOutOfSubLevel(Level level, BlockPos pos, Vec3 direction) {
        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level, pos);
        if (subLevel == null) {
            return direction;
        }
        return subLevel.logicalPose().transformNormal(direction);
    }

    public static boolean isOnSubLevel(Level level, BlockPos pos) {
        return SableCompanion.INSTANCE.getContaining(level, pos) != null;
    }

    public static Map<String, Object> getContainingSubLevel(Level level, BlockPos pos, Vec3 center) {
        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level, pos);
        if (subLevel == null) {
            return null;
        }
        return subLevelToObject(level, subLevel, center);
    }

    public static List<Map<String, Object>> scanSubLevels(Level level, Vec3 center, double radius) {
        BoundingBox3d bounds = new BoundingBox3d(
            center.x - radius, center.y - radius, center.z - radius,
            center.x + radius, center.y + radius, center.z + radius);

        List<Map<String, Object>> result = new ArrayList<>();
        for (SubLevelAccess subLevel : SableCompanion.INSTANCE.getAllIntersecting(level, bounds)) {
            result.add(subLevelToObject(level, subLevel, center));
        }
        return result;
    }

    private static Map<String, Object> subLevelToObject(Level level, SubLevelAccess subLevel, Vec3 center) {
        Pose3dc pose = subLevel.logicalPose();
        Vector3dc pos = pose.position();
        Quaterniondc rot = pose.orientation();
        BoundingBox3dc box = subLevel.boundingBox();
        Vector3d velocity = SableCompanion.INSTANCE.getVelocity(level, new Vector3d(pos));

        Map<String, Object> data = new HashMap<>();
        data.put("id", subLevel.getUniqueId().toString());
        String name = subLevel.getName();
        if (name != null) {
            data.put("name", name);
        }
        data.put("x", pos.x() - center.x);
        data.put("y", pos.y() - center.y);
        data.put("z", pos.z() - center.z);
        data.put("rotate", Map.of("x", rot.x(), "y", rot.y(), "z", rot.z(), "w", rot.w()));
        data.put("size", Map.of("x", box.maxX() - box.minX(), "y", box.maxY() - box.minY(), "z", box.maxZ() - box.minZ()));
        data.put("velocity", Map.of("x", velocity.x(), "y", velocity.y(), "z", velocity.z()));
        return data;
    }
}
