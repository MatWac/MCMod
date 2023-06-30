package com.macwac.olddiscovery.item.custom;

import com.macwac.olddiscovery.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Scanner extends Item {
    public Scanner(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerEntity, Hand hand) {
        if (!world.isRemote) {
            // Vérifie la durabilité restante avant de scanner
            ItemStack stack = playerEntity.getHeldItem(hand);

            if (stack.getDamage() >= stack.getMaxDamage()) {
                // Scanner détruit, ne peut pas être utilisé
                return new ActionResult<>(ActionResultType.FAIL, stack);
            }

            // Charge le chunk du joueur
            if (world.getChunkProvider().isChunkLoaded(new ChunkPos(playerEntity.getPosition()))) {
                // Détection des blocs spécifiques dans le chunk du joueur
                scanChunk(playerEntity, stack);
            }
        }
        return super.onItemRightClick(world, playerEntity, hand);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        int durability = stack.getMaxDamage() - stack.getDamage();

        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent("tooltip.olddiscovery.scanner"));
            tooltip.add(new TranslationTextComponent("Durability : " + durability + " / " + stack.getMaxDamage()));
        } else {
            tooltip.add(new TranslationTextComponent("tooltip.olddiscovery.scanner_shift"));
        }
        super.addInformation(stack, world, tooltip, flagIn);
    }

    public static void scanChunk(PlayerEntity playerEntity, ItemStack scannerStack) {
        World world = playerEntity.getEntityWorld();
        BlockPos playerPos = playerEntity.getPosition();

        int startX = MathHelper.floor(playerPos.getX()) - 8;
        int startZ = MathHelper.floor(playerPos.getZ()) - 8;
        int startY = MathHelper.floor(playerPos.getY()) - 16;
        int endY = MathHelper.floor(playerPos.getY()) + 16;

        ITextComponent component = new TranslationTextComponent("information.olddiscovery.ruins");

        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                for (int y = startY; y < endY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();

                    // Vérifie si le bloc est celui que tu recherches
                    if (isDesiredBlock(block)) {
                        // Le scanner a détecté le bloc recherché !
                        playerEntity.sendMessage(component, playerEntity.getUniqueID());
                    }
                }
            }
        }
        // Réduit la durabilité du scanner
        scannerStack.attemptDamageItem(1, world.getRandom(), null);
    }

    private static boolean isDesiredBlock(Block block) {
        // Ajoute ici les conditions pour les blocs que tu souhaites détecter
        return block == Blocks.MOSSY_COBBLESTONE;
    }
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
