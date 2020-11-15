package ru.penekgaming.mc.povertycharm.util;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class AxisAlignedBBContainer {
    private final AxisAlignedBB[] BBS;

    private AxisAlignedBBContainer(AxisAlignedBB[] bbs) {
        BBS = bbs;
    }

    public AxisAlignedBB get(EnumFacing facing) {
        AxisAlignedBB bb = BBS[facing.getIndex()];
        return bb == null ? Block.FULL_BLOCK_AABB : bb;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AxisAlignedBB[] BBS;

        private Builder() {
            BBS = new AxisAlignedBB[EnumFacing.VALUES.length];
        }

        public Builder set(EnumFacing facing, AxisAlignedBB bb) {
            BBS[facing.getIndex()] = bb;
            return this;
        }

        public AxisAlignedBBContainer build() {
            return new AxisAlignedBBContainer(BBS);
        }
    }
}
