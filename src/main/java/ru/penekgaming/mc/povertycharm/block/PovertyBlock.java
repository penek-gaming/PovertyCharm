package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.penekgaming.mc.povertycharm.PovertyCharm;
import ru.penekgaming.mc.povertycharm.init.PovertyBlocks;
import ru.penekgaming.mc.povertycharm.item.ItemPovertyBlock;

import java.util.*;

public class PovertyBlock extends Block {
    public Item item;
    private boolean noPlaceCollision = false;
    protected boolean useCustomStateMapper = false;
    private final List<IProperty<?>> ignoredProperties = new ArrayList<>();

    public PovertyBlock(String name, Material material, float hardness) {
        this(name, material, true, hardness);
    }

    public PovertyBlock(String name, Material material) {
        this(name, material, true, 0.5f);
    }

    protected PovertyBlock(String name, Material material, boolean autoRegister, float hardness) {
        super(material);
        setRegistryName(PovertyCharm.MOD_ID, name);
        setTranslationKey(String.format("%s.%s", PovertyCharm.MOD_ID, name));
        setCreativeTab(PovertyCharm.CREATIVE_TAB);
        item = new ItemPovertyBlock(this);
        setHardness(hardness);

        if (autoRegister)
            PovertyBlocks.BLOCKS.add(this);
    }

    public boolean canStayOn(IBlockAccess world, BlockPos sourcePos, EnumFacing sourceFacing, BlockFaceShape targetShape) {
        return canStayOn(world, sourcePos, sourceFacing, new BlockFaceShape[]{targetShape});
    }

    public boolean canStayOn(IBlockAccess world, BlockPos sourcePos, EnumFacing sourceFacing, BlockFaceShape[] targetShapes) {
        BlockPos targetPos = sourcePos.offset(sourceFacing);
        IBlockState targetState = world.getBlockState(targetPos);
        BlockFaceShape targetShape =
                targetState.getBlock().getBlockFaceShape(world, targetState, targetPos, sourceFacing.getOpposite());

        return Arrays.asList(targetShapes).contains(targetShape);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes", "NullableProblems"})
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, net.minecraft.item.EnumDyeColor color) {
        IBlockState state = world.getBlockState(pos).getActualState(world, pos);

        for (IProperty prop : state.getProperties().keySet()) {
            if (prop.getName().equals("color") && prop.getValueClass() == net.minecraft.item.EnumDyeColor.class) {
                net.minecraft.item.EnumDyeColor current = (net.minecraft.item.EnumDyeColor) state.getValue(prop);
                if (current != color && prop.getAllowedValues().contains(color)) {
                    world.setBlockState(pos, state.withProperty(prop, color));
                    return true;
                }
            }
        }
        return false;
    }

    public static HashMap<EnumFacing, Block> getFacingBlocksHorizontal(IBlockAccess world, BlockPos pos) {
        HashMap<EnumFacing, Block> facings = new HashMap<>();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos other = pos.offset(facing);
            Block block = world.getBlockState(other).getBlock();
            if (block != Blocks.AIR)
                facings.put(facing, block);
        }

        return facings;
    }

    public static HashMap<EnumFacing, IBlockState> getFacingBlocks(IBlockAccess world, BlockPos pos) {
        HashMap<EnumFacing, IBlockState> facingBlocks = new HashMap<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos other = pos.offset(facing);
            facingBlocks.put(facing, world.getBlockState(other));
        }

        return facingBlocks;
    }

    public static boolean oppositeBocksAreEqual(EnumFacing facing, HashMap<EnumFacing, Block> facings) {
        return facings.size() >= 2
                && facings.containsKey(facing.getOpposite())
                && facings.containsKey(facing)
                && facings.get(facing).getClass() == facings.get(facing.getOpposite()).getClass();
    }

    public static boolean oppositeBockIsEqual(EnumFacing facing, HashMap<EnumFacing, Block> facings, Class<?> clazz) {
        //PovertyCharm.LOGGER.warn("[{}:{}] [{}]", facing, facings.get(facing.getOpposite()).getClass(), clazz);
        return facings.size() >= 2 && facings.containsKey(facing.getOpposite()) && facings.get(facing.getOpposite()).getClass().equals(clazz);
    }

    public boolean isReplaceable(World world, BlockPos pos, Block block) {
        return isReplaceable(world, pos);
    }

    public boolean isNoPlaceCollision() {
        return noPlaceCollision;
    }

    public void setNoPlaceCollision(boolean noPlaceCollision) {
        this.noPlaceCollision = noPlaceCollision;
    }

    public String getStatePath() {
        return Objects.requireNonNull(getRegistryName()).getPath();
    }

    public boolean usesCustomStateMapper() {
        return useCustomStateMapper;
    }

    protected void addIgnoredProperties(IProperty<?>... props) {
        Collections.addAll(ignoredProperties, props);
    }

    public List<IProperty<?>> getIgnoredProperties() {
        return ignoredProperties;
    }
}
