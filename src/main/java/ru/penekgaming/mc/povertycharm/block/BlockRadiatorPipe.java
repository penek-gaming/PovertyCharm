package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
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

@SuppressWarnings({"deprecation", "NullableProblems"})
public class BlockRadiatorPipe extends BlockRadiator {
    public final static PropertyEnum<Part> PART = PropertyEnum.create("part", Part.class);
    public final static PropertyBool ROTATE = PropertyBool.create("rotate");

    private static final AxisAlignedBBContainer BBS_TURN_OUTER = AxisAlignedBBContainer.builder()
            .set(EnumFacing.NORTH, new AxisAlignedBB(0.0, 1.5 / 16, 0.0, 3.5 / 16, 1 - 2.75 / 16, 3.5 / 16))
            .set(EnumFacing.EAST, new AxisAlignedBB(1 - 3.5 / 16, 1.5 / 16, 0.0, 1.0, 1 - 2.75 / 16, 3.5 / 16))
            .set(EnumFacing.SOUTH, new AxisAlignedBB(1 - 3.5 / 16, 1.5 / 16, 1 - 3.5 / 16, 1.0, 1 - 2.75 / 16, 1.0))
            .set(EnumFacing.WEST, new AxisAlignedBB(0.0, 1.5 / 16, 1 - 3.5 / 16, 3.5 / 16, 1 - 2.75 / 16, 1.0))
            .build();

    public BlockRadiatorPipe() {
        super("radiator_pipe");
        setDefaultState(getDefaultState().withProperty(PART, Part.STRAIGHT).withProperty(ROTATE, false));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        state = state.getActualState(source, pos);
        switch (state.getValue(PART)) {
            case TURN_CONNECTION:
                return BBS_TURN_OUTER.get(state.getValue(FACING));
            case TURN:
                return FULL_BLOCK_AABB;
            default:
                return super.getBoundingBox(state, source, pos);
        }
    }

    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        state = state.getActualState(worldIn, pos);
        EnumFacing facing = state.getValue(FACING);

        addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getValue(PART) == Part.TURN_CONNECTION ? BBS_TURN_OUTER.get(facing) : BBS_MAIN.get(facing));
        if (state.getValue(PART) == Part.TURN)
            addCollisionBoxToList(pos, entityBox, collidingBoxes, BBS_MAIN.get(facing.rotateY()));
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        blockState = blockState.getActualState(worldIn, pos);
        EnumFacing facing = blockState.getValue(FACING);
        RayTraceResult result = rayTrace(pos, start, end, BBS_MAIN.get(facing));

        if (result == null) {
            switch (blockState.getValue(PART)) {
                case TURN:
                    result = rayTrace(pos, start, end, BBS_MAIN.get(facing.rotateY()));
                    break;

                case TURN_CONNECTION:
                    result = rayTrace(pos, start, end, BBS_TURN_OUTER.get(facing));
                    break;
            }
        }

        return result;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        Map<EnumFacing, IBlockState> neighbors = getFacingBlocks(worldIn, pos).entrySet().stream()
                .filter(e -> e.getValue().getBlock() instanceof BlockRadiator)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        EnumFacing facing = state.getValue(FACING);

        if (neighbors.containsKey(facing) && (neighbors.containsKey(facing.rotateY().getOpposite()) || neighbors.containsKey(facing.rotateY())))
            state = state.withProperty(PART, Part.TURN_CONNECTION).withProperty(FACING, neighbors.containsKey(facing.getOpposite().rotateY()) ? facing : facing.rotateY());
        else if (neighbors.containsKey(facing.getOpposite())
                && worldIn.getBlockState(pos.offset(state.getValue(FACING).getOpposite())).getValue(FACING) == facing.rotateY())
            state = state.withProperty(PART, Part.TURN);

        return super.getActualState(state, worldIn, pos);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, PART, ROTATE);
    }

    public enum Part implements IBlockVariants {
        STRAIGHT(0, "straight"),
        CROSS_V(1, "cross"),
        TURN(2, "turn"),
        TURN_CONNECTION(3, "turn_conn"),
        TURN_UP(4, "turn_up"),
        VERTICAL(5, "vertical");

        private final int meta;
        private final String name;

        Part(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        @Override
        public int getMetadata() {
            return meta;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
