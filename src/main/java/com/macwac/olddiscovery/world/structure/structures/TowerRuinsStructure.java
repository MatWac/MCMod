package com.macwac.olddiscovery.world.structure.structures;

import com.macwac.olddiscovery.OldDiscovery;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class TowerRuinsStructure extends Structure<NoFeatureConfig>
{
    public TowerRuinsStructure() {
        super(NoFeatureConfig.CODEC);
    }

    @Override
    public GenerationStage.Decoration getDecorationStage() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    // Vérifie si la structure de ruines de tour peut être générée dans un chunk spécifique
    @Override
    protected boolean func_230363_a_(
            ChunkGenerator chunkGenerator,
            BiomeProvider biomeSource,
            long seed,
            SharedSeedRandom chunkRandom,
            int chunkX,
            int chunkZ,
            Biome biome,
            ChunkPos chunkPos,
            NoFeatureConfig featureConfig)
    {
        // Obtient la position centrale du chunk
        BlockPos centerOfChunk = new BlockPos(
                (chunkX << 4) + 7,
                0,
                (chunkZ << 4) + 7);

        // Obtient la hauteur du terrain à la position centrale du chunk
        int landHeight = chunkGenerator.getHeight(
                centerOfChunk.getX(),
                centerOfChunk.getZ(),
                Heightmap.Type.WORLD_SURFACE_WG);

        // Obtient le bloc le plus haut à la position centrale du chunk
        IBlockReader columnOfBlocks = chunkGenerator.func_230348_a_(
                centerOfChunk.getX(),
                centerOfChunk.getZ());

        BlockState topBlock = columnOfBlocks.getBlockState(
                centerOfChunk.up(landHeight));

        // Vérifie si le bloc le plus haut est recouvert de fluide
        return topBlock.getFluidState().isEmpty();
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return TowerRuinsStructure.Start::new;
    }

    // Classe interne représentant le point de départ de la génération de la structure
    public static class Start extends StructureStart<NoFeatureConfig> {
        public Start(
                Structure<NoFeatureConfig> structureIn,
                int chunkX,
                int chunkZ,
                MutableBoundingBox mutableBoundingBox,
                int referenceIn,
                long seedIn) {

            super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
        }

        @Override
        public void func_230364_a_(
                DynamicRegistries dynamicRegistryManager,
                ChunkGenerator chunkGenerator,
                TemplateManager templateManagerIn,
                int chunkX,
                int chunkZ,
                Biome biomeIn,
                NoFeatureConfig config)
        {
            // Convertit les coordonnées du chunk en coordonnées réelles utilisables
            int x = (chunkX << 4) + 7;
            int z = (chunkZ << 4) + 7;
            BlockPos blockpos = new BlockPos(x, 0, z);

            // Ajoute les pièces de la structure en utilisant JigsawManager
            JigsawManager.func_242837_a(dynamicRegistryManager,
                    new VillageConfig(() -> dynamicRegistryManager.getRegistry(Registry.JIGSAW_POOL_KEY)
                            .getOrDefault(new ResourceLocation(OldDiscovery.MOD_ID, "tower_ruins/start_pool")),
                            10), AbstractVillagePiece::new, chunkGenerator, templateManagerIn,
                    blockpos, this.components, this.rand, false, true);

            // Ajuste la position des pièces pour les placer correctement dans le monde
            this.components.forEach(piece -> piece.offset(0, 1, 0));
            this.components.forEach(piece -> piece.getBoundingBox().minY -= 1);

            // Recalcule la taille de la structure
            this.recalculateStructureSize();

            // Journalise les coordonnées de la structure générée
            LogManager.getLogger().log(Level.DEBUG, "Tower ruins at " +
                    this.components.get(0).getBoundingBox().minX + " " +
                    this.components.get(0).getBoundingBox().minY + " " +
                    this.components.get(0).getBoundingBox().minZ);
        }
    }
}
