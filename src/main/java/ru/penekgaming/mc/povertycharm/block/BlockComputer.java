package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariants;
import ru.penekgaming.mc.povertycharm.init.PovertySounds;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityBlock;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityComputer;
import ru.penekgaming.mc.povertycharm.util.AxisAlignedBBContainer;

import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings({"deprecation", "NullableProblems"})
public class BlockComputer extends TileEntityBlock<TileEntityComputer> {
    public static PropertyDirection FACING = BlockHorizontal.FACING;
    public static PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

    private static final AxisAlignedBBContainer BB_COLLISIONS = AxisAlignedBBContainer.builder()
            .set(EnumFacing.NORTH, new AxisAlignedBB(
                    1 / 16.0, 0.0, 0.0,
                    1 - 1 / 16.0, 1.0, 9 / 16.0
            ))
            .set(EnumFacing.EAST, new AxisAlignedBB(
                    1.0 - 9 / 16.0, 0.0, 1 / 16.0,
                    1.0, 1.0, 1.0 - 1 / 16.0
            ))
            .set(EnumFacing.SOUTH, new AxisAlignedBB(
                    1 / 16.0, 0.0, 1 - 9 / 16.0,
                    1 - 1 / 16.0, 1.0, 1.0
            ))
            .set(EnumFacing.WEST, new AxisAlignedBB(
                    0.0, 0.0, 1 / 16.0,
                    9 / 16.0, 1.0, 1.0 - 1 / 16.0
            ))
            .build();

    public BlockComputer() {
        super("computer", Material.IRON, TileEntityComputer.class);

        setHardness(0.5f);
        setTickRandomly(true);

        setDefaultState(getBlockState().getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(VARIANT, Variant.XP)
        );
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return canStayOn(worldIn, pos, EnumFacing.DOWN, BlockFaceShape.SOLID);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isAirBlock(pos.offset(EnumFacing.DOWN)) && canStayOn(worldIn, pos, EnumFacing.DOWN, BlockFaceShape.SOLID))
            return;

        worldIn.destroyBlock(pos, true);
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        EnumFacing facing = blockState.getValue(FACING);

        return rayTrace(pos, start, end, BB_COLLISIONS.get(facing));
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return BB_COLLISIONS.get(blockState.getValue(FACING));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Variant variant = getTileEntity(worldIn, pos).getVariant();
        Variant newVariant = Variant.values()[(variant.getMetadata() + 1) % Variant.values().length];

        getTileEntity(worldIn, pos).setVariant(newVariant);
        worldIn.notifyBlockUpdate(pos, state, state.withProperty(VARIANT, newVariant), 0);

        return true;
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        if (!worldIn.isRemote) {
            state = state.getActualState(worldIn, pos);
            SoundEvent sound = PovertySounds.COMP_AMBIENT;
            SoundEvent customSound = state.getValue(VARIANT).getCustomSound();

            if (customSound != null && random.nextFloat() <= 0.15f)
                sound = customSound;

            worldIn.playSound(null, pos,
                    sound,
                    SoundCategory.BLOCKS,
                    0.65f,
                    1.0f);
        }
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);

        // May be implemented in future
//        if(!worldIn.isRemote) {
//            float rnd = RANDOM.nextFloat();
//
//            if ((fallDistance + 2) / 10 >= rnd) {
//                TileEntityComputer entityComputer = getTileEntity(worldIn, pos);
//
//
//                if (entityComputer == null) {
//                    PovertyCharm.LOGGER.warn("null is null");
//                    return;
//                }
//
//                entityComputer.setVariant(Variant.BSOD);
//            }
//        }
//
//        IBlockState state = worldIn.getBlockState(pos);
//        worldIn.notifyBlockUpdate(pos, state, state.withProperty(VARIANT, getTileEntity(worldIn, pos).getVariant()), 0);
    }


    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(VARIANT, getTileEntity(worldIn, pos).getVariant());
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public TileEntityComputer createTileEntity(World world, IBlockState blockState) {
        TileEntityComputer computer = new TileEntityComputer();
        computer.setVariant(Variant.XP);
        return computer;
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, VARIANT);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
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
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public enum Variant implements IBlockVariants {
        BSOD(0, "bsod", PovertySounds.COMP_BSOD),
        XP(1, "xp"),
        DOTA(2, "dota", PovertySounds.COMP_DOTA),
        CS(3, "cs", PovertySounds.COMP_CS16),
        LINUX(4, "linux"),
        HEROES(5, "heroes", PovertySounds.COMP_HEROES),
        LINEAGE(6, "lineage", PovertySounds.COMP_LINEAGE),
        SR(7, "sr", PovertySounds.COMP_SR);

        private final int meta;
        private final String name;
        private final SoundEvent customSound;

        Variant(int meta, String name, SoundEvent customSound) {
            this.meta = meta;
            this.name = name;
            this.customSound = customSound;
        }

        Variant(int meta, String name) {
            this(meta, name, null);
        }

        @Override
        public int getMetadata() {
            return meta;
        }

        @Override
        public String getName() {
            return name;
        }

        public SoundEvent getCustomSound() {
            return customSound;
        }
    }
}
