package ru.penekgaming.mc.povertycharm.init;

import net.minecraft.item.Item;
import ru.penekgaming.mc.povertycharm.item.ItemMouthpiece;
import ru.penekgaming.mc.povertycharm.item.ItemPoverty;

import java.util.ArrayList;
import java.util.List;

public class PovertyItems {
    public static final List<Item> ITEM_LIST = new ArrayList<>();

    public static final ItemPoverty MOUTHPIECE;

    static {
        MOUTHPIECE = new ItemMouthpiece();
    }
}
