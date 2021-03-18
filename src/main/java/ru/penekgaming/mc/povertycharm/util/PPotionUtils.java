package ru.penekgaming.mc.povertycharm.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class PPotionUtils {
    public static NBTTagCompound listToTag(List<PotionEffect> potionEffectList) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setInteger("size", potionEffectList.size());

        for (int i = 0; i < potionEffectList.size(); i++) {
            PotionEffect potionEffect = potionEffectList.get(i);
            NBTTagCompound potionTag = new NBTTagCompound();
            potionEffect.writeCustomPotionEffectToNBT(potionTag);

            tagCompound.setTag(String.format("effect_%d", i), potionTag);
        }

        return tagCompound;
    }

    public static List<PotionEffect> tagToList(NBTTagCompound tagCompound) {
        List<PotionEffect> list = new ArrayList<>();

        if (!tagCompound.hasKey("size"))
            return list;

        int size = tagCompound.getInteger("size");

        for (int i = 0; i < size; i++) {
            String effectKey = String.format("effect_%d", i);
            if (!tagCompound.hasKey(effectKey))
                continue;

            list.add(PotionEffect.readCustomPotionEffectFromNBT(tagCompound.getCompoundTag(effectKey)));
        }

        return list;
    }
}
