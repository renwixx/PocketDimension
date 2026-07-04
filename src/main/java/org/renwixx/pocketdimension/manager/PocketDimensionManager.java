package org.renwixx.pocketdimension.manager;

import org.renwixx.pocketdimension.block.PocketBlockEntity;
import org.renwixx.pocketdimension.registry.ModDimensions;
import org.renwixx.pocketdimension.world.PocketState;
import org.renwixx.pocketdimension.world.RoomGenerator;
import org.renwixx.pocketdimension.util.TeleportUtils;
import org.renwixx.pocketdimension.config.PocketConfig;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.renwixx.pocketdimension.PocketDimensionMod;
import org.renwixx.pocketdimension.util.MessageCooldownManager;

public final class PocketDimensionManager {

    private PocketDimensionManager() {
    }

    /**
     * Handles the logic for a player entering a pocket dimension room from the
     * overworld.
     */
    public static void enterPocket(ServerPlayerEntity player, ServerWorld world, PocketBlockEntity pocketEntity) {
        ServerWorld pocketWorld = world.getServer().getWorld(ModDimensions.REALM);
        if (pocketWorld == null) {
            PocketDimensionMod.LOGGER.error("Pocket dimension world is null!");
            return;
        }

        if (pocketEntity.getOwnerId() != null && !pocketEntity.getOwnerId().equals(player.getUuid())) {
            MessageCooldownManager.sendCooldownMessage(
                    player,
                    world,
                    "pocket_dimension.message.not_owner");
            return;
        }

        PocketState stateData = PocketState.getServerState(world.getServer());
        int pocketId = pocketEntity.getPocketId();

        if (pocketId == -1) {
            Integer existingPocketId = stateData.getPlayerPocket(player.getUuid());
            if (existingPocketId != null) {
                pocketId = existingPocketId;
            } else {
                pocketId = stateData.getNextPocketId();
                stateData.setPlayerPocket(player.getUuid(), pocketId);
                RoomGenerator.generateRoom(pocketWorld, pocketId);
            }

            pocketEntity.setPocketId(pocketId);
            pocketEntity.setOwnerId(player.getUuid());
            pocketEntity.setOwnerName(player.getName().getString());
            stateData.setPocketActive(pocketId, true);
            pocketEntity.markDirty();
        }

        PocketState.ExitLocation exitLoc = new PocketState.ExitLocation(
                player.getX(), player.getY(), player.getZ(),
                player.getYaw(), player.getPitch(),
                world.getRegistryKey().getValue());

        stateData.addExitLocation(player.getUuid(), exitLoc);

        BlockPos spawnPos = RoomGenerator.getSpawnPos(pocketId);
        TeleportUtils.teleportPlayer(player, pocketWorld, spawnPos.toCenterPos(), 90, 0);
    }

    /**
     * Handles the logic for a player returning from a pocket dimension to their
     * previous location.
     * 
     * @return true if successful, false if no exit location was found.
     */
    public static boolean returnFromPocket(ServerPlayerEntity player, ServerWorld world) {
        PocketState state = PocketState.getServerState(world.getServer());
        PocketState.ExitLocation loc = state.getExitLocation(player.getUuid());

        if (loc == null) {
            return false;
        }

        RegistryKey<World> targetDim = RegistryKey.of(RegistryKeys.WORLD, loc.dimension());
        ServerWorld targetWorld = world.getServer().getWorld(targetDim);

        if (targetWorld == null) {
            return false;
        }

        TeleportUtils.teleportPlayer(player, targetWorld, new Vec3d(loc.x(), loc.y(), loc.z()),
                loc.yaw(), loc.pitch());
        state.removeExitLocation(player.getUuid());
        return true;
    }

    /**
     * Handles the logic for a player entering the rift dimension as a fallback.
     */
    public static void enterRift(ServerPlayerEntity player, ServerWorld world) {
        ServerWorld pocketWorld = world.getServer().getWorld(ModDimensions.REALM);
        if (pocketWorld == null) {
            PocketDimensionMod.LOGGER.warn("Pocket dimension world is null! Falling back to current world.");
            pocketWorld = world;
        }

        RoomGenerator.generateRift(pocketWorld);
        TeleportUtils.teleportPlayer(player, pocketWorld, PocketConfig.RIFT_SPAWN_POS_VEC, 0, 0);
    }
}
