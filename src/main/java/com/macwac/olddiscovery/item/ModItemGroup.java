package com.macwac.olddiscovery.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroup {

    public static final ItemGroup OLD_DISCOVERY_GROUP = new ItemGroup("oldDiscoveryTab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.SCANNER.get());
        }
    };
}
