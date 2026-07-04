package org.renwixx.pocketdimension.block;

import com.mojang.serialization.MapCodec;
import org.renwixx.pocketdimension.manager.PocketDimensionManager;
import org.renwixx.pocketdimension.util.MessageCooldownManager;
import org.renwixx.pocketdimension.config.PocketConfig;
import org.renwixx.pocketdimension.world.PocketState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class ExitBlock extends Block implements BlockEntityProvider {
    public static final MapCodec<ExitBlock> CODEC = createCodec(ExitBlock::new);

    @Override
    public MapCodec<ExitBlock> getCodec() {
        return CODEC;
    }

    public ExitBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ExitBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity,
            EntityCollisionHandler handler, boolean isInside) {
        handleCollision(world, pos, entity);
    }

    public static void handleCollision(World world, BlockPos pos, Entity entity) {
        if (world.isClient() || !(entity instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        ServerWorld serverWorld = (ServerWorld) world;

        if (pos.getX() < PocketConfig.RIFT_X_THRESHOLD) {
            handleRiftExit(serverPlayer, serverWorld);
        } else {
            handleRoomExit(serverPlayer, serverWorld, pos);
        }
    }

    private static void handleRiftExit(ServerPlayerEntity player, ServerWorld world) {
        if (!player.getInventory().isEmpty()) {
            MessageCooldownManager.sendCooldownMessage(player, world,
                    "pocket_dimension.message.rift.require_clear_inventory");
            return;
        }

        if (PocketDimensionManager.returnFromPocket(player, world)) {
            return;
        }

        TeleportTarget target = player.getRespawnTarget(false, TeleportTarget.NO_OP);
        if (target != null) {
            player.teleportTo(target);
        } else {
            PocketDimensionManager.enterRift(player, world);
        }
    }

    private static void handleRoomExit(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        int pocketId = Math.floorDiv(pos.getX(), PocketConfig.ROOM_OFFSET_MULTIPLIER);
        PocketState stateData = PocketState.getServerState(world.getServer());

        if (stateData.isPocketActive(pocketId)) {
            if (!PocketDimensionManager.returnFromPocket(player, world)) {
                MessageCooldownManager.sendCooldownMessage(player, world, "pocket_dimension.message.exit_not_found");
                PocketDimensionManager.enterRift(player, world);
            }
        } else {
            MessageCooldownManager.sendCooldownMessage(player, world, "pocket_dimension.message.no_room_available");
            PocketDimensionManager.enterRift(player, world);
        }
    }
}
