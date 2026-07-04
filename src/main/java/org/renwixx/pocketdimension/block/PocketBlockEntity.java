package org.renwixx.pocketdimension.block;

import org.renwixx.pocketdimension.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;
import org.renwixx.pocketdimension.world.PocketState;
import org.renwixx.pocketdimension.manager.PocketDimensionManager;
import org.renwixx.pocketdimension.config.PocketConfig;
import org.renwixx.pocketdimension.registry.ModDimensions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.TeleportTarget;
import net.minecraft.util.math.Box;

public class PocketBlockEntity extends BlockEntity {
    private int pocketId = -1;
    private UUID ownerId = null;
    private String ownerName = null;

    public PocketBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POCKET_BLOCK_ENTITY, pos, state);
    }

    public void setPocketId(int id) {
        this.pocketId = id;
        markDirty();
    }

    public int getPocketId() {
        return this.pocketId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
        markDirty();
    }

    public UUID getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        markDirty();
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    @Override
    protected void writeData(net.minecraft.storage.WriteView view) {
        super.writeData(view);
        view.putInt("pocketId", pocketId);
        if (ownerId != null) {
            view.putString("ownerId", ownerId.toString());
        }
        if (ownerName != null) {
            view.putString("ownerName", ownerName);
        }
    }

    @Override
    protected void readData(net.minecraft.storage.ReadView view) {
        super.readData(view);
        view.getOptionalInt("pocketId").ifPresent(id -> this.pocketId = id);
        view.getOptionalString("ownerId").ifPresent(idStr -> {
            try {
                this.ownerId = UUID.fromString(idStr);
            } catch (IllegalArgumentException e) {
                this.ownerId = null;
            }
        });
        view.getOptionalString("ownerName").ifPresent(name -> this.ownerName = name);
    }

    @Override
    protected void addComponents(net.minecraft.component.ComponentMap.Builder builder) {
        super.addComponents(builder);
        net.minecraft.nbt.NbtCompound nbt = new net.minecraft.nbt.NbtCompound();
        nbt.putInt("pocketId", pocketId);
        if (ownerId != null) {
            nbt.putString("ownerId", ownerId.toString());
        }
        if (ownerName != null) {
            nbt.putString("ownerName", ownerName);
        }
        builder.add(net.minecraft.component.DataComponentTypes.CUSTOM_DATA,
                net.minecraft.component.type.NbtComponent.of(nbt));
    }

    @Override
    protected void readComponents(net.minecraft.component.ComponentsAccess components) {
        super.readComponents(components);
        net.minecraft.component.type.NbtComponent nbtComponent = components
                .get(net.minecraft.component.DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent != null) {
            net.minecraft.nbt.NbtCompound nbt = nbtComponent.copyNbt();
            nbt.getInt("pocketId").ifPresent(id -> this.pocketId = id);
            nbt.getString("ownerId").ifPresent(idStr -> {
                try {
                    this.ownerId = UUID.fromString(idStr);
                } catch (IllegalArgumentException e) {
                    this.ownerId = null;
                }
            });
            nbt.getString("ownerName").ifPresent(name -> this.ownerName = name);
        }
    }
    @Override
    public void markRemoved() {
        if (this.world != null && !this.world.isClient() && this.world instanceof ServerWorld serverWorld) {
            boolean isChunkUnloading = true;
            if (serverWorld.isChunkLoaded(this.getPos())) {
                isChunkUnloading = serverWorld.getBlockState(this.getPos()).isOf(this.getCachedState().getBlock());
            }

            if (!isChunkUnloading && this.pocketId != -1) {
                PocketState stateData = PocketState.getServerState(serverWorld.getServer());
                stateData.setPocketActive(this.pocketId, false);
                if (this.ownerId != null) {
                    stateData.removeBlockLocation(this.ownerId);
                }

                ServerWorld pocketWorld = serverWorld.getServer().getWorld(ModDimensions.REALM);
                if (pocketWorld != null) {
                    int xOffset = this.pocketId * PocketConfig.ROOM_OFFSET_MULTIPLIER;
                    Box roomBox = new Box(
                            xOffset, pocketWorld.getBottomY(), 0,
                            xOffset + PocketConfig.ROOM_OUTER_SIZE,
                            pocketWorld.getBottomY() + pocketWorld.getHeight(),
                            PocketConfig.ROOM_OUTER_SIZE);
                    for (Entity e : pocketWorld.getOtherEntities(null, roomBox)) {
                        if (e instanceof ServerPlayerEntity p) {
                            if (!PocketDimensionManager.returnFromPocket(p, pocketWorld)) {
                                PocketDimensionManager.enterRift(p, pocketWorld);
                            }
                        } else {
                            e.teleportTo(new TeleportTarget(serverWorld,
                                    this.getPos().toCenterPos(), e.getVelocity(), e.getYaw(), e.getPitch(),
                                    TeleportTarget.NO_OP));
                        }
                    }
                }
            }
        }
        super.markRemoved();
    }
}
