package ru.penekgaming.mc.povertycharm.tileentity;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ru.penekgaming.mc.povertycharm.block.PovertyBlock;

@SuppressWarnings({"NullableProblems"})
public abstract class TileEntityBlock<T extends TileEntity> extends PovertyBlock {
    private final Class<T> clazz;

    protected TileEntityBlock(String name, Material material, Class<T> clazz) {
        super(name, material);
        this.clazz = clazz;
    }

    public Class<T> getTileEntityClass() {
        return clazz;
    }

    @SuppressWarnings("unchecked")
    public T getTileEntity(IBlockAccess world, BlockPos position) {
        return (T) world.getTileEntity(position);
    }

    @Override
    public boolean hasTileEntity(IBlockState blockState) {
        return true;
    }

    @Override
    public abstract T createTileEntity(World world, IBlockState blockState);
}
