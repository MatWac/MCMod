package com.macwac.olddiscovery.item;

import com.macwac.olddiscovery.OldDiscovery;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, OldDiscovery.MOD_ID);

    public static final RegistryObject<Item> SCANNER = ITEMS.register("scanner",
            () -> new Item(new Item.Properties().group(ModItemGroup.OLD_DISCOVERY_GROUP)));

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }

}

















