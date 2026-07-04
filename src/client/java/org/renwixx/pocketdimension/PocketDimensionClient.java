package org.renwixx.pocketdimension;

import org.renwixx.pocketdimension.registry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import org.renwixx.pocketdimension.registry.ModBlockEntities;

public class PocketDimensionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(ModBlocks.POCKET_BLOCK, BlockRenderLayer.CUTOUT);
        
        BlockEntityRendererFactories.register(ModBlockEntities.EXIT_BLOCK_ENTITY, ctx -> (net.minecraft.client.render.block.entity.BlockEntityRenderer) new EndPortalBlockEntityRenderer());
    }
}
