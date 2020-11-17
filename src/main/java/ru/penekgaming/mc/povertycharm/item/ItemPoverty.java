package ru.penekgaming.mc.povertycharm.item;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.penekgaming.mc.povertycharm.block.PovertyBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@SuppressWarnings("NullableProblems")
public class ItemPoverty extends ItemBlock {
    protected final PovertyBlock block;

    public ItemPoverty(PovertyBlock block) {
        this(block, Objects.requireNonNull(block.getRegistryName()));
    }

    public ItemPoverty(PovertyBlock block, @Nonnull ResourceLocation resourceLocation) {
        super(block);
        this.block = block;
        setRegistryName(resourceLocation);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        PovertyBlock povertyBlock = block instanceof PovertyBlock ? (PovertyBlock) block : null;

        if (!block.isReplaceable(worldIn, pos)
                || (povertyBlock != null && povertyBlock.isReplaceable(worldIn, pos, this.block))) {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && mayPlace(worldIn, pos, facing, player)) {
            int i = this.getMetadata(itemstack.getMetadata());
            IBlockState placementState = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);

            if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, placementState)) {
                placementState = worldIn.getBlockState(pos);
                SoundType soundtype = placementState.getBlock().getSoundType(placementState, worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        IBlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();

        if (block == Blocks.SNOW_LAYER && block.isReplaceable(worldIn, pos)) {
            side = EnumFacing.UP;
        } else if (!block.isReplaceable(worldIn, pos) || block instanceof PovertyBlock && ((PovertyBlock) block).isReplaceable(worldIn, pos, this.block)) {
            pos = pos.offset(side);
        }

        return mayPlace(worldIn, pos, side, player);
    }

    private boolean mayPlace(World world, BlockPos pos, EnumFacing sidePlacedOn, @Nullable Entity placer) {
        IBlockState state = world.getBlockState(pos);

        if (!((placer instanceof EntityPlayer)
                || !ForgeEventFactory.onBlockPlace(placer, new BlockSnapshot(world, pos, this.block.getDefaultState()), sidePlacedOn).isCanceled()))
            return false;

        AxisAlignedBB collisionBB = block.isNoPlaceCollision() ? null : this.block.getDefaultState().getCollisionBoundingBox(world, pos);

        if (collisionBB != null && !world.checkNoEntityCollision(collisionBB.offset(pos)))
            return false;

        if (state.getMaterial() == Material.CIRCUITS && this.block == Blocks.ANVIL)
            return true;

        return state.getBlock().isReplaceable(world, pos) && this.block.canPlaceBlockOnSide(world, pos, sidePlacedOn)
                || (state.getBlock() instanceof PovertyBlock && ((PovertyBlock) state.getBlock()).isReplaceable(world, pos, this.block));
    }
}
