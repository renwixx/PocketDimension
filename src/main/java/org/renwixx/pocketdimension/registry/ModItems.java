package org.renwixx.pocketdimension.registry;

import org.renwixx.pocketdimension.PocketDimensionMod;
import org.renwixx.pocketdimension.item.PocketItem;
import org.renwixx.pocketdimension.item.UnstablePocketItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item POCKET = registerItem("pocket",
            new PocketItem(ModBlocks.POCKET_BLOCK,
                    new Item.Settings().maxCount(1).useItemPrefixedTranslationKey().registryKey(key("pocket"))));
    public static final Item UNSTABLE_POCKET = registerItem("unstable_pocket",
            new UnstablePocketItem(new Item.Settings().maxCount(1).registryKey(key("unstable_pocket"))));

    private static RegistryKey<Item> key(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(PocketDimensionMod.MOD_ID, name));
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(PocketDimensionMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        PocketDimensionMod.LOGGER.info("Registering Mod Items for " + PocketDimensionMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(POCKET);
            entries.add(UNSTABLE_POCKET);
        });
    }
}
