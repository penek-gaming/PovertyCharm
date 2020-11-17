package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariative;
import ru.penekgaming.mc.povertycharm.item.ItemBlockVariative;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityBlock;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityTire;
import ru.penekgaming.mc.povertycharm.util.AxisAlignedBBContainer;

@SuppressWarnings({"deprecation", "NullableProblems"})
public class BlockTire extends TileEntityBlock<TileEntityTire> implements IBlockVariative {
    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    private static final AxisAlignedBBContainer BBC = AxisAlignedBBContainer.builder()
            .set(EnumFacing.NORTH, new AxisAlignedBB(0.0, 0, 5.0 / 16.0, 1.0, 7.5 / 16.0, 1 - 5.0 / 16.0))
            .set(EnumFacing.EAST, new AxisAlignedBB(5.0 / 16.0, 0, 0.0, 1 - 5.0 / 16.0, 7.5 / 16.0, 1.0))
            .build();

    protected BlockTire(String name, Material material) {
        super(name, material, TileEntityTire.class);

        setDefaultState(blockState.getBaseState().withProperty(COLOR, EnumDyeColor.WHITE).withProperty(FACING, EnumFacing.NORTH));
        item = new ItemBlockVariative(this);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);
        TileEntityTire entity = getTileEntity(source, pos);

        if (entity != null)
            facing = entity.getFacing();

        switch (facing) {
            case NORTH:
            case SOUTH:
                return BBC.get(EnumFacing.NORTH);

            case EAST:
            case WEST:
                return BBC.get(EnumFacing.EAST);
        }

        return FULL_BLOCK_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return getBoundingBox(state, source, pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!(playerIn.getHeldItem(hand).getItem() instanceof ItemDye))
            return false;

        ItemStack heldStack = playerIn.getHeldItem(hand);

        return recolorBlock(worldIn, pos, facing, EnumDyeColor.byDyeDamage(heldStack.getItem().getDamage(heldStack)));
    }

    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        if (entityIn.isSneaking())
            super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
        else
            entityIn.fall(fallDistance, 0.0F);
    }

    public void onLanded(World worldIn, Entity entityIn) {
        if (entityIn.isSneaking())
            super.onLanded(worldIn, entityIn);
        else if (entityIn.motionY < 0.0)
            entityIn.motionY = -entityIn.motionY * 0.95;
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.getBlockColor(state.getValue(COLOR));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
    }

    private boolean canBlockStay(World worldIn, BlockPos pos) {
        return !worldIn.isAirBlock(pos.down());
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(COLOR).getMetadata();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (int i = 0; i < 16; ++i) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(COLOR).getMetadata();
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(COLOR, EnumDyeColor.byMetadata(meta));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(FACING, getTileEntity(worldIn, pos).getFacing());
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

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, COLOR, FACING);
    }

    @Override
    public String getVariationName(int meta) {
        return EnumDyeColor.values()[meta].getName();
    }

    @Override
    public int getVariationCount() {
        return EnumDyeColor.values().length;
    }

    @Override
    public TileEntityTire createTileEntity(World world, IBlockState blockState) {
        TileEntityTire entity = new TileEntityTire();
        entity.setFacing(blockState.getValue(FACING));
        return entity;
    }
}
