package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariants;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariative;

public class ConcreteBlock extends PovertyBlockVariative<ConcreteBlock.Variants> implements IBlockVariative {
    private static final PropertyEnum<Variants> VARIANT
            = PropertyEnum.create("variant", Variants.class);

    protected ConcreteBlock() {
        super("concrete", Material.ROCK, VARIANT);
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    public enum Variants implements IBlockVariants {
        GRAY(0, "gray"),
        GRAY_CRACKED(1, "gray_cracked"),

        WHITE(2, "white"),
        WHITE_CRACKED(3, "white_cracked"),

        GREEN(4, "green"),
        GREEN_CRACKED(5, "green_cracked");

        private final int meta;
        private final String name;

        Variants(int meta, String name) {
            this.meta = meta;
            this.name = name;
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
