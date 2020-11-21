package ru.penekgaming.mc.povertycharm.block;

import akka.japi.pf.FI;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.penekgaming.mc.povertycharm.item.ItemPoverty;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class BlockConcreteSlab extends BlockRotatable {
    public static final PropertyInteger ADD_SLABS = PropertyInteger.create("add_slabs", 0, 4);

    public static final AxisAlignedBB[] BBS = {
            new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 3 / 16.0, 1.0),
            new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 6 / 16.0, 1.0),
            new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 9 / 16.0, 1.0),
            new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 12 / 16.0, 1.0),
            new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 15 / 16.0, 1.0)
    };

    public BlockConcreteSlab() {
        super("concrete_slab", Material.ROCK);

        setDefaultState(getDefaultState().withProperty(ADD_SLABS, 0));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BBS[state.getValue(ADD_SLABS)];
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState state = world.getBlockState(pos);

        if(state.getBlock() instanceof BlockConcreteSlab)
            return state.cycleProperty(ADD_SLABS);

        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int iFacing = state.getValue(FACING).getHorizontalIndex() % 2;
        int slabs = 2 * state.getValue(ADD_SLABS);

        return iFacing | slabs;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int iFacing = 1 & meta;
        int slabs = (14 & meta) / 2;

        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(iFacing)).withProperty(ADD_SLABS, slabs);
    }

    @Override
    public boolean isReplaceable(World world, BlockPos pos, Block block) {
        IBlockState state = world.getBlockState(pos);

        return block instanceof BlockConcreteSlab && state.getValue(ADD_SLABS) < 4;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ADD_SLABS);
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
