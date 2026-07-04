package org.renwixx.pocketdimension.registry;

import org.renwixx.pocketdimension.PocketDimensionMod;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModDimensions {
    public static final RegistryKey<World> REALM = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(PocketDimensionMod.MOD_ID, "realm"));

    public static void register() {
        PocketDimensionMod.LOGGER.info("Registering dimensions for " + PocketDimensionMod.MOD_ID);
    }
}
