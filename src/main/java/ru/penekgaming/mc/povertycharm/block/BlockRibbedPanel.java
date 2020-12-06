package ru.penekgaming.mc.povertycharm.block;

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
import ru.penekgaming.mc.povertycharm.util.AxisAlignedBBContainer;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class BlockRibbedPanel extends PovertyBlockMeta<EnumFacing> {
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool HORIZONTAL = PropertyBool.create("horizontal");

    public static final AxisAlignedBBContainer BBS = AxisAlignedBBContainer.builder()
            .set(EnumFacing.NORTH, new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 3 / 16.0))
            .set(EnumFacing.EAST, new AxisAlignedBB(1 - 3 / 16.0, 0.0, 0.0, 1.0, 1.0, 1.0))
            .set(EnumFacing.SOUTH, new AxisAlignedBB(0.0, 0.0, 1 - 3 / 16.0, 1.0, 1.0, 1.0))
            .set(EnumFacing.WEST, new AxisAlignedBB(0.0, 0.0, 0.0, 3 / 16.0, 1.0, 1.0))
            .set(EnumFacing.DOWN, new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 4 / 16.0, 1.0))
            .set(EnumFacing.UP, new AxisAlignedBB(0.0, 1 - 3 / 16.0, 0.0, 1.0, 1.0, 1.0))
            .build();

    public BlockRibbedPanel(boolean horizontal) {
        super("ribbed_panel" + (horizontal ? "_horizontal" : ""), Material.IRON, FACING, EnumFacing.NORTH);
        setNoPlaceCollision(true);
        useCustomStateMapper = true;

        setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(HORIZONTAL, horizontal));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        if(placer.isSneaking())
            return getDefaultState().withProperty(FACING, facing.getOpposite());

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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BBS.get(state.getValue(FACING));
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
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, HORIZONTAL);
    }

    @Override
    public String getStatePath() {
        return "ribbed_panel";
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