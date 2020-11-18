package ru.penekgaming.mc.povertycharm.init;

import net.minecraft.util.SoundEvent;
import ru.penekgaming.mc.povertycharm.util.PovertySound;

import java.util.HashMap;

public class PovertySounds {
    public static final HashMap<String, SoundEvent> SOUNDS = new HashMap<>();

    public static final SoundEvent HATCH_CLOSE = new PovertySound("hatch_close");
    public static final SoundEvent HATCH_OPEN = new PovertySound("hatch_open");
}
