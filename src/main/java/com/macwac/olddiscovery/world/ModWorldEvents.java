package com.macwac.olddiscovery.world;

import com.macwac.olddiscovery.OldDiscovery;
import com.macwac.olddiscovery.world.gen.ModOreGeneration;
import com.macwac.olddiscovery.world.gen.ModStructureGeneration;
import com.mojang.serialization.Codec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import com.macwac.olddiscovery.world.structure.ModStructures;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = OldDiscovery.MOD_ID)
public class ModWorldEvents {

    /**
     * Événement de chargement du biome.
     * Génère les structures et les minerais spécifiques au biome.
     */
    @SubscribeEvent
    public static void biomeLoadingEvent(final BiomeLoadingEvent event) {
        ModStructureGeneration.generateStructures(event);
        ModOreGeneration.generateOres(event);
    }

    /**
     * Événement d'ajout de l'espacement dimensionnel.
     * Ajoute les structures aux dimensions lors du chargement du monde.
     */
    @SubscribeEvent
    public static void addDimensionalSpacing(final WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) event.getWorld();

            try {
                // Vérifie si le ChunkGenerator utilise Terraforged
                Method GET_CODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "func_230347_a_");
                ResourceLocation cgRL = Registry.CHUNK_GENERATOR_CODEC.getKey((Codec<? extends ChunkGenerator>) GET_CODEC_METHOD.invoke(serverWorld.getChunkProvider().generator));

                if (cgRL != null && cgRL.getNamespace().equals("terraforged")) {
                    return;
                }
            } catch (Exception e) {
                LogManager.getLogger().error("Was unable to check if " + serverWorld.getDimensionKey().getLocation()
                        + " is using Terraforged's ChunkGenerator.");
            }

            // Empêche la génération de notre structure dans le monde plat de base de Vanilla
            if (serverWorld.getChunkProvider().generator instanceof FlatChunkGenerator && serverWorld.getDimensionKey().equals(World.OVERWORLD)) {
                return;
            }

            // Ajoute notre structure à la carte des structures de la dimension
            Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(serverWorld.getChunkProvider().generator.func_235957_b_().func_236195_a_());
            tempMap.putIfAbsent(ModStructures.OASIS.get(), DimensionStructuresSettings.field_236191_b_.get(ModStructures.OASIS.get()));
            tempMap.putIfAbsent(ModStructures.TOWER_RUINS.get(), DimensionStructuresSettings.field_236191_b_.get(ModStructures.TOWER_RUINS.get()));

            serverWorld.getChunkProvider().generator.func_235957_b_().field_236193_d_ = tempMap;
        }
    }
}
