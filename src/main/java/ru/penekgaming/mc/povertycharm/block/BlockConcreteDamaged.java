package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariants;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariative;
import ru.penekgaming.mc.povertycharm.item.ItemBlockVariative;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityBlock;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityConcreteDamaged;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings({"deprecation", "NullableProblems"})
public class BlockConcreteDamaged extends TileEntityBlock<TileEntityConcreteDamaged> implements IBlockVariative {
    public static final PropertyEnum<Part> PART = PropertyEnum.create("part", Part.class);
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<BlockConcrete.Variant> VARIANT = BlockConcrete.VARIANT;

    private static final Random RANDOM = new Random();

    protected BlockConcreteDamaged() {
        super("concrete_damaged", Material.ROCK, TileEntityConcreteDamaged.class);

        setDefaultState(getDefaultState().withProperty(PART, Part.DEFAULT).withProperty(FACING, EnumFacing.NORTH));
        item = new ItemBlockVariative(this).setRegistryName(Objects.requireNonNull(getRegistryName()));
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getStateFromMeta(meta).withProperty(PART, Part.INNER_VALUES[RANDOM.nextInt(Part.INNER_VALUES.length)]);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, BlockConcrete.Variant.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        HashMap<EnumFacing, IBlockState> neighbors = getFacingBlocks(worldIn, pos);
        boolean up = neighbors.get(EnumFacing.UP).getBlock() instanceof BlockConcreteDamaged;
        boolean down = neighbors.get(EnumFacing.DOWN).getBlock() instanceof BlockConcreteDamaged;
        Part part = Part.DEFAULT;
        TileEntityConcreteDamaged entity = getTileEntity(worldIn, pos);

        if (up && down)
            part = Part.INNER_VALUES[Math.max(entity.getInnerIndex(), 0)];
        else if (up)
            part = Part.DOWN;
        else if (down)
            part = Part.UP;

        if (part == Part.UP)
            state = state.withProperty(FACING, EnumFacing.NORTH);
        else
            state = state.withProperty(FACING, entity.getFacing());

        return state.withProperty(PART, part);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (BlockConcrete.Variant v : VARIANT.getAllowedValues()) {
            items.add(new ItemStack(this, 1, v.getMetadata()));
        }
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PART, VARIANT, FACING);
    }

    @Override
    public String getVariationName(int meta) {
        return VARIANT.getAllowedValues().toArray()[meta].toString();
    }

    @Override
    public int getVariationCount() {
        return VARIANT.getAllowedValues().size();
    }

    @Override
    public TileEntityConcreteDamaged createTileEntity(World world, IBlockState blockState) {
        return new TileEntityConcreteDamaged()
                .setFacing(EnumFacing.byHorizontalIndex(RANDOM.nextInt(EnumFacing.HORIZONTALS.length)))
                .setInnerIndex(blockState.getValue(PART).getInnerIndex());
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

    public enum Part implements IBlockVariants {
        DEFAULT(0, "default"),
        DOWN(1, "down"),
        UP(2, "up"),
        INNER_1(3, "inner_1"),
        INNER_2(4, "inner_2"),
        INNER_3(5, "inner_3");

        public static final Part[] INNER_VALUES = new Part[]{INNER_1, INNER_2, INNER_3};
        private final String name;
        private final int meta;

        Part(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public int getInnerIndex() {
            return meta - 3;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getMetadata() {
            return meta;
        }
    }
}
