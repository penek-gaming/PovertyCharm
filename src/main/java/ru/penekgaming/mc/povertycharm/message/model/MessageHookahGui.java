package ru.penekgaming.mc.povertycharm.message.model;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityHookah;

public class MessageHookahGui implements IMessage {
    private int buttonId;
    private int x, y, z;

    public MessageHookahGui() {
    }

    public MessageHookahGui(int buttonId, TileEntityHookah hookah) {
        this.buttonId = buttonId;
        x = hookah.getPos().getX();
        y = hookah.getPos().getY();
        z = hookah.getPos().getZ();
    }

    public TileEntityHookah getHookah(World world) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        return tileEntity instanceof TileEntityHookah ? (TileEntityHookah) tileEntity : null;
    }

    public int getButtonId() {
        return buttonId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        buttonId = buf.readInt();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(buttonId);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }
}
