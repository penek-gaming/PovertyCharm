package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
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

import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class BlockFloorGrid extends PovertyBlockMeta<EnumFacing> {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool LEFT = PropertyBool.create("left");
    public static final PropertyBool RIGHT = PropertyBool.create("right");

    private static final AxisAlignedBB BB = new AxisAlignedBB(0.0, 1 - 2 / 16.0, 0.0, 1.0, 1.0, 1.0);

    protected BlockFloorGrid() {
        super("floor_grid", Material.IRON, FACING, EnumFacing.NORTH);

        setDefaultState(getDefaultState().withProperty(LEFT, false).withProperty(RIGHT, false));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing actualFacing = placer.getHorizontalFacing();

        //actualFacing = actualFacing.getHorizontalIndex() < 2 ? actualFacing.getOpposite() : actualFacing;

        return getDefaultState().withProperty(FACING, actualFacing);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);
        EnumFacing fLeft = facing.getOpposite().rotateY();
        EnumFacing fRight = facing.rotateY();

        Map<EnumFacing, Block> neighbors = getFacingBlocksHorizontal(worldIn, pos).entrySet().stream()
                .filter(e -> e.getValue() instanceof BlockFloorGrid)
                .filter(e -> e.getKey().equals(fLeft) || e.getKey().equals(fRight))
                .filter(e -> worldIn.getBlockState(pos.offset(e.getKey())).getValue(FACING) == facing
                        || worldIn.getBlockState(pos.offset(e.getKey())).getValue(FACING) == facing.getOpposite())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return state.withProperty(LEFT, neighbors.containsKey(fLeft)).withProperty(RIGHT, neighbors.containsKey(fRight));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BB;
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
        return new BlockStateContainer(this, FACING, LEFT, RIGHT);
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
