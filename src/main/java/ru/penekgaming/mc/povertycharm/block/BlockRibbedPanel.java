package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import ru.penekgaming.mc.povertycharm.util.AxisAlignedBBContainer;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class BlockRibbedPanel extends BlockRotatable {
    public static final AxisAlignedBBContainer BBS = AxisAlignedBBContainer.builder()
            .set(EnumFacing.NORTH, new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 3 / 16.0))
            .set(EnumFacing.EAST, new AxisAlignedBB(1 - 3 / 16.0, 0.0, 0.0, 1.0, 1.0, 1.0))
            .set(EnumFacing.SOUTH, new AxisAlignedBB(0.0, 0.0, 1 - 3 / 16.0, 1.0, 1.0, 1.0))
            .set(EnumFacing.WEST, new AxisAlignedBB(0.0, 0.0, 0.0, 3 / 16.0, 1.0, 1.0))
            .build();

    public BlockRibbedPanel() {
        super("ribbed_panel", Material.IRON);
        setNoPlaceCollision(true);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BBS.get(state.getValue(FACING));
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

}