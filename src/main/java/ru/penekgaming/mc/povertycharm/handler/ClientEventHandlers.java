package ru.penekgaming.mc.povertycharm.handler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.penekgaming.mc.povertycharm.item.ItemMouthpiece;

public class ClientEventHandlers {
    @SubscribeEvent
    public static void onFovChange(FOVUpdateEvent event) {
        ItemStack itemStack = event.getEntity().getActiveItemStack();
        if (!(itemStack.getItem() instanceof ItemMouthpiece))
            return;

        int itemInUseCount = event.getEntity().getItemInUseCount();
        float newFov = (1.0f - itemInUseCount / (float) itemStack.getMaxItemUseDuration()) * 0.25f;

        event.setNewfov(event.getFov() - newFov);

        //PovertyCharm.LOGGER.warn("{} / {} = {} :::: {}", itemInUseCount, itemStack.getMaxItemUseDuration(), newFov, event.getNewfov());
    }
}
