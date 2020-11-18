package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.penekgaming.mc.povertycharm.item.ItemPoverty;

import java.util.HashMap;
import java.util.Optional;

@SuppressWarnings({"deprecation", "NullableProblems"})
public class BlockConcreteBroken extends PovertyBlock {
    public static final PropertyEnum<BlockConcrete.Variant> VARIANT = PropertyEnum.create("variant", BlockConcrete.Variant.class);
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    protected BlockConcreteBroken() {
        this("concrete_broken", Material.ROCK, true);
    }

    protected BlockConcreteBroken(String name, Material material, boolean initDefState) {
        super(name, material);

        if (initDefState) {
            setDefaultState(getBlockState().getBaseState()
                    .withProperty(VARIANT, BlockConcrete.Variant.GRAY)
                    .withProperty(FACING, EnumFacing.NORTH)
            );

            item = new ItemPoverty(this);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        HashMap<EnumFacing, IBlockState> neighborBlocks = getFacingBlocks(worldIn, pos);

        EnumFacing facing = state.getValue(FACING).getOpposite();
        BlockConcrete.Variant variant = BlockConcrete.Variant.GRAY;
        IBlockState facingState = neighborBlocks.get(facing);

        if (facingState != null && (facingState.getBlock() instanceof BlockConcrete || facingState.getBlock() instanceof BlockConcreteDamaged))
            variant = neighborBlocks.get(facing).getValue(BlockConcrete.VARIANT);
        else {
            Optional<EnumFacing> optFacing
                    = neighborBlocks.keySet().stream()
                    .filter(f ->
                            neighborBlocks.get(f).getBlock() instanceof BlockConcrete
                                    || neighborBlocks.get(f).getBlock() instanceof BlockConcreteDamaged)
                    .findFirst();

            if (optFacing.isPresent())
                variant = neighborBlocks.get(optFacing.get()).getValue(VARIANT);
        }

        return state.withProperty(VARIANT, variant);
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT, FACING);
    }
}
