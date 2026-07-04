package org.renwixx.pocketdimension.block;

import org.renwixx.pocketdimension.registry.ModDimensions;
import org.renwixx.pocketdimension.world.PocketState;
import org.renwixx.pocketdimension.manager.PocketDimensionManager;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.Direction;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.GlobalPos;
import org.jetbrains.annotations.Nullable;
import org.renwixx.pocketdimension.config.PocketConfig;
import org.renwixx.pocketdimension.util.MessageCooldownManager;
import org.renwixx.pocketdimension.registry.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class PocketBlock extends BlockWithEntity {
    public static final MapCodec<PocketBlock> CODEC = createCodec(PocketBlock::new);
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    @Override
    public MapCodec<PocketBlock> getCodec() {
        return CODEC;
    }

    public PocketBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PocketBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
            BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack itemStack) {
        if (!(world instanceof ServerWorld serverWorld)) {
            super.onPlaced(world, pos, state, placer, itemStack);
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof PocketBlockEntity pocketEntity)) {
            super.onPlaced(world, pos, state, placer, itemStack);
            return;
        }

        int pocketId = pocketEntity.getPocketId();
        PocketState stateData = PocketState.getServerState(serverWorld.getServer());
        boolean shouldDrop = false;

        if (pocketId != -1) {
            if (stateData.isPocketActive(pocketId)) {
                shouldDrop = true;
            } else {
                stateData.setPocketActive(pocketId, true);
            }
        } else if (placer instanceof PlayerEntity player) {
            Integer existingPocketId = stateData.getPlayerPocket(player.getUuid());
            if (existingPocketId != null && stateData.isPocketActive(existingPocketId)) {
                shouldDrop = true;
            } else {
                pocketEntity.setOwnerId(player.getUuid());
                pocketEntity.setOwnerName(player.getName().getString());
            }
        }

        if (shouldDrop) {
            if (placer instanceof PlayerEntity player) {
                player.sendMessage(Text.translatable("pocket_dimension.actionbar.pocket_already_placed"), true);
            }
            ItemScatterer.spawn(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    itemStack.copy());
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            return;
        }

        if (pocketEntity.getOwnerId() != null) {
            stateData.addBlockLocation(pocketEntity.getOwnerId(),
                    GlobalPos.create(world.getRegistryKey(), pos));
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof PocketBlockEntity pocketEntity)) {
            return ActionResult.SUCCESS;
        }

        if (world.getRegistryKey() == ModDimensions.REALM) {
            ServerWorld pocketWorld = (ServerWorld) world;
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            ItemScatterer.spawn(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    new ItemStack(ModItems.UNSTABLE_POCKET));

            PocketDimensionManager.enterRift(serverPlayer, pocketWorld);

            return ActionResult.SUCCESS;
        }

        if (PocketConfig.requireFullHealth
                && player.getHealth() < player.getMaxHealth()) {
            MessageCooldownManager.sendCooldownMessage(serverPlayer, (ServerWorld) world,
                    "pocket_dimension.message.require_full_health");
            return ActionResult.FAIL;
        }

        ServerWorld serverWorld = (ServerWorld) world;
        PocketDimensionManager.enterPocket(serverPlayer, serverWorld, pocketEntity);

        return ActionResult.SUCCESS;
    }
}
