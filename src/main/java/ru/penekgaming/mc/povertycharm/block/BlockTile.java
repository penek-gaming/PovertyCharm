package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariants;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariative;

@SuppressWarnings({"NullableProblems"})
public class BlockTile extends PovertyBlockVariative<BlockTile.Variants> implements IBlockVariative {
    private static final PropertyEnum<Variants> VARIANT = PropertyEnum.create("variant", Variants.class);

    public BlockTile() {
        super("tile", Material.CLAY, VARIANT);
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    public enum Variants implements IBlockVariants {
        DEFAULT(0, "default"),
        DEFAULT_CRACKED(1, "default_cracked"),
        GREEN(2, "green"),
        GREEN_CRACKED(3, "green_cracked"),
        WHITE(4, "white"),
        WHITE_CRACKED(5, "white_cracked"),
        WHITE_LINE_RED(6, "white_line_red"),
        WHITE_LINE_BLUE(7, "white_line_blue"),
        WHITE_LINE_GREEN(8, "white_line_green");

        private final int meta;
        private final String name;

        Variants(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        @Override
        public int getMetadata() {
            return meta;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
