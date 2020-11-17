package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.penekgaming.mc.povertycharm.Config;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class BlockTrashCan extends PovertyBlock {
    private static final Random RANDOM = new Random();

    public static final int MAX_FILL = 3;
    public static final PropertyInteger FILL = PropertyInteger.create("fill", 0, MAX_FILL);
    public static final AxisAlignedBB BB = new AxisAlignedBB(2 / 16.0, 0.0, 2 / 16.0, 1 - 2 / 16.0, 15 / 16.0, 1 - 2 / 16.0);

    protected BlockTrashCan() {
        super("trash_can", Material.IRON);
        setTickRandomly(true);

        setDefaultState(getBlockState().getBaseState().withProperty(FILL, 0));
        item = new ItemBlock(this).setRegistryName(Objects.requireNonNull(getRegistryName()));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BB;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state.getValue(FILL) < MAX_FILL)
            return false;

        if (!worldIn.isRemote) {
            worldIn.setBlockState(pos, state.withProperty(FILL, 0));

            if (Config.TrashCan.expDropChance > 0 && Config.TrashCan.maxExpDrop > 0 && RANDOM.nextDouble() <= Config.TrashCan.expDropChance)
                dropXpOnBlockBreak(worldIn, pos, Config.TrashCan.minExpDrop + RANDOM.nextInt(Config.TrashCan.maxExpDrop - Config.TrashCan.minExpDrop));

            tryDropItem(worldIn, pos);
        }

        return true;
    }

    private void tryDropItem(World world, BlockPos pos) {
        if (Config.TrashCan.itemDropChance <= 0 || RANDOM.nextDouble() > Config.TrashCan.itemDropChance)
            return;

        AtomicBoolean dropped = new AtomicBoolean(false);
        HashMap<Item, Integer> items = Config.TrashCan.getItemRandomCollection().next();

        if(items.size() < 1)
            return;

        items.forEach(
                (item, maxCount) -> {
                    int count = RANDOM.nextInt(Math.max(1, maxCount + 1));
                    if (count > 0) {
                        dropped.set(true);
                        spawnItem(world, pos, item, count);
                    }
                }
        );

        if (!dropped.get())
            spawnItem(world, pos, (Item) items.keySet().toArray()[RANDOM.nextInt(items.size())], 1);
    }

    private void spawnItem(World world, BlockPos pos, Item item, int count) {
        world.spawnEntity(
                new EntityItem(world, pos.getX(), pos.offset(EnumFacing.UP).getY(), pos.getZ(),
                        new ItemStack(item, count)
                )
        );
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (Config.TrashCan.updateChance <= 0
                || rand.nextDouble() > Config.TrashCan.updateChance
                || state.getValue(FILL) >= MAX_FILL
                || worldIn.isRemote)
            return;

        worldIn.setBlockState(pos, state.withProperty(FILL, state.getValue(FILL) + 1));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FILL, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FILL);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FILL);
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

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}

