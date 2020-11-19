package ru.penekgaming.mc.povertycharm.util;

import com.google.common.collect.Maps;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.penekgaming.mc.povertycharm.block.PovertyBlock;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class PovertyStateMap extends StateMapperBase {
    @Override
    @Nonnull
    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        Map<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
        PovertyBlock block = (PovertyBlock) state.getBlock();

        String s = String.format("%s:%s", Objects.requireNonNull(block.getRegistryName()).getNamespace(), block.getStatePath());

        for (IProperty<?> property : block.getIgnoredProperties()) {
            map.remove(property);
        }

        return new ModelResourceLocation(s, getPropertyString(map));
    }
}
