package org.renwixx.pocketdimension.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

public class TeleportUtils {

    /**
     * Teleports a player to the specified target position.
     */
    public static void teleportPlayer(ServerPlayerEntity player, ServerWorld targetWorld, Vec3d targetPos, float yaw,
            float pitch) {
        TeleportTarget target = new TeleportTarget(
                targetWorld,
                targetPos,
                player.getVelocity(),
                yaw, pitch,
                TeleportTarget.NO_OP);

        player.stopRiding();
        player.teleportTo(target);
    }
}
