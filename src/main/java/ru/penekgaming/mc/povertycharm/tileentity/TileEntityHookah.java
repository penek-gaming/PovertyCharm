package ru.penekgaming.mc.povertycharm.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import ru.penekgaming.mc.povertycharm.Config;
import ru.penekgaming.mc.povertycharm.block.BlockHookah;
import ru.penekgaming.mc.povertycharm.container.hookah.SlotItemHandlerCoal;
import ru.penekgaming.mc.povertycharm.container.hookah.SlotItemHandlerPotion;
import ru.penekgaming.mc.povertycharm.container.hookah.SlotItemHandlerWater;
import ru.penekgaming.mc.povertycharm.util.PPotionUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileEntityHookah extends TileEntity implements ITickable {
    private final ItemStackHandler inventory = new ItemStackHandler(7);
    private int chargesRemain = 0, chargesMax;
    private List<PotionEffect> effectList = new ArrayList<>();
    private int ticksPassed;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("inventory", inventory.serializeNBT());
        compound.setInteger("maxCharges", chargesMax);
        compound.setInteger("charges", chargesRemain);
        compound.setTag("effects", PPotionUtils.listToTag(effectList));

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        chargesMax = compound.getInteger("maxCharges");
        chargesRemain = compound.getInteger("charges");
        effectList = PPotionUtils.tagToList(compound.getCompoundTag("effects"));

        super.readFromNBT(compound);
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
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        writeToNBT(nbtTagCompound);
        return new SPacketUpdateTileEntity(pos, 0, nbtTagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) inventory : super.getCapability(capability, facing);
    }

    public boolean canBeActivated() {
        if (isActive())
            return false;

        boolean coalValid = true;
        boolean potionValid = false;

        for (int i = 0; i < 2; i++) {
            if (!SlotItemHandlerCoal.validateStack(inventory.getStackInSlot(i))) {
                coalValid = false;
                break;
            }
        }

        for (int i = 3; i < 5; i++) {
            if (SlotItemHandlerPotion.validateStack(inventory.getStackInSlot(i))) {
                potionValid = true;
                break;
            }
        }

        boolean waterValid = SlotItemHandlerWater.validateStack(inventory.getStackInSlot(6));

        return coalValid && potionValid && waterValid;
    }

    public boolean activate() {
        if (!canBeActivated())
            return false;

        chargesMax = Config.Hookah.charges;
        chargesRemain = chargesMax;
        effectList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }

        for (int i = 3; i < 6; i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            List<PotionEffect> effectsFromStack = PotionUtils.getEffectsFromStack(slot);

            if (slot.isEmpty() || effectsFromStack.size() <= 0)
                continue;

            effectList.addAll(effectsFromStack);
            inventory.setStackInSlot(i, new ItemStack(Items.GLASS_BOTTLE));
        }

        Item waterItem = inventory.getStackInSlot(6).getItem();
        if (waterItem == Items.WATER_BUCKET) {
            inventory.setStackInSlot(6, new ItemStack(Items.BUCKET));
        } else if (waterItem instanceof UniversalBucket) {
            UniversalBucket universalBucket = (UniversalBucket) waterItem;

            FluidStack fluid = universalBucket.getFluid(inventory.getStackInSlot(6));

            if (fluid == null)
                inventory.setStackInSlot(6, ItemStack.EMPTY);
            else {
                fluid.amount -= 1000;
                inventory.setStackInSlot(6, new ItemStack(universalBucket));
            }
        }

        markDirty();
        notifyUpdate();

        return true;
    }

    public void notifyUpdate() {
        IBlockState blockState = world.getBlockState(pos);
        world.notifyBlockUpdate(
                pos,
                blockState,
                blockState
                        .withProperty(BlockHookah.ACTIVE, isActive())
                        .withProperty(BlockHookah.COALS, getCoalCount()),
                Constants.BlockFlags.DEFAULT
        );
    }

    public int getCoalCount() {
        int count = 0;

        for (int i = 0; i < 3; i++) {
            if (SlotItemHandlerCoal.validateStack(inventory.getStackInSlot(i))) {
                count++;
            }
        }

        return count;
    }

    public void reset() {
        chargesRemain = 0;
        markDirty();
        notifyUpdate();
    }

    public boolean isActive() {
        return chargesRemain > 0;
    }

    public int getChargesRemaining() {
        return chargesRemain;
    }

    public int getChargesMax() {
        return chargesMax;
    }

    public List<PotionEffect> getEffectList() {
        return Collections.unmodifiableList(effectList);
    }

    public void useCharge() {
        chargesRemain--;
        markDirty();
        notifyUpdate();
    }

    @Override
    public void update() {
        int loseTicks = Config.Hookah.chargeLoseTicks;
        if (loseTicks <= 0 || !isActive())
            return;

        if (ticksPassed++ > loseTicks) {
            useCharge();
            ticksPassed = 0;
        }
    }
}
