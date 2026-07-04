package org.renwixx.pocketdimension.util;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import java.util.UUID;
import org.renwixx.pocketdimension.config.PocketConfig;

public class MessageCooldownManager {
    private static final Object2LongMap<UUID> MESSAGE_COOLDOWNS = new Object2LongOpenHashMap<>();

    static {
        MESSAGE_COOLDOWNS.defaultReturnValue(-1000L);
    }

    public static void removeCooldown(UUID uuid) {
        MESSAGE_COOLDOWNS.removeLong(uuid);
    }

    public static void sendCooldownMessage(ServerPlayerEntity player, ServerWorld world, String key) {
        long time = world.getTime();
        if (time - MESSAGE_COOLDOWNS.getLong(player.getUuid()) > PocketConfig.messageCooldown) {
            MESSAGE_COOLDOWNS.put(player.getUuid(), time);
            player.sendMessage(Text.translatable(key), true);
        }
    }
}
