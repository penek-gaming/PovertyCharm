package ru.penekgaming.mc.povertycharm.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import ru.penekgaming.mc.povertycharm.PovertyCharm;
import ru.penekgaming.mc.povertycharm.init.PovertySounds;

public class PovertySound extends SoundEvent {
    public PovertySound(String name) {
        this(new ResourceLocation(PovertyCharm.MOD_ID, name));
    }

    private PovertySound(ResourceLocation location) {
        super(location);
        setRegistryName(location);
        PovertySounds.SOUNDS.put(location.toString(), this);
    }
}
