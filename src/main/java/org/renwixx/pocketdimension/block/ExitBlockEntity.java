package org.renwixx.pocketdimension.block;

import org.renwixx.pocketdimension.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ExitBlockEntity extends EndPortalBlockEntity {
    public ExitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXIT_BLOCK_ENTITY, pos, state);
    }

    @Override
    public boolean shouldDrawSide(Direction direction) {
        return true;
    }
}
