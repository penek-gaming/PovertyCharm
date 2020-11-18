package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import ru.penekgaming.mc.povertycharm.item.ItemPoverty;

import java.util.HashMap;
import java.util.Optional;

public class BlockTileBroken extends BlockConcreteBroken {
    public static final PropertyEnum<BlockTile.Variants> VARIANT = PropertyEnum.create("variant", BlockTile.Variants.class);
    //public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public BlockTileBroken() {
        super("tile_broken", Material.CLAY, false);

        setDefaultState(getBlockState().getBaseState()
                .withProperty(VARIANT, BlockTile.Variants.DEFAULT)
                .withProperty(FACING, EnumFacing.NORTH)
        );
        item = new ItemPoverty(this);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        HashMap<EnumFacing, IBlockState> neighborBlocks = getFacingBlocks(worldIn, pos);

        EnumFacing facing = state.getValue(FACING).getOpposite();
        BlockTile.Variants variant = BlockTile.Variants.DEFAULT;
        IBlockState facingState = neighborBlocks.get(facing);

        if (facingState != null && (facingState.getBlock() instanceof BlockTile))
            variant = neighborBlocks.get(facing).getValue(VARIANT);
        else {
            Optional<EnumFacing> optFacing
                    = neighborBlocks.keySet().stream()
                    .filter(f -> neighborBlocks.get(f).getBlock() instanceof BlockTile)
                    .findFirst();

            if (optFacing.isPresent())
                variant = neighborBlocks.get(optFacing.get()).getValue(VARIANT);
        }

        return state.withProperty(VARIANT, variant);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT, FACING);
    }
}
