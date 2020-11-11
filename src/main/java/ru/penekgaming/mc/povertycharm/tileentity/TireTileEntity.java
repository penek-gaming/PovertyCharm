package ru.penekgaming.mc.povertycharm.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import ru.penekgaming.mc.povertycharm.PovertyCharm;
import ru.penekgaming.mc.povertycharm.block.TireBlock;

@SuppressWarnings("NullableProblems")
public class TireTileEntity extends TileEntity {
    private EnumFacing facing;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger(TireBlock.FACING.getName(), facing.getIndex());

        return super.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        facing = EnumFacing.byIndex(tagCompound.getInteger(TireBlock.FACING.getName()));
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound compound = new NBTTagCompound();
        return writeToNBT(compound);
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        this.readFromNBT(tag);
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
        markDirty();
    }

    public EnumFacing getFacing() {
        return facing;
    }

    // This code may be used in future so not deleted
//    @Override
//    public SPacketUpdateTileEntity getUpdatePacket()
//    {
//        PovertyCharm.LOGGER.warn("UpdateEntitiiii NBT {}", facingIndex);
//        NBTTagCompound nbtTagCompound = new NBTTagCompound();
//        writeToNBT(nbtTagCompound);
//        return new SPacketUpdateTileEntity(pos, 0, nbtTagCompound);
//    }
//
//    @Override
//    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
//    {
//        readFromNBT(pkt.getNbtCompound());
//        PovertyCharm.LOGGER.warn("GOT DATA NBT {}", facingIndex);
//    }
}
