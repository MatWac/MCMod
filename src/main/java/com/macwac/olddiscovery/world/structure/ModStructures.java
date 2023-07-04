package com.macwac.olddiscovery.world.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.macwac.olddiscovery.OldDiscovery;
import com.macwac.olddiscovery.world.structure.structures.OasisStructure;
import com.macwac.olddiscovery.world.structure.structures.TowerRuinsStructure;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ModStructures {
    // Enregistrement différé des structures personnalisées
    public static final DeferredRegister<Structure<?>> STRUCTURES =
            DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, OldDiscovery.MOD_ID);

    // Enregistrement différé de la structure Oasis
    public static final RegistryObject<Structure<NoFeatureConfig>> OASIS =
            STRUCTURES.register("oasis", OasisStructure::new);

    // Enregistrement différé de la structure TowerRuins
    public static final RegistryObject<Structure<NoFeatureConfig>> TOWER_RUINS =
            STRUCTURES.register("tower_ruins", TowerRuinsStructure::new);

    // Configuration des structures
    public static void setupStructures() {
        // Configuration de la séparation et du placement de la structure Oasis
        setupMapSpacingAndLand(OASIS.get(),
                new StructureSeparationSettings(
                        10, // Distance moyenne en chunks entre les tentatives de spawn
                        5,  // Distance minimale en chunks entre les tentatives de spawn
                        1234567890), // Graine unique pour la structure
                true); // Modification du terrain environnant pour correspondre à la base de la structure

        // Configuration de la séparation et du placement de la structure TowerRuins
        setupMapSpacingAndLand(TOWER_RUINS.get(),
                new StructureSeparationSettings(
                        15, // Distance moyenne en chunks entre les tentatives de spawn
                        8,  // Distance minimale en chunks entre les tentatives de spawn
                        987654321), // Graine unique pour la structure
                true); // Modification du terrain environnant pour correspondre à la base de la structure
    }

    /**
     * Ajoute la structure fournie au registre et configure les paramètres de séparation.
     * La rareté de la structure est déterminée par les valeurs passées dans structureSeparationSettings.
     * Cette méthode est appelée par la méthode setupStructures ci-dessus.
     **/
    public static <F extends Structure<?>> void setupMapSpacingAndLand(F structure, StructureSeparationSettings structureSeparationSettings,
                                                                       boolean transformSurroundingLand) {
        // Ajout de la structure au registre
        Structure.NAME_STRUCTURE_BIMAP.put(structure.getRegistryName().toString(), structure);

        // Modification du terrain environnant si transformSurroundingLand est true
        if (transformSurroundingLand) {
            Structure.field_236384_t_ = ImmutableList.<Structure<?>>builder()
                    .addAll(Structure.field_236384_t_)
                    .add(structure)
                    .build();
        }

        // Ajout des paramètres de séparation au registre des structures par défaut
        DimensionStructuresSettings.field_236191_b_ =
                ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
                        .putAll(DimensionStructuresSettings.field_236191_b_)
                        .put(structure, structureSeparationSettings)
                        .build();

        // Ajout des paramètres de séparation au registre des structures pour chaque générateur de bruit
        WorldGenRegistries.NOISE_SETTINGS.getEntries().forEach(settings -> {
            Map<Structure<?>, StructureSeparationSettings> structureMap =
                    settings.getValue().getStructures().func_236195_a_();
            if (structureMap instanceof ImmutableMap) {
                // Crée une copie mutable du registre des structures si nécessaire
                Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(structureMap);
                tempMap.put(structure, structureSeparationSettings);
                settings.getValue().getStructures().func_236195_a_();

            } else {
                structureMap.put(structure, structureSeparationSettings);
            }
        });
    }

    // Méthode d'enregistrement des structures sur le bus d'événements
    public static void register(IEventBus eventBus) {
        STRUCTURES.register(eventBus);
    }
}
