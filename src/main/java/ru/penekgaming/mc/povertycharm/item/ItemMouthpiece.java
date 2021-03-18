package ru.penekgaming.mc.povertycharm.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import ru.penekgaming.mc.povertycharm.block.BlockHookah;

public class ItemMouthpiece extends ItemPoverty {
    public static final int MAX_TIME = 40;

    public ItemMouthpiece() {
        super("mouthpiece");
        setMaxStackSize(1);
        setMaxDamage(10);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
//        if(worldIn.isRemote)
//            return stack;

        setDamage(stack, 10);
        return stack;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (/*worldIn.isRemote || */!stack.isItemDamaged())
            return;

        stack.setItemDamage(stack.getItemDamage() - 1);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return MAX_TIME;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!(worldIn.getBlockState(pos).getBlock() instanceof BlockHookah))
            return EnumActionResult.FAIL;

        MinecraftForge.EVENT_BUS.post(new FOVUpdateEvent(player, 1.0F));

        player.setActiveHand(hand);

        return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    //    @Override
//    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
//        ItemStack itemstack = playerIn.getHeldItem(handIn);
//
//        MinecraftForge.EVENT_BUS.post(new FOVUpdateEvent(playerIn, 1.0F));
//
//        playerIn.setActiveHand(handIn);
//        return new ActionResult<>(EnumActionResult.FAIL, itemstack);
//    }
}
