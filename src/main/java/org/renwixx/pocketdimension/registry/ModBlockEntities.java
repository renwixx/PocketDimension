package org.renwixx.pocketdimension.registry;

import org.renwixx.pocketdimension.PocketDimensionMod;
import org.renwixx.pocketdimension.block.PocketBlockEntity;
import org.renwixx.pocketdimension.block.ExitBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<PocketBlockEntity> POCKET_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(PocketDimensionMod.MOD_ID, "pocket_block_entity"),
            FabricBlockEntityTypeBuilder.create(PocketBlockEntity::new, ModBlocks.POCKET_BLOCK).build()
    );

    public static final BlockEntityType<ExitBlockEntity> EXIT_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(PocketDimensionMod.MOD_ID, "exit_block_entity"),
            FabricBlockEntityTypeBuilder.create(ExitBlockEntity::new, ModBlocks.EXIT_BLOCK).build()
    );

    public static void registerModBlockEntities() {
        PocketDimensionMod.LOGGER.info("Registering Mod Block Entities for " + PocketDimensionMod.MOD_ID);
    }
}
