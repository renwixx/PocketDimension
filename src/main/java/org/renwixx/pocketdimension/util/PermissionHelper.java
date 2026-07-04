package org.renwixx.pocketdimension.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.lang.reflect.Method;

public class PermissionHelper {

    private static Boolean isLuckPermsLoaded = null;
    private static boolean reflectionInitialized = false;
    private static boolean reflectionFailed = false;

    private static Method luckPermsProviderGet;
    private static Method luckPermsGetUserManager;
    private static Method userManagerGetUser;
    private static Method userGetCachedData;
    private static Method cachedDataManagerGetPermissionData;
    private static Method cachedPermissionDataCheckPermission;
    private static Method tristateAsBoolean;

    private static void initReflection() {
        if (reflectionInitialized) return;
        reflectionInitialized = true;
        try {
            Class<?> providerClass = Class.forName("net.luckperms.api.LuckPermsProvider");
            luckPermsProviderGet = providerClass.getMethod("get");
            Class<?> luckPermsClass = Class.forName("net.luckperms.api.LuckPerms");
            luckPermsGetUserManager = luckPermsClass.getMethod("getUserManager");
            Class<?> userManagerClass = Class.forName("net.luckperms.api.model.user.UserManager");
            userManagerGetUser = userManagerClass.getMethod("getUser", java.util.UUID.class);
            Class<?> userClass = Class.forName("net.luckperms.api.model.user.User");
            userGetCachedData = userClass.getMethod("getCachedData");
            Class<?> cachedDataClass = Class.forName("net.luckperms.api.cacheddata.CachedDataManager");
            cachedDataManagerGetPermissionData = cachedDataClass.getMethod("getPermissionData");
            Class<?> permissionDataClass = Class.forName("net.luckperms.api.cacheddata.CachedPermissionData");
            cachedPermissionDataCheckPermission = permissionDataClass.getMethod("checkPermission", String.class);
            Class<?> tristateClass = Class.forName("net.luckperms.api.util.Tristate");
            tristateAsBoolean = tristateClass.getMethod("asBoolean");
        } catch (Exception e) {
            reflectionFailed = true;
        }
    }

    public static boolean checkLuckPerms(ServerCommandSource source, String permission) {
        if (isLuckPermsLoaded == null) {
            isLuckPermsLoaded = FabricLoader.getInstance().isModLoaded("luckperms");
        }

        if (!isLuckPermsLoaded) {
            return false;
        }

        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            return false;
        }

        initReflection();
        if (reflectionFailed) {
            return false;
        }

        try {
            Object luckPerms = luckPermsProviderGet.invoke(null);
            Object userManager = luckPermsGetUserManager.invoke(luckPerms);
            Object user = userManagerGetUser.invoke(userManager, player.getUuid());

            if (user != null) {
                Object cachedData = userGetCachedData.invoke(user);
                Object permissionData = cachedDataManagerGetPermissionData.invoke(cachedData);
                Object tristate = cachedPermissionDataCheckPermission.invoke(permissionData, permission);
                return (Boolean) tristateAsBoolean.invoke(tristate);
            }
        } catch (Exception e) {
            // Ignore reflection exceptions, fallback to false
        }

        return false;
    }

    public static boolean hasPermission(ServerCommandSource source, String permission, int fallbackLevel) {
        if (isLuckPermsLoaded == null) {
            isLuckPermsLoaded = FabricLoader.getInstance().isModLoaded("luckperms");
        }
        
        if (isLuckPermsLoaded && source.getPlayer() != null) {
            if (checkLuckPerms(source, permission)) {
                return true;
            }
        }
        
        // Fallback: Check if the player is an operator (or singleplayer cheats)
        if (fallbackLevel >= 4) return net.minecraft.server.command.CommandManager.OWNERS_CHECK.allows(source.getPermissions());
        if (fallbackLevel == 3) return net.minecraft.server.command.CommandManager.ADMINS_CHECK.allows(source.getPermissions());
        if (fallbackLevel == 2) return net.minecraft.server.command.CommandManager.MODERATORS_CHECK.allows(source.getPermissions());
        return true;
    }
}
