package ru.penekgaming.mc.povertycharm.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import ru.penekgaming.mc.povertycharm.block.BlockConcreteDamaged;

@SuppressWarnings("NullableProblems")
public class TileEntityConcreteDamaged extends TileEntity {
    private EnumFacing facing;
    private int innerIndex;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger(BlockConcreteDamaged.FACING.getName(), facing.getIndex());
        tagCompound.setInteger("inner", innerIndex);

        return super.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        facing = EnumFacing.byIndex(tagCompound.getInteger(BlockConcreteDamaged.FACING.getName()));
        innerIndex = tagCompound.getInteger("inner");
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        return writeToNBT(compound);
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        writeToNBT(nbtTagCompound);
        return new SPacketUpdateTileEntity(pos, 0, nbtTagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    public TileEntityConcreteDamaged setFacing(EnumFacing facing) {
        this.facing = facing;
        markDirty();
        return this;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public TileEntityConcreteDamaged setInnerIndex(int index) {
        this.innerIndex = index;
        markDirty();
        return this;
    }

    public int getInnerIndex() {
        return innerIndex;
    }
}
