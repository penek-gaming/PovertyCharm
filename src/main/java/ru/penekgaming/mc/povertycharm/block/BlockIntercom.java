package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.penekgaming.mc.povertycharm.PovertyCharm;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariants;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariative;
import ru.penekgaming.mc.povertycharm.init.PovertySounds;
import ru.penekgaming.mc.povertycharm.item.ItemBlockVariative;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityBlock;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityIntercom;
import ru.penekgaming.mc.povertycharm.util.AxisAlignedBBContainer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

@SuppressWarnings({"deprecation", "NullableProblems"})
public class BlockIntercom extends TileEntityBlock<TileEntityIntercom> implements IBlockVariative {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
    public static final PropertyEnum<State> STATE = PropertyEnum.create("state", State.class);
    public static final int OPEN_TICKS = 60;

    public static final HashMap<Variant, AxisAlignedBBContainer> BBS = new HashMap<Variant, AxisAlignedBBContainer>() {{
        put(Variant.HOME,
                AxisAlignedBBContainer.builder()
                        .set(EnumFacing.NORTH, new AxisAlignedBB(
                                4 / 16.0, 7 / 16.0, 0.0,
                                1 - 4 / 16.0, 1 - 5 / 16.0, 1 / 16.0))
                        .set(EnumFacing.EAST, new AxisAlignedBB(
                                1 - 1 / 16.0, 7 / 16.0, 4 / 16.0,
                                1.0, 1 - 5 / 16.0, 1 - 4 / 16.0))
                        .set(EnumFacing.SOUTH, new AxisAlignedBB(
                                4 / 16.0, 7 / 16.0, 1 - 1 / 16.0,
                                1 - 4 / 16.0, 1 - 5 / 16.0, 1.0))
                        .set(EnumFacing.WEST, new AxisAlignedBB(
                                0.0, 7 / 16.0, 4 / 16.0,
                                1 / 16.0, 1 - 5 / 16.0, 1 - 4 / 16.0))
                        .build());
    }};

    public BlockIntercom() {
        super("intercom", Material.IRON, TileEntityIntercom.class);

        setDefaultState(getBlockState().getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(STATE, State.CLOSED)
        );

        item = new ItemBlockVariative(this);
        setNoPlaceCollision(true);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BBS.get(state.getValue(VARIANT)).get(state.getValue(FACING));
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getStateFromMeta(meta).withProperty(FACING, placer.getHorizontalFacing()).withProperty(STATE, State.DENIED);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntityIntercom entityIntercom = getTileEntity(worldIn, pos);

        entityIntercom.setOwner(placer.getUniqueID());
        entityIntercom.setUuid(UUID.randomUUID());

        PovertyCharm.PROXY.showGui(pos);
    }

    @Override
    public TileEntityIntercom createTileEntity(World world, IBlockState blockState) {
        PovertyCharm.LOGGER.warn("createTileEntity");
        TileEntityIntercom entityIntercom = new TileEntityIntercom();
        entityIntercom.setCode(TileEntityIntercom.DEFAULT_CODE);

        return entityIntercom;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.getBlockState(pos).getValue(STATE) != State.CLOSED)
            return true;

        TileEntityIntercom tileEntity = getTileEntity(worldIn, pos);

        if (tileEntity.isPublic()) {
            open(worldIn, pos, state);
            return true;
        }

        PovertyCharm.PROXY.showGui(pos);

        return true;

    }

    public void openByKey(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote)
            world.playSound(null, pos, PovertySounds.INTERCOM_ACCEPT, SoundCategory.BLOCKS, 1.0f, 1.0f);

        world.setBlockState(pos, state.withProperty(STATE, State.PENDING), 3);
        world.scheduleUpdate(pos, this, 20);
    }

    public void openByCode(World world, BlockPos pos, IBlockState state, String code) {
        TileEntityIntercom entityIntercom = getTileEntity(world, pos);

        if (!entityIntercom.getCode().equals(code) && !entityIntercom.isPublic()) {
            world.playSound(null, pos, PovertySounds.INTERCOM_REJECT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.setBlockState(pos, state.withProperty(STATE, State.DENIED), 3);
            world.scheduleUpdate(pos, this, 40);
            return;
        }

        open(world, pos, state);
    }

    public void open(World world, BlockPos pos, IBlockState state) {
        if (world.isRemote)
            return;

        world.setBlockState(pos, state.withProperty(STATE, State.OPENED), 3);
        world.markBlockRangeForRenderUpdate(pos, pos);
        notifyNeighbors(world, pos, state.getValue(FACING));
        world.scheduleUpdate(pos, this, OPEN_TICKS);

        world.playSound(null, pos, PovertySounds.INTERCOM_OPEN, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    public void close(World world, BlockPos pos, IBlockState state) {
        if (world.isRemote)
            return;

        world.setBlockState(pos, state.withProperty(STATE, State.CLOSED));
        this.notifyNeighbors(world, pos, state.getValue(FACING));
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    public void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing facing) {
        worldIn.notifyNeighborsOfStateChange(pos, this, false);
        worldIn.notifyNeighborsOfStateChange(pos.offset(facing), this, false);
    }

    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return blockState.getValue(STATE) == State.OPENED ? 15 : 0;
    }

    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        if (blockState.getValue(STATE) == State.OPENED) {
            return blockState.getValue(FACING) == side.getOpposite() ? 15 : 0;
        } else {
            return 0;
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getValue(STATE) == State.OPENED)
            notifyNeighbors(worldIn, pos, state.getValue(FACING));

        super.breakBlock(worldIn, pos, state);
    }

    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
//        if (worldIn.isRemote)
//            return;

        switch (state.getValue(STATE)) {
            case PENDING:
            case CLOSED:
                open(worldIn, pos, state);
                break;

            case DENIED:
            case OPENED:
                close(worldIn, pos, state);
                break;
        }

        //close(worldIn, pos, state);
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
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (Variant v : VARIANT.getAllowedValues()) {
            items.add(new ItemStack(this, 1, v.getMetadata()));
        }
    }

    @Override
    public String getVariationName(int meta) {
        return Variant.values()[(12 & meta) / 4].getName();
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, VARIANT, STATE);
    }

    @Override
    public int getVariationCount() {
        return Variant.values().length;
    }

    public enum Variant implements IBlockVariants {
        HOME(0, "home");

        private final int meta;
        private final String name;

        Variant(int meta, String name) {
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

        @Override
        public String toString() {
            return name;
        }
    }

    public enum State implements IBlockVariants {
        CLOSED(0, "closed"),
        PENDING(1, "pending"),
        OPENED(2, "opened"),
        DENIED(3, "denied");

        private final int meta;
        private final String name;

        State(int meta, String name) {
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

        @Override
        public String toString() {
            return name;
        }
    }
}
