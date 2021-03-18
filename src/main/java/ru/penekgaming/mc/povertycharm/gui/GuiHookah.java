package ru.penekgaming.mc.povertycharm.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import ru.penekgaming.mc.povertycharm.PovertyCharm;
import ru.penekgaming.mc.povertycharm.container.hookah.ContainerHookah;
import ru.penekgaming.mc.povertycharm.init.PovertyBlocks;
import ru.penekgaming.mc.povertycharm.message.model.MessageHookahGui;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityHookah;

import java.io.IOException;
import java.util.List;

public class GuiHookah extends GuiContainer {
    public static final ResourceLocation TEXTURE = new ResourceLocation(PovertyCharm.MOD_ID, "textures/gui/hookah.png");
    private final InventoryPlayer inventoryPlayer;
    private final TileEntityHookah hookah;
    private int xOffs, yOffs;

    private static final int STATUS_X = 176, STATUS_Y = 32;

    public GuiHookah(Container container, InventoryPlayer inventoryPlayer) {
        super(container);

        this.inventoryPlayer = inventoryPlayer;
        this.hookah = ((ContainerHookah) container).hookah;

        ySize = 203;
    }

    @Override
    public void initGui() {
        super.initGui();
        xOffs = (width - xSize) / 2;
        yOffs = (height - ySize) / 2;
        addButton(new Button(this, 0, xOffs + xSize - 30, yOffs + 84));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        updateItemStatuses();
        updateIndicator();
        drawCaption();
    }

    private void drawCaption() {
        String name = I18n.format(PovertyBlocks.HOOKAH.getTranslationKey());

        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer.drawString(inventoryPlayer.getDisplayName().getUnformattedText(), 8, ySize - 94, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    private void updateIndicator() {
        if (!hookah.isActive())
            return;

        mc.getTextureManager().bindTexture(TEXTURE);

        int hRemain = (int) (14 * hookah.getChargesRemaining() / (float) hookah.getChargesMax());

        drawTexturedModalRect(146, 20 + 14 - hRemain, xSize, 18 + 14 - hRemain, 14, hRemain);

    }

    private void updateItemStatuses() {
        //GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(TEXTURE);

        List<Slot> coalSlots = inventorySlots.inventorySlots.subList(0, 3);
        List<Slot> potionSlots = inventorySlots.inventorySlots.subList(3, 6);
        Slot waterSlot = inventorySlots.inventorySlots.get(6);

        coalSlots.stream()
                .filter(slot -> !slot.getHasStack())
                .forEach(slot -> drawTexturedModalRect(slot.xPos - 1, slot.yPos - 1, 176, 0, 18, 18));

        potionSlots.stream()
                .filter(slot -> !slot.getHasStack())
                .forEach(slot -> drawTexturedModalRect(slot.xPos - 1, slot.yPos - 1, 194, 0, 18, 18));

        if (!waterSlot.getHasStack())
            drawTexturedModalRect(waterSlot.xPos - 1, waterSlot.yPos - 1, 212, 0, 18, 18);

        int coalsOffs = coalSlots.stream().allMatch(slot -> slot.isItemValid(slot.getStack()))
                || hookah.isActive() ? 0 : 14;
        int potionOffs = potionSlots.stream().anyMatch(slot -> slot.isItemValid(slot.getStack())) || hookah.isActive() ? 0 : 14;
        int waterOffs = waterSlot.isItemValid(waterSlot.getStack()) || hookah.isActive() ? 0 : 14;

        drawTexturedModalRect(69, 21, STATUS_X + coalsOffs, STATUS_Y, 14, 13);
        drawTexturedModalRect(69, 45, STATUS_X + potionOffs, STATUS_Y, 14, 13);
        drawTexturedModalRect(69, 87, STATUS_X + waterOffs, STATUS_Y, 14, 13);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(xOffs, yOffs, 0, 0, xSize, ySize);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        PovertyCharm.NETWORK_WRAPPER.sendToServer(new MessageHookahGui(button.id, hookah));
        if (hookah.isActive())
            hookah.reset();
        else
            hookah.activate();
    }

    public static class Button extends GuiButton {
        private final GuiHookah guiHookah;

        public Button(GuiHookah guiHookah, int buttonId, int x, int y) {
            super(buttonId, x, y, 20, 20, "");
            this.guiHookah = guiHookah;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            super.drawButton(mc, mouseX, mouseY, partialTicks);

            guiHookah.mc.getTextureManager().bindTexture(TEXTURE);
            int imgOffset = 0;

            if (guiHookah.hookah.isActive()) {
                imgOffset = 20;
            } else {
                enabled = guiHookah.hookah.canBeActivated();
            }

            drawTexturedModalRect(x, y, guiHookah.xSize + imgOffset, 45, 20, 20);
        }
    }
}
