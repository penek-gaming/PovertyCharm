package ru.penekgaming.mc.povertycharm.item;

import net.minecraft.item.Item;
import ru.penekgaming.mc.povertycharm.PovertyCharm;
import ru.penekgaming.mc.povertycharm.init.PovertyItems;

public class ItemPoverty extends Item {
    public ItemPoverty(String name) {
        setRegistryName(PovertyCharm.MOD_ID, name);
        setTranslationKey(String.format("%s.%s", PovertyCharm.MOD_ID, name));
        setCreativeTab(PovertyCharm.CREATIVE_TAB);

        PovertyItems.ITEM_LIST.add(this);
    }
}
