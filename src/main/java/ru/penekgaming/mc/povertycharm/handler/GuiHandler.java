package ru.penekgaming.mc.povertycharm.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import ru.penekgaming.mc.povertycharm.container.hookah.ContainerHookah;
import ru.penekgaming.mc.povertycharm.gui.GuiHookah;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityHookah;

import javax.annotation.Nullable;
import java.util.Objects;

public class GuiHandler implements IGuiHandler {
    @Nullable
    @Override
    public Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        Id enumId = Id.values()[id];

        switch (enumId) {
            case HOOKAH:
                return new ContainerHookah(player.inventory,
                        (TileEntityHookah) Objects.requireNonNull(world.getTileEntity(new BlockPos(x, y, z))));

            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        Id enumId = Id.values()[id];

        switch (enumId) {
            case HOOKAH:
                return new GuiHookah(getServerGuiElement(id, player, world, x, y, z), player.inventory);

            default:
                return null;
        }
    }

    public enum Id {
        HOOKAH(0),
        INTERCOM_HOME(1);

        private final int id;

        Id(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
