package org.renwixx.pocketdimension.config;

import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import com.moandjiezana.toml.Toml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Box;

public class PocketConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("Pocket Dimension");

    public static int messageCooldown = 40;
    public static boolean requireFullHealth = true;

    public static String fallbackWallBlock = "pocket_dimension:pocket_wall";
    public static String fallbackFloorBlock = "minecraft:bedrock";
    public static String fallbackAirBlock = "minecraft:air";
    public static String fallbackPortalBlock = "pocket_dimension:exit_block";
    public static String fallbackGlowstoneBlock = "minecraft:glowstone";

    // Room scaling and placement
    public static int ROOM_OFFSET_MULTIPLIER = 1000;
    public static int ROOM_OUTER_SIZE = 15;
    public static int ROOM_INNER_MIN = 5;
    public static int ROOM_INNER_MAX = 11;
    public static int ROOM_WALL_MIN = 4;
    public static int ROOM_WALL_MAX = 12;

    // Default spawn inside the room (relative to room origin)
    public static BlockPos RELATIVE_SPAWN_POS = new BlockPos(10, 5, 8);

    // Portal exit blocks inside the room (relative to room origin)
    public static int PORTAL_X = 12;
    public static int PORTAL_Y_MIN = 6;
    public static int PORTAL_Y_MAX = 8;
    public static int PORTAL_Z_MIN = 7;
    public static int PORTAL_Z_MAX = 9;

    // Glowstone position (relative to room origin)
    public static BlockPos RELATIVE_GLOWSTONE_POS = new BlockPos(8, 12, 8);

    // Rift dimensions and bounds
    public static BlockPos RIFT_MIN = new BlockPos(-1024, 0, -1024);
    public static BlockPos RIFT_MAX = new BlockPos(-1008, 15, -1008);
    public static BlockPos RIFT_SPAWN_POS_BLOCK = new BlockPos(-1016, 8, -1016);
    public static Vec3d RIFT_SPAWN_POS_VEC = new Vec3d(-1016, 8, -1016);
    public static Box RIFT_BOUNDS = new Box(RIFT_MIN.getX(), RIFT_MIN.getY(), RIFT_MIN.getZ(), RIFT_MAX.getX(),
            RIFT_MAX.getY() + 1, RIFT_MAX.getZ());

    // Collision checking
    public static int RIFT_X_THRESHOLD = -1000;

    // Blocks place flag
    public static int PLACE_FLAGS = 104;

    public static void loadConfig() {
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve("pocket_dimension.toml");
        if (!Files.exists(configFile)) {
            saveDefaultConfig(configFile);
        }
        try {
            Toml toml = new Toml().read(configFile.toFile());

            Toml room = toml.getTable("Room");
            if (room != null) {
                ROOM_OFFSET_MULTIPLIER = getInt(room, "offset_multiplier", ROOM_OFFSET_MULTIPLIER);
                ROOM_OUTER_SIZE = getInt(room, "outer_size", ROOM_OUTER_SIZE);
                ROOM_INNER_MIN = getInt(room, "inner_min", ROOM_INNER_MIN);
                ROOM_INNER_MAX = getInt(room, "inner_max", ROOM_INNER_MAX);
                ROOM_WALL_MIN = getInt(room, "wall_min", ROOM_WALL_MIN);
                ROOM_WALL_MAX = getInt(room, "wall_max", ROOM_WALL_MAX);
            }

            Toml spawn = toml.getTable("Positions.Spawn");
            if (spawn != null) {
                int x = getInt(spawn, "x", RELATIVE_SPAWN_POS.getX());
                int y = getInt(spawn, "y", RELATIVE_SPAWN_POS.getY());
                int z = getInt(spawn, "z", RELATIVE_SPAWN_POS.getZ());
                RELATIVE_SPAWN_POS = new BlockPos(x, y, z);
            }

            Toml portal = toml.getTable("Positions.Portal");
            if (portal != null) {
                PORTAL_X = getInt(portal, "x", PORTAL_X);
                PORTAL_Y_MIN = getInt(portal, "y_min", PORTAL_Y_MIN);
                PORTAL_Y_MAX = getInt(portal, "y_max", PORTAL_Y_MAX);
                PORTAL_Z_MIN = getInt(portal, "z_min", PORTAL_Z_MIN);
                PORTAL_Z_MAX = getInt(portal, "z_max", PORTAL_Z_MAX);
            }

            Toml glowstone = toml.getTable("Positions.Glowstone");
            if (glowstone != null) {
                int x = getInt(glowstone, "x", RELATIVE_GLOWSTONE_POS.getX());
                int y = getInt(glowstone, "y", RELATIVE_GLOWSTONE_POS.getY());
                int z = getInt(glowstone, "z", RELATIVE_GLOWSTONE_POS.getZ());
                RELATIVE_GLOWSTONE_POS = new BlockPos(x, y, z);
            }

            Toml rift = toml.getTable("Rift");
            if (rift != null) {
                int minX = getInt(rift, "min_x", RIFT_MIN.getX());
                int minY = getInt(rift, "min_y", RIFT_MIN.getY());
                int minZ = getInt(rift, "min_z", RIFT_MIN.getZ());
                RIFT_MIN = new BlockPos(minX, minY, minZ);

                int maxX = getInt(rift, "max_x", RIFT_MAX.getX());
                int maxY = getInt(rift, "max_y", RIFT_MAX.getY());
                int maxZ = getInt(rift, "max_z", RIFT_MAX.getZ());
                RIFT_MAX = new BlockPos(maxX, maxY, maxZ);

                int spawnX = getInt(rift, "spawn_x", RIFT_SPAWN_POS_BLOCK.getX());
                int spawnY = getInt(rift, "spawn_y", RIFT_SPAWN_POS_BLOCK.getY());
                int spawnZ = getInt(rift, "spawn_z", RIFT_SPAWN_POS_BLOCK.getZ());
                RIFT_SPAWN_POS_BLOCK = new BlockPos(spawnX, spawnY, spawnZ);
                RIFT_SPAWN_POS_VEC = new Vec3d(spawnX, spawnY, spawnZ);

                RIFT_BOUNDS = new Box(
                        minX, minY, minZ,
                        maxX, maxY + 1, maxZ);

                RIFT_X_THRESHOLD = getInt(rift, "x_threshold", RIFT_X_THRESHOLD);
            }

            Toml gameplay = toml.getTable("Gameplay");
            if (gameplay != null) {
                messageCooldown = getInt(gameplay, "message_cooldown", messageCooldown);
                requireFullHealth = gameplay.getBoolean("require_full_health", requireFullHealth);
            }

            Toml blocks = toml.getTable("Blocks");
            if (blocks != null) {
                fallbackWallBlock = blocks.getString("fallback_wall", fallbackWallBlock);
                fallbackFloorBlock = blocks.getString("fallback_floor", fallbackFloorBlock);
                fallbackAirBlock = blocks.getString("fallback_air", fallbackAirBlock);
                fallbackPortalBlock = blocks.getString("fallback_portal", fallbackPortalBlock);
                fallbackGlowstoneBlock = blocks.getString("fallback_glowstone", fallbackGlowstoneBlock);
            }

        } catch (IllegalStateException e) {
            LOGGER.error("Failed to parse config file: syntax or structure is invalid", e);
        } catch (RuntimeException e) {
            LOGGER.error("Runtime error while reading config file", e);
        }
    }

    private static int getInt(Toml toml, String key, int defaultValue) {
        Long val = toml.getLong(key);
        return val != null ? val.intValue() : defaultValue;
    }

    private static void saveDefaultConfig(Path file) {
        try (InputStream in = PocketConfig.class.getResourceAsStream("/pocket_dimension.toml")) {
            if (in != null) {
                Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
            } else {
                LOGGER.error("Could not find default config in resources at /pocket_dimension.toml");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to copy default config file", e);
        }
    }
}
