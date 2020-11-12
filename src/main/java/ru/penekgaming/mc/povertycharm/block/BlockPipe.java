package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({"deprecation", "NullableProblems"})
public class BlockPipe extends PovertyBlock {
    private static final PropertyDirection FACING = BlockHorizontal.FACING;
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0, 4.5 / 16d, 0, 1D, 10.5 / 16d, 1);
    private static final PropertyEnum<Part> PART = PropertyEnum.create("part", BlockPipe.Part.class);

    protected BlockPipe(String name, Part defPart) {
        super(name, Material.IRON);

        setDefaultState(
                getBlockState().getBaseState()
                        .withProperty(FACING, EnumFacing.NORTH)
                        .withProperty(PART, defPart)
        );

        item = new ItemBlock(this).setRegistryName(Objects.requireNonNull(getRegistryName()));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byIndex(meta);

        if (facing.getAxis() == EnumFacing.Axis.Y) {
            facing = EnumFacing.NORTH;
        }

        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    private EnumFacing processValveFacing(IBlockState state, EnumFacing resultFacing) {
        EnumFacing actualFacing = state.getValue(FACING);

        switch (resultFacing) {
            case EAST:
                if (actualFacing == EnumFacing.SOUTH)
                    return EnumFacing.WEST;
                else if (actualFacing == EnumFacing.NORTH)
                    return EnumFacing.EAST;

                break;

            case NORTH:
                if (actualFacing == EnumFacing.WEST)
                    return EnumFacing.SOUTH;
                else if (actualFacing == EnumFacing.EAST)
                    return EnumFacing.NORTH;

                break;
        }

        return actualFacing;
    }

    private EnumFacing getFacing(BlockPipe.Part part, HashMap<EnumFacing, Block> facings, IBlockState state) {
        switch (part) {
            case VALVE:
            case STRAIGHT:
                if ((facings.containsKey(EnumFacing.NORTH) && facings.get(EnumFacing.NORTH) instanceof BlockPipe)
                        || (facings.containsKey(EnumFacing.SOUTH) && facings.get(EnumFacing.SOUTH) instanceof BlockPipe))
                    return part == BlockPipe.Part.VALVE ? processValveFacing(state, EnumFacing.EAST) : EnumFacing.EAST;
                else if ((facings.containsKey(EnumFacing.EAST) && facings.get(EnumFacing.EAST) instanceof BlockPipe)
                        || (facings.containsKey(EnumFacing.WEST) && facings.get(EnumFacing.WEST) instanceof BlockPipe))
                    return part == BlockPipe.Part.VALVE ? processValveFacing(state, EnumFacing.NORTH) : EnumFacing.NORTH;
                else if (facings.containsKey(EnumFacing.NORTH) || facings.containsKey(EnumFacing.SOUTH))
                    return part == BlockPipe.Part.VALVE ? processValveFacing(state, EnumFacing.EAST) : EnumFacing.EAST;
                else if (facings.containsKey(EnumFacing.EAST) || facings.containsKey(EnumFacing.WEST))
                    return part == BlockPipe.Part.VALVE ? processValveFacing(state, EnumFacing.NORTH) : EnumFacing.NORTH;
                break;

            case TURN:
                HashMap<EnumFacing, Block> facingMap = new HashMap<>();
                facings.keySet().stream().filter(facing -> facings.get(facing) instanceof BlockPipe)
                        .forEach(facing -> facingMap.put(facing, facings.get(facing)));

                if (facingMap.containsKey(EnumFacing.NORTH) && facingMap.containsKey(EnumFacing.EAST))
                    return EnumFacing.EAST;
                else if (facingMap.containsKey(EnumFacing.NORTH) && facingMap.containsKey(EnumFacing.WEST))
                    return EnumFacing.NORTH;
                else if (facingMap.containsKey(EnumFacing.SOUTH) && facingMap.containsKey(EnumFacing.EAST))
                    return EnumFacing.SOUTH;
                else if (facingMap.containsKey(EnumFacing.SOUTH) && facingMap.containsKey(EnumFacing.WEST))
                    return EnumFacing.WEST;
                else if (facings.containsKey(EnumFacing.NORTH) && facings.containsKey(EnumFacing.EAST))
                    return EnumFacing.EAST;
                else if (facings.containsKey(EnumFacing.NORTH) && facings.containsKey(EnumFacing.WEST))
                    return EnumFacing.NORTH;
                else if (facings.containsKey(EnumFacing.SOUTH) && facings.containsKey(EnumFacing.EAST))
                    return EnumFacing.SOUTH;
                else if (facings.containsKey(EnumFacing.SOUTH) && facings.containsKey(EnumFacing.WEST))
                    return EnumFacing.WEST;
                break;

            case TRIO:
                Optional<EnumFacing> noTubeFacing = Arrays.stream(EnumFacing.HORIZONTALS)
                        .filter(eFacing -> !facings.containsKey(eFacing) || !(facings.get(eFacing) instanceof BlockPipe)).findFirst();

                if (!noTubeFacing.isPresent())
                    break;

                return noTubeFacing.get().rotateY().getOpposite();
        }

        return state.getValue(FACING);
    }

    private Part getPart(HashMap<EnumFacing, Block> facings) {
        if (getDefaultState().getValue(PART) == Part.VALVE)
            return Part.VALVE;

        if (isTurning(facings))
            return Part.TURN;
        else if (isTrio(facings))
            return Part.TRIO;
        else if (isQuad(facings))
            return Part.QUAD;

        return Part.STRAIGHT;
    }

    private static boolean isTurning(HashMap<EnumFacing, Block> facings) {
        switch (facings.size()) {
            case 2:
                return !(facings.containsKey(EnumFacing.NORTH) && facings.containsKey(EnumFacing.SOUTH)
                        || facings.containsKey(EnumFacing.EAST) && facings.containsKey(EnumFacing.WEST));

            case 3:
            case 4:
                long pipeCnt = facings.values().stream().filter(block -> block instanceof BlockPipe).count();
                boolean b = !(oppositeBocksAreEqual(EnumFacing.NORTH, facings) || oppositeBocksAreEqual(EnumFacing.EAST, facings));
                return pipeCnt == 2 && b;
        }

        return false;
    }

    private static boolean isTrio(HashMap<EnumFacing, Block> facings) {
        return facings.values().stream().filter(block -> block instanceof BlockPipe).count() == 3;
    }

    private static boolean isQuad(HashMap<EnumFacing, Block> facings) {
        return facings.values().stream().filter(block -> block instanceof BlockPipe).count() == 4;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        HashMap<EnumFacing, Block> facings = getFacingBlocksHorizontal(worldIn, pos);
        Part part = getPart(facings);

        return state.withProperty(PART, part)
                .withProperty(FACING, getFacing(part, facings, state));
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PART, FACING);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return BOUNDING_BOX;
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    public enum Part implements IBlockVariants {
        STRAIGHT(0, "straight"),
        TURN(1, "turn"),
        TRIO(2, "trio"),
        QUAD(3, "quad"),
        VALVE(4, "valve");

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
        public String getName() {
            return name;
        }
    }
}
