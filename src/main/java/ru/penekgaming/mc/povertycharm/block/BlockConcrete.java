package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariants;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariative;

@SuppressWarnings({"NullableProblems"})
public class BlockConcrete extends PovertyBlockVariative<BlockConcrete.Variant> implements IBlockVariative {
    public static final PropertyEnum<Variant> VARIANT
            = PropertyEnum.create("variant", Variant.class);

    public BlockConcrete(String name) {
        super(name, Material.ROCK, VARIANT);
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    public enum Variant implements IBlockVariants {
        GRAY(0, "gray"),
        GRAY_CRACKED(1, "gray_cracked"),

        WHITE(2, "white"),
        WHITE_CRACKED(3, "white_cracked"),

        GREEN(4, "green"),
        GREEN_CRACKED(5, "green_cracked");

        private final int meta;
        private final String name;

        Variant(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int getMetadata() {
            return meta;
        }
    }
}
