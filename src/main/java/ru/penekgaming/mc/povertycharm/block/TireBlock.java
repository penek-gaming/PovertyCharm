package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.*;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
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
import ru.penekgaming.mc.povertycharm.item.PovertyItemBlockVariative;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityBlock;
import ru.penekgaming.mc.povertycharm.tileentity.TireTileEntity;

import java.util.Objects;

@SuppressWarnings({"deprecation", "NullableProblems"})
public class TireBlock extends TileEntityBlock<TireTileEntity> implements IBlockVariative {
    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    private static final AxisAlignedBB BB_NS
            = new AxisAlignedBB(0.0, 0, 5.0/16.0, 1.0,7.5/16.0, 1 - 5.0/16.0);

    private static final AxisAlignedBB BB_EW
            = new AxisAlignedBB(5.0/16.0, 0, 0.0, 1 - 5.0/16.0,7.5/16.0, 1.0);

    protected TireBlock(String name, Material material) {
        super(name, material, TireTileEntity.class);

        setDefaultState(blockState.getBaseState().withProperty(COLOR, EnumDyeColor.WHITE).withProperty(FACING, EnumFacing.NORTH));
        item = new PovertyItemBlockVariative(this).setRegistryName(Objects.requireNonNull(getRegistryName()));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);
        TireTileEntity entity = getTileEntity(source, pos);

        if(entity != null)
            facing = entity.getFacing();

        switch (facing){
            case NORTH:
            case SOUTH:
                return BB_NS;

            case EAST:
            case WEST:
                return BB_EW;
        }

        return FULL_BLOCK_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return getBoundingBox(state, source, pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(playerIn.getHeldItem(hand).getItem() instanceof ItemDye)
            return recolorBlock(worldIn, pos, facing, EnumDyeColor.byMetadata(playerIn.getHeldItem(hand).getItem().getMetadata(playerIn.getHeldItem(hand)) + 1));

        return false;
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return MapColor.getBlockColor(state.getValue(COLOR));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
    }

    private boolean canBlockStay(World worldIn, BlockPos pos)
    {
        return !worldIn.isAirBlock(pos.down());
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(COLOR).getMetadata();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
        for (int i = 0; i < 16; ++i)
        {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(COLOR).getMetadata();
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(COLOR, EnumDyeColor.byMetadata(meta));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(FACING, EnumFacing.byIndex(Math.max(2, getTileEntity(worldIn, pos).getFacing().getIndex())));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public BlockStateContainer createBlockState()
    {
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
    public TireTileEntity createTileEntity(World world, IBlockState blockState) {
        TireTileEntity entity = new TireTileEntity();
        entity.setFacing(blockState.getValue(FACING));
        return entity;
    }
}
