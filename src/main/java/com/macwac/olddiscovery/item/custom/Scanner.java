package com.macwac.olddiscovery.item.custom;

import com.macwac.olddiscovery.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class Scanner extends Item {
    public Scanner(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {

        World world = context.getWorld();
        PlayerEntity player = Objects.requireNonNull(context.getPlayer());
        ItemStack stack = context.getItem();

        if (!world.isRemote) {
            BlockPos pos = context.getPos();

            // Charge le chunk du joueur
            if (world.getChunkProvider().isChunkLoaded(new ChunkPos(pos))) {
                // Détection des ruines dans le chunk du joueur
                scanChunk(pos.getX() >> 4, pos.getZ() >> 4, player);
            }

            // Réduit la durabilité de l'item
            stack.damageItem(1, player, playerEntity -> playerEntity.sendBreakAnimation(context.getHand()));
        }

        return super.onItemUse(context);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent("tooltip.olddiscovery.scanner"));
        } else {
            tooltip.add(new TranslationTextComponent("tooltip.olddiscovery.scanner_shift"));
        }

        super.addInformation(stack, world, tooltip, flagIn);
    }

    public static void scanChunk(int chunkX, int chunkZ, PlayerEntity playerEntity) {
        // Obtient les coordonnées du coin supérieur gauche du chunk
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        World world = playerEntity.getEntityWorld();
        ITextComponent component = new TranslationTextComponent("information.olddiscovery.ruins");

        // Parcourt chaque bloc dans le chunk
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                for (int y = 0; y < world.getHeight(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();

                    // Vérifie si le bloc est de la Cobblestone moussue
                    if (block == Blocks.MOSSY_COBBLESTONE) {
                        // Le scanner a détecté un bloc de Cobblestone moussue !
                        playerEntity.sendMessage(component, playerEntity.getUniqueID());
                        return;
                    }
                }
            }
        }
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        // Vérifie si le matériau de réparation est une pile
        return repair.isItemEqual(new ItemStack(ModItems.BATTERY.get(), 3));
    }
}
