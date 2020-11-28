package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
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
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariants;
import ru.penekgaming.mc.povertycharm.init.PovertySounds;
import ru.penekgaming.mc.povertycharm.util.AxisAlignedBBContainer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class BlockHatch extends PovertyBlockMeta<EnumFacing> {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyBool UPPER = PropertyBool.create("upper");
    public static final PropertyBool LADDER = PropertyBool.create("ladder");
    public static final PropertyEnum<Cover> COVER = PropertyEnum.create("cover", Cover.class);

    public static final AxisAlignedBB BB_LOWER = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 3 / 16.0, 1.0);
    public static final AxisAlignedBB BB_UPPER = new AxisAlignedBB(0.0, 1 - 3 / 16.0, 0.0, 1.0, 1.0, 1.0);

    public static final AxisAlignedBBContainer BB_LADDER = AxisAlignedBBContainer.builder()
            .set(EnumFacing.NORTH, new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 3 / 16.0))
            .set(EnumFacing.EAST, new AxisAlignedBB(1 - 3 / 16.0, 0.0, 0.0, 1.0, 1.0, 1.0))
            .set(EnumFacing.SOUTH, new AxisAlignedBB(0.0, 0.0, 1 - 3 / 16.0, 1.0, 1.0, 1.0))
            .set(EnumFacing.WEST, new AxisAlignedBB(0.0, 0.0, 0.0, 3 / 16.0, 1.0, 1.0))
            .build();

    public BlockHatch() {
        super("hatch", Material.IRON, FACING, EnumFacing.NORTH);
        setDefaultState(getDefaultState()
                .withProperty(OPEN, false)
                .withProperty(UPPER, false)
                .withProperty(COVER, Cover.LOWER_CLOSED)
                .withProperty(LADDER, false)
        );
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(UPPER) ? BB_UPPER : BB_LOWER;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return blockState.getValue(OPEN)
                ? isLadderDown(blockState, worldIn, pos) ? BB_LADDER.get(blockState.getValue(FACING)) : null
                : getBoundingBox(blockState, worldIn, pos);
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return isLadderDown(state, world, pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        worldIn.setBlockState(pos, state.cycleProperty(OPEN), 2);

        if(!worldIn.isRemote) {
            worldIn.playSound(null, pos,
                    state.getValue(OPEN) ? PovertySounds.HATCH_OPEN : PovertySounds.HATCH_CLOSE,
                    SoundCategory.BLOCKS,
                    0.75f + RANDOM.nextFloat() * 0.25f,
                    1.0f + (RANDOM.nextFloat() - 0.5f) * 0.1f);
        }

        return true;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState iblockstate = this.getDefaultState();

        iblockstate = iblockstate.withProperty(FACING, placer.getHorizontalFacing());

        if (facing.getAxis().isHorizontal()) {
            iblockstate = iblockstate.withProperty(UPPER, hitY > 0.5F);
        } else {
            iblockstate = iblockstate.withProperty(UPPER, facing != EnumFacing.UP);
        }

        iblockstate = ladderDown(iblockstate, world, pos);

        return iblockstate;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        boolean upper = state.getValue(UPPER);

        state = ladderDown(state, worldIn, pos);

        if (!state.getValue(OPEN))
            return state.withProperty(COVER, upper ? Cover.UPPER_CLOSED : Cover.LOWER_CLOSED);

        Map<EnumFacing, IBlockState> freeBlocks = getFacingBlocks(worldIn, state.getValue(UPPER) ? pos.offset(EnumFacing.UP) : pos).entrySet().stream()
                .filter(e -> e.getValue().getBlock() instanceof BlockAir)
                .filter(e -> e.getKey() != EnumFacing.UP)
                .filter(e -> e.getKey() != EnumFacing.DOWN)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        EnumFacing facing = state.getValue(FACING);
        Cover cover;

        if (freeBlocks.containsKey(facing.rotateY())) // Right side
            cover = Cover.byFacing(EnumFacing.NORTH, upper);
        else if (freeBlocks.containsKey(facing.getOpposite())) // Lower side
            cover = Cover.byFacing(EnumFacing.EAST, upper);
        else if (freeBlocks.containsKey(facing.getOpposite().rotateY())) // Left side
            cover = Cover.byFacing(EnumFacing.SOUTH, upper);
        else if (freeBlocks.containsKey(facing)) // Upper side
            cover = Cover.byFacing(EnumFacing.WEST, upper);
        else
            cover = upper ? Cover.UPPER : Cover.LOWER;

        return state.withProperty(COVER, cover);
    }

    private IBlockState ladderDown(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty(LADDER, isLadderDown(state, world, pos));
    }

    @SuppressWarnings("ConstantConditions")
    private boolean isLadderDown(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState downBS = world.getBlockState(pos.offset(EnumFacing.DOWN));

        return state.getValue(UPPER)
                && downBS.getBlock().isLadder(downBS, world, pos, null)
                && downBS.getValue(FACING) == state.getValue(FACING).getOpposite();
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getValue(OPEN);
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
        EnumFacing facing = EnumFacing.byHorizontalIndex(meta > 3 ? meta ^ 12 : meta);
        boolean upper = (meta & 4) > 0;
        boolean open = (meta & 8) > 0;

        return getDefaultState().withProperty(FACING, facing).withProperty(UPPER, upper).withProperty(OPEN, open);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(FACING).getHorizontalIndex();

        if (state.getValue(UPPER))
            meta |= 4;

        if (state.getValue(OPEN))
            meta |= 8;

        return meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, OPEN, UPPER, COVER, LADDER);
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

    public enum Cover implements IBlockVariants {
        LOWER_NORTH(0, "lower_north"),
        LOWER_EAST(1, "lower_east"),
        LOWER_SOUTH(2, "lower_south"),
        LOWER_WEST(3, "lower_west"),

        UPPER_NORTH(4, "upper_north"),
        UPPER_EAST(5, "upper_east"),
        UPPER_SOUTH(6, "upper_south"),
        UPPER_WEST(7, "upper_west"),

        LOWER(8, "lower"),
        UPPER(9, "upper"),
        LOWER_CLOSED(10, "lower_closed"),
        UPPER_CLOSED(11, "upper_closed");

        private final int metadata;
        private final String name;

        static final HashMap<EnumFacing, Cover> LOWERS = new HashMap<EnumFacing, Cover>() {{
            put(EnumFacing.NORTH, LOWER_NORTH);
            put(EnumFacing.EAST, LOWER_EAST);
            put(EnumFacing.SOUTH, LOWER_SOUTH);
            put(EnumFacing.WEST, LOWER_WEST);
        }};

        static final HashMap<EnumFacing, Cover> UPPERS = new HashMap<EnumFacing, Cover>() {{
            put(EnumFacing.NORTH, UPPER_NORTH);
            put(EnumFacing.EAST, UPPER_EAST);
            put(EnumFacing.SOUTH, UPPER_SOUTH);
            put(EnumFacing.WEST, UPPER_WEST);
        }};

        Cover(int metadata, String name) {
            this.metadata = metadata;
            this.name = name;
        }

        @Override
        public int getMetadata() {
            return metadata;
        }

        @Override
        public String getName() {
            return name;
        }

        public static Cover byFacing(EnumFacing facing, boolean upper) {
            return upper ? UPPERS.get(facing) : LOWERS.get(facing);
        }
    }
}
