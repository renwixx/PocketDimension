package org.renwixx.pocketdimension;

import org.renwixx.pocketdimension.command.ModCommands;
import org.renwixx.pocketdimension.config.PocketConfig;
import org.renwixx.pocketdimension.registry.ModBlockEntities;
import org.renwixx.pocketdimension.registry.ModBlocks;
import org.renwixx.pocketdimension.registry.ModDimensions;
import org.renwixx.pocketdimension.registry.ModItems;
import org.renwixx.pocketdimension.util.MessageCooldownManager;
import org.renwixx.pocketdimension.world.PocketState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PocketDimensionMod implements ModInitializer {
	public static final String MOD_ID = "pocket_dimension";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Pocket Dimension Mod!");
		
		PocketConfig.loadConfig();
		
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (newPlayer.getEntityWorld() instanceof ServerWorld sw && sw.getServer() != null) {
				PocketState.getServerState(sw.getServer()).removeExitLocation(newPlayer.getUuid());
				MessageCooldownManager.removeCooldown(newPlayer.getUuid());
			}
		});

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
			if (origin.getRegistryKey() == ModDimensions.REALM && origin.getServer() != null) {
				PocketState.getServerState(origin.getServer()).removeExitLocation(player.getUuid());
				MessageCooldownManager.removeCooldown(player.getUuid());
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			MessageCooldownManager.removeCooldown(handler.getPlayer().getUuid());
		});

		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlockEntities();
		ModItems.registerModItems();
		ModDimensions.register();
		ModCommands.register();
	}
}
