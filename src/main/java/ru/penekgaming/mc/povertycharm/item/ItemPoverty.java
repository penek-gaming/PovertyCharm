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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.penekgaming.mc.povertycharm.block.BlockHandholds;
import ru.penekgaming.mc.povertycharm.block.PovertyBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@SuppressWarnings("NullableProblems")
public class ItemPoverty extends ItemBlock {
    public ItemPoverty(PovertyBlock block) {
        this(block, Objects.requireNonNull(block.getRegistryName()));
    }

    public ItemPoverty(PovertyBlock block, @Nonnull ResourceLocation resourceLocation) {
        super(block);
        setRegistryName(resourceLocation);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();

        if (!block.isReplaceable(worldIn, pos) || block instanceof BlockHandholds && state.getValue(BlockHandholds.TURN)) {
            pos = pos.offset(facing);
        }

        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && mayPlace(worldIn, this.block, pos, facing, player)) {
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
        } else if (!block.isReplaceable(worldIn, pos) || block instanceof BlockHandholds && state.getValue(BlockHandholds.TURN)) {
            pos = pos.offset(side);
        }

        return mayPlace(worldIn, this.block, pos, side, player);
    }

    private static boolean mayPlace(World world, Block blockIn, BlockPos pos, EnumFacing sidePlacedOn, @Nullable Entity placer) {
        IBlockState state = world.getBlockState(pos);

        if (!((placer instanceof EntityPlayer)
                || !ForgeEventFactory.onBlockPlace(placer, new BlockSnapshot(world, pos, blockIn.getDefaultState()), sidePlacedOn).isCanceled()))
            return false;

        if (state.getMaterial() == Material.CIRCUITS && blockIn == Blocks.ANVIL)
            return true;

        return state.getBlock().isReplaceable(world, pos) && blockIn.canPlaceBlockOnSide(world, pos, sidePlacedOn)
                || state.getBlock() instanceof BlockHandholds && !state.getValue(BlockHandholds.TURN);
    }
}
