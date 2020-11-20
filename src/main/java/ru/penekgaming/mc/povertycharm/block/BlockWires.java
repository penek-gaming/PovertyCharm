package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
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
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariants;
import ru.penekgaming.mc.povertycharm.util.AxisAlignedBBContainer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class BlockWires extends BlockRotatable {
    public static final PropertyEnum<Part> PART = PropertyEnum.create("part", Part.class);
    public static final PropertyEnum<TurnOuter> TURN_OUTER = PropertyEnum.create("turn_outer", TurnOuter.class);

    public static final AxisAlignedBBContainer BBS = AxisAlignedBBContainer.builder()
            .set(EnumFacing.NORTH, new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 4 / 16.0))
            .set(EnumFacing.EAST, new AxisAlignedBB(1 - 4 / 16.0, 0.0, 0.0, 1.0, 1.0, 1.0))
            .set(EnumFacing.SOUTH, new AxisAlignedBB(0.0, 0.0, 1 - 4 / 16.0, 1.0, 1.0, 1.0))
            .set(EnumFacing.WEST, new AxisAlignedBB(0.0, 0.0, 0.0, 4 / 16.0, 1.0, 1.0))
            .build();

    public BlockWires() {
        super("wires", Material.CLOTH);

        setDefaultState(getDefaultState().withProperty(TURN_OUTER, TurnOuter.NONE));
        setNoPlaceCollision(true);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, facing.getOpposite());
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return !(side == EnumFacing.UP || side == EnumFacing.DOWN) && worldIn.getBlockState(pos.offset(side.getOpposite())).isFullBlock();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        state = super.getActualState(state, worldIn, pos);
        EnumFacing facing = state.getValue(FACING);

        Map<EnumFacing, IBlockState> neighbors = getFacingBlocks(worldIn, pos).entrySet().stream()
                .filter(e -> e.getValue().getBlock() instanceof BlockWires)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        IBlockState down = worldIn.getBlockState(pos.offset(facing.getOpposite()));

        if(down.getBlock() instanceof BlockWires && !(neighbors.containsKey(facing.rotateY()) && neighbors.containsKey(facing.rotateY().getOpposite()))) {
            if(down.getValue(FACING) == facing.rotateY())
                state = state.withProperty(PART, Part.TURN_INNER);
            else if(down.getValue(FACING) == facing.getOpposite().rotateY())
                state = state.withProperty(PART, Part.TURN_INNER).withProperty(FACING, facing.getOpposite().rotateY());
        } else if(worldIn.getBlockState(pos.offset(facing.rotateY()).offset(facing)).getBlock() instanceof BlockWires) {
            state = state.withProperty(TURN_OUTER, TurnOuter.DEFAULT);
        }

        if(worldIn.getBlockState(pos.offset(facing.rotateY()).offset(facing.getOpposite())).getBlock() instanceof BlockWires) {
            state = state.withProperty(PART, Part.TURN_INNER).withProperty(TURN_OUTER, TurnOuter.ROT_Y);
        } else if(worldIn.getBlockState(pos.offset(facing.rotateY().getOpposite()).offset(facing.getOpposite())).getBlock() instanceof BlockWires) {
            state = state.withProperty(PART, Part.TURN_INNER).withProperty(FACING, facing.getOpposite().rotateY());
            if(worldIn.getBlockState(pos.offset(facing).offset(facing.rotateY())).getBlock() instanceof BlockWires)
                state = state.withProperty(TURN_OUTER, TurnOuter.ROT_Y);
        }

        return state;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (state.getActualState(source, pos).getValue(PART) == Part.TURN_INNER)
            return FULL_BLOCK_AABB;

        return BBS.get(state.getValue(FACING));
    }

    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        state = state.getActualState(worldIn, pos);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BBS.get(state.getValue(FACING)));

        if(state.getValue(PART) == Part.TURN_INNER)
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BBS.get(state.getValue(FACING).rotateY()));
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        blockState = blockState.getActualState(worldIn, pos);
        EnumFacing facing = blockState.getValue(FACING);

        RayTraceResult result = rayTrace(pos, start, end, BBS.get(facing));

        if (result == null && blockState.getValue(PART) == Part.TURN_INNER)
            result = rayTrace(pos, start, end, BBS.get(facing.rotateY()));

        return result;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, PART, TURN_OUTER);
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

    public enum Part implements IBlockVariants {
        STRAIGHT(0, "straight"),
        TURN_INNER(1, "turn_inner")
        ;

        private final int meta;
        private final String name;

        Part(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int getMetadata() {
            return meta;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum TurnOuter implements IBlockVariants {
        NONE(0, "none"),
        DEFAULT(1, "default"),
        ROT_Y(2, "rot_y")
        ;

        private final int meta;
        private final String name;

        TurnOuter(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int getMetadata() {
            return meta;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
