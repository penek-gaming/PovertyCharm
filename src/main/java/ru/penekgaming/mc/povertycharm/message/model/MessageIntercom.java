package ru.penekgaming.mc.povertycharm.message.model;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.apache.commons.compress.utils.CharsetNames;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MessageIntercom implements IMessage {
    private String code;
    private int x, y, z;

    public MessageIntercom() {}

    public MessageIntercom(String code, BlockPos pos) {
        this.code = code;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        code = buf.readCharSequence(4, StandardCharsets.US_ASCII).toString();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeCharSequence(code.subSequence(0, 4), StandardCharsets.US_ASCII);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public String getCode() {
        return code;
    }

    public BlockPos getBlockPos() {
        return new BlockPos(x, y, z);
    }
}
