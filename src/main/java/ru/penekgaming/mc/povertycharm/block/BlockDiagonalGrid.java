package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.penekgaming.mc.povertycharm.item.ItemPovertyBlock;
import ru.penekgaming.mc.povertycharm.util.AxisAlignedBBContainer;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class BlockDiagonalGrid extends PovertyBlockMeta<EnumFacing> {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    private static final AxisAlignedBBContainer BBC = AxisAlignedBBContainer.builder()
            .set(EnumFacing.NORTH, new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.5 / 16.0))
            .set(EnumFacing.SOUTH, new AxisAlignedBB(0.0, 0.0, 1 - 0.5 / 16.0, 1.0, 1.0, 1.0))
            .set(EnumFacing.EAST, new AxisAlignedBB(1 - 0.5 / 16.0, 0.0, 0.0, 1.0, 1.0, 1.0))
            .set(EnumFacing.WEST, new AxisAlignedBB(0.0, 0.0, 0.0, 0.5 / 16.0, 1.0, 1.0))
            .build();

    public BlockDiagonalGrid() {
        super("diagonal_grid", Material.IRON, FACING, EnumFacing.NORTH);

        item = new ItemPovertyBlock(this);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, placer.getAdjustedHorizontalFacing());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BBC.get(state.getValue(FACING));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
