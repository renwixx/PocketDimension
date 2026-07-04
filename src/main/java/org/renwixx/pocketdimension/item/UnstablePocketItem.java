package org.renwixx.pocketdimension.item;

import org.renwixx.pocketdimension.manager.PocketDimensionManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class UnstablePocketItem extends Item {
    public UnstablePocketItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.SUCCESS;
        }

        ServerWorld serverWorld = (ServerWorld) world;

        if (!PocketDimensionManager.returnFromPocket(serverPlayer, serverWorld)) {
            player.sendMessage(net.minecraft.text.Text.translatable("pocket_dimension.message.no_room_available"),
                    true);
            PocketDimensionManager.enterRift(serverPlayer, serverWorld);
        }

        if (!player.isCreative()) {
            player.getStackInHand(hand).decrement(1);
        }

        return ActionResult.SUCCESS;
    }
}
