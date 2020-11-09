package ru.penekgaming.mc.povertycharm.block;

import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.init.Blocks;
import ru.penekgaming.mc.povertycharm.block.variant.ConcreteVariants;
import ru.penekgaming.mc.povertycharm.block.variant.IBlockVariative;
import ru.penekgaming.mc.povertycharm.item.PovertyItemBlockVariative;
import scala.sys.Prop;

import java.util.Objects;

public class ConcreteBlock extends PovertyBlockVariative<ConcreteVariants> implements IBlockVariative {
    private static final PropertyEnum<ConcreteVariants> VARIANT
            = PropertyEnum.create("variant", ConcreteVariants.class);

    protected ConcreteBlock() {
        super("concrete", Material.ROCK, VARIANT);
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }
}
