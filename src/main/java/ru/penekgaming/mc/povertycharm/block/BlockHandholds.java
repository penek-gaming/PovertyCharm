package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class BlockHandholds extends PovertyBlock {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool TURN = PropertyBool.create("turn");

    private static final AxisAlignedBB[] BB = {
            // SOUTH
            new AxisAlignedBB(
                    0.0, 0.0, 1 - 3 / 16.0,
                    1.0, 1.0, 1.0
            ),

            // WEST
            new AxisAlignedBB(
                    0.0, 0.0, 0.0,
                    3 / 16.0, 1.0, 1.0
            ),

            // NORTH
            new AxisAlignedBB(
                    0.0, 0.0, 0.0,
                    1.0, 1.0, 3 / 16.0
            ),

            // EAST
            new AxisAlignedBB(
                    1 - 3 / 16.0, 0.0, 0.0,
                    1.0, 1.0, 1.0
            )
    };

    protected BlockHandholds() {
        super("handholds", Material.IRON);

        setDefaultState(getBlockState().getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(TURN, false));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(FACING).getHorizontalIndex();
        if (state.getValue(TURN))
            meta |= 4;

        return meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byHorizontalIndex(meta > 3 ? meta ^ 4 : meta);
        boolean turn = (meta & 4) == 4;

        return getDefaultState().withProperty(FACING, facing).withProperty(TURN, turn);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (state.getValue(TURN))
            return FULL_BLOCK_AABB;

        return BB[state.getValue(FACING).getHorizontalIndex()];
    }

    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        for (AxisAlignedBB axisalignedbb : getCollisionBoxList(state)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, axisalignedbb);
        }
    }

    private List<AxisAlignedBB> getCollisionBoxList(IBlockState state) {
        List<AxisAlignedBB> list = new ArrayList<>();
        EnumFacing facing = state.getValue(FACING);

        list.add(BB[facing.getHorizontalIndex()]);
        if (state.getValue(TURN))
            list.add(BB[facing.rotateY().getHorizontalIndex()]);

        return list;
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        EnumFacing facing = blockState.getValue(FACING);
        RayTraceResult result = rayTrace(pos, start, end, BB[facing.getHorizontalIndex()]);
        if (result == null && blockState.getValue(TURN))
            result = rayTrace(pos, start, end, BB[facing.rotateY().getHorizontalIndex()]);

        return result;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState placeAtState = world.getBlockState(pos);
        boolean isHandholds = placeAtState.getBlock() instanceof BlockHandholds;

        return getDefaultState()
                .withProperty(FACING,
                        isHandholds
                                ? placeAtState.getValue(FACING).rotateY() == placer.getHorizontalFacing()
                                ? placeAtState.getValue(FACING)
                                : placeAtState.getValue(FACING).getOpposite().rotateY()
                                : placer.getHorizontalFacing())
                .withProperty(TURN, isHandholds && !placeAtState.getValue(TURN));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, TURN);
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return !worldIn.getBlockState(pos).getValue(TURN);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
