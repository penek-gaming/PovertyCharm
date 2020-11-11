package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import ru.penekgaming.mc.povertycharm.PovertyCharm;

import java.util.HashMap;
import java.util.Objects;

public class PovertyBlock extends Block {
    public Item item;

    protected PovertyBlock(String name, Material material) {
        super(material);
        SetupBlock(name, true);
    }

    protected PovertyBlock(String name, Material material, boolean autoRegister) {
        super(material);
        SetupBlock(name, autoRegister);
    }

    private void SetupBlock(String name, boolean autoRegister) {
        setRegistryName(PovertyCharm.MOD_ID, name);
        setTranslationKey(String.format("%s.%s", PovertyCharm.MOD_ID, name));
        setCreativeTab(PovertyCharm.CREATIVE_TAB);
        item = new ItemBlock(this).setRegistryName(Objects.requireNonNull(getRegistryName()));

        if (autoRegister)
            PovertyBlocks.BLOCKS.put(name, this);
    }

    public static HashMap<EnumFacing, Block> getFacingBlocks(IBlockAccess world, BlockPos pos) {
        HashMap<EnumFacing, Block> facings = new HashMap<>();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos other = pos.offset(facing);
            Block block = world.getBlockState(other).getBlock();
            if (block != Blocks.AIR)
                facings.put(facing, block);
        }

        return facings;
    }

    public static boolean oppositeBocksAreEqual(EnumFacing facing, HashMap<EnumFacing, Block> facings) {
        return facings.size() >= 2
                && facings.containsKey(facing.getOpposite())
                && facings.containsKey(facing)
                && facings.get(facing).getClass() == facings.get(facing.getOpposite()).getClass();
    }

    public static boolean oppositeBockIsEqual(EnumFacing facing, HashMap<EnumFacing, Block> facings, Class<?> clazz) {
        PovertyCharm.LOGGER.warn("[{}:{}] [{}]", facing, facings.get(facing.getOpposite()).getClass(), clazz);
        return facings.size() >= 2 && facings.containsKey(facing.getOpposite()) && facings.get(facing.getOpposite()).getClass().equals(clazz);
    }
}
