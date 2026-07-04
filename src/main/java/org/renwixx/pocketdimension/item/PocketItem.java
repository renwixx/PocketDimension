package org.renwixx.pocketdimension.item;

import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PocketItem extends BlockItem {
    public PocketItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context,
            TooltipDisplayComponent displayComponent,
            Consumer<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, tooltip, type);

        tooltip.accept(Text.translatable("item.pocket_dimension.pocket.tooltip.desc").formatted(Formatting.WHITE));
        tooltip.accept(Text.translatable("item.pocket_dimension.pocket.tooltip.req").formatted(Formatting.BLUE));

        var component = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (component == null) {
            return;
        }

        var nbt = component.copyNbt();
        if (nbt.contains("ownerName")) {
            String ownerName = nbt.getString("ownerName").orElse("");
            tooltip.accept(Text.translatable("item.pocket_dimension.pocket.tooltip.owner").append(Text.literal(" " + ownerName))
                    .formatted(Formatting.GOLD));
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy().formatted(Formatting.LIGHT_PURPLE);
    }
}
