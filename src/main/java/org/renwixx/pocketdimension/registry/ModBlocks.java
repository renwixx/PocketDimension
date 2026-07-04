package org.renwixx.pocketdimension.registry;

import org.renwixx.pocketdimension.PocketDimensionMod;
import org.renwixx.pocketdimension.block.PocketBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModBlocks {
        public static final Block POCKET_BLOCK = registerBlock("pocket_block",
                        new PocketBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).nonOpaque()
                                        .registryKey(RegistryKey.of(RegistryKeys.BLOCK,
                                                        Identifier.of(PocketDimensionMod.MOD_ID, "pocket_block")))));

        public static final Block EXIT_BLOCK = registerBlock("exit_block",
                        new org.renwixx.pocketdimension.block.ExitBlock(AbstractBlock.Settings.create()
                                        .mapColor(MapColor.IRON_GRAY).noCollision().strength(-1.0f, 3600000.0f)
                                        .registryKey(RegistryKey.of(RegistryKeys.BLOCK,
                                                        Identifier.of(PocketDimensionMod.MOD_ID, "exit_block")))));

        public static final Block POCKET_WALL = registerBlock("pocket_wall",
                        new Block(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(-1.0f, 3600000.0f)
                                        .registryKey(RegistryKey.of(RegistryKeys.BLOCK,
                                                        Identifier.of(PocketDimensionMod.MOD_ID, "pocket_wall")))));

        private static Block registerBlock(String name, Block block) {
                return Registry.register(Registries.BLOCK, Identifier.of(PocketDimensionMod.MOD_ID, name), block);
        }

        public static void registerModBlocks() {
                PocketDimensionMod.LOGGER.info("Registering ModBlocks for " + PocketDimensionMod.MOD_ID);
        }
}
