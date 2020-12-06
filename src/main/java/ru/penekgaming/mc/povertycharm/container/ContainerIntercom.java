package ru.penekgaming.mc.povertycharm.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityIntercom;

public class ContainerIntercom extends Container {
    private final TileEntityIntercom intercom;

    public ContainerIntercom(TileEntityIntercom intercom) {
        this.intercom = intercom;
    }

    // TODO add interact limitations
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !intercom.isInvalid();
    }
}
