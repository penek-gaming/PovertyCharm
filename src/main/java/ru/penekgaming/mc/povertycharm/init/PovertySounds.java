package ru.penekgaming.mc.povertycharm.init;

import net.minecraft.util.SoundEvent;
import ru.penekgaming.mc.povertycharm.util.PovertySound;

import java.util.HashMap;

public class PovertySounds {
    public static final HashMap<String, SoundEvent> SOUNDS = new HashMap<>();

    public static final SoundEvent HATCH_CLOSE = new PovertySound("hatch_close");
    public static final SoundEvent HATCH_OPEN = new PovertySound("hatch_open");
    public static final SoundEvent INTERCOM_ACCEPT = new PovertySound("intercom_accept");
    public static final SoundEvent INTERCOM_BEEP = new PovertySound("intercom_beep");
    public static final SoundEvent INTERCOM_OPEN = new PovertySound("intercom_open");
    public static final SoundEvent INTERCOM_REJECT = new PovertySound("intercom_reject");
    public static final SoundEvent INTERCOM_RING = new PovertySound("intercom_ring");
    public static final SoundEvent COMP_AMBIENT = new PovertySound("comp_ambient");
    public static final SoundEvent COMP_CS16 = new PovertySound("comp_cs16");
    public static final SoundEvent COMP_DOTA = new PovertySound("comp_dota");
    public static final SoundEvent COMP_BSOD = new PovertySound("comp_bsod");
}
