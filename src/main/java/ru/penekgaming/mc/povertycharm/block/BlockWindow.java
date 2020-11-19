package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariants;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariative;
import ru.penekgaming.mc.povertycharm.item.ItemBlockVariative;
import ru.penekgaming.mc.povertycharm.util.AxisAlignedBBContainer;

import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class BlockWindow extends PovertyBlockMeta<EnumFacing> implements IBlockVariative {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
    public static final PropertyBool SPLIT = PropertyBool.create("split");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyBool LEFT = PropertyBool.create("left");
    public static final PropertyBool RIGHT = PropertyBool.create("right");

    public final boolean vertical;

    public static final AxisAlignedBBContainer BBS = AxisAlignedBBContainer.builder()
            .set(EnumFacing.NORTH, new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 2.2 / 16.0))
            .set(EnumFacing.EAST, new AxisAlignedBB(1 - 2.2 / 16.0, 0.0, 0.0, 1.0, 1.0, 1.0))
            .set(EnumFacing.SOUTH, new AxisAlignedBB(0.0, 0.0, 1 - 2.2 / 16.0, 1.0, 1.0, 1.0))
            .set(EnumFacing.WEST, new AxisAlignedBB(0.0, 0.0, 0.0, 2.2 / 16.0, 1.0, 1.0))
            .build();

    public BlockWindow(String name, boolean split, boolean vertical) {
        super(name, Material.GLASS, FACING, EnumFacing.NORTH);
        setDefaultState(getDefaultState()
                .withProperty(VARIANT, Variant.PLASTIC)
                .withProperty(UP, false)
                .withProperty(DOWN, false)
                .withProperty(LEFT, false)
                .withProperty(RIGHT, false)
                .withProperty(SPLIT, split)
        );

        this.vertical = vertical;

        setNoPlaceCollision(true);
        item = new ItemBlockVariative(this);
        useCustomStateMapper = true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BBS.get(state.getValue(FACING));
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        if (placer.getHorizontalFacing() != facing.getOpposite()) {
            switch (placer.getHorizontalFacing()) {
                case NORTH:
                case SOUTH:
                    facing = hitZ > 0.5 ? EnumFacing.SOUTH : EnumFacing.NORTH;
                    break;

                case EAST:
                case WEST:
                    facing = hitX > 0.5 ? EnumFacing.EAST : EnumFacing.WEST;
                    break;
            }
        } else {
            facing = facing.getOpposite();
        }

        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);

        Map<EnumFacing, IBlockState> neighbors = getFacingBlocks(worldIn, pos).entrySet().stream()
                .filter(e -> e.getValue().getBlock() instanceof BlockWindow)
                .filter(e -> e.getValue().getValue(FACING) == facing)
                .filter(e -> ((BlockWindow) e.getValue().getBlock()).vertical == vertical)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        boolean up = neighbors.containsKey(EnumFacing.UP);
        boolean down = neighbors.containsKey(EnumFacing.DOWN);

        if (!vertical) {
            boolean left = neighbors.containsKey(facing.getOpposite().rotateY());
            boolean right = neighbors.containsKey(facing.rotateY());

            state = state.withProperty(LEFT, left).withProperty(RIGHT, right);
        }

        return state
                .withProperty(UP, up)
                .withProperty(DOWN, down);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 4 * state.getValue(VARIANT).getMetadata();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (Variant v : Variant.values()) {
            items.add(new ItemStack(this, 1, 4 * v.getMetadata()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int iFacing = 3 & meta;
        int iVariant = (12 & meta) / 4;

        return getDefaultState()
                .withProperty(FACING, EnumFacing.byHorizontalIndex(iFacing))
                .withProperty(VARIANT, Variant.values()[iVariant]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int iFacing = state.getValue(FACING).getHorizontalIndex();
        int iVariant = state.getValue(VARIANT).getMetadata();

        return iFacing | (4 * iVariant);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, VARIANT, UP, DOWN, LEFT, RIGHT, SPLIT);
    }

    @Override
    public String getVariationName(int meta) {
        return Variant.values()[(12 & meta) / 4].getName();
    }

    @Override
    public int getVariationCount() {
        return Variant.values().length;
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
    public String getStatePath() {
        return "window";
    }

    public enum Variant implements IBlockVariants {
        PLASTIC(0, "plastic");

        private final int meta;
        private final String name;

        Variant(int meta, String name) {
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
