package com.macwac.olddiscovery.item;
import com.macwac.olddiscovery.OldDiscovery;
import com.macwac.olddiscovery.item.custom.Scanner;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, OldDiscovery.MOD_ID);

    public static final RegistryObject<Item> BATTERY =
            ITEMS.register("battery", () -> new Item(new Item.Properties()
                    .group(ModItemGroup.OLD_DISCOVERY_GROUP)));

    public static final RegistryObject<Item> COPPER_INGOT =
            ITEMS.register("copper_ingot", () -> new Item(new Item.Properties()
                    .group(ModItemGroup.OLD_DISCOVERY_GROUP)));

    public static final RegistryObject<Item> LITHIUM =
            ITEMS.register("lithium", () -> new Item(new Item.Properties()
                    .group(ModItemGroup.OLD_DISCOVERY_GROUP)));

    public static final RegistryObject<Item> LITHIUM_INGOT =
            ITEMS.register("lithium_ingot", () -> new Item(new Item.Properties()
                    .group(ModItemGroup.OLD_DISCOVERY_GROUP)));

    public static final RegistryObject<Item> SCANNER =
            ITEMS.register("scanner", () -> new Scanner(new Item.Properties()
                    .group(ModItemGroup.OLD_DISCOVERY_GROUP)
                    .maxStackSize(1)
                    .maxDamage(50)));

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}

















