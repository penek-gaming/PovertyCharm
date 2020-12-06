package ru.penekgaming.mc.povertycharm.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import ru.penekgaming.mc.povertycharm.PovertyCharm;
import ru.penekgaming.mc.povertycharm.init.PovertySounds;
import ru.penekgaming.mc.povertycharm.message.model.MessageIntercom;
import ru.penekgaming.mc.povertycharm.tileentity.TileEntityIntercom;

import java.awt.*;
import java.io.IOException;
import java.security.Key;


public class GuiIntercom extends GuiScreen {
    private final static int I_WIDTH = 256;
    private final static int I_HEIGHT = 128;

    private final BlockPos pos;

    private String code = "";
    private int xOffset;
    private int yOffset;

    public GuiIntercom(BlockPos pos) {
        super();
        this.pos = pos;
    }

    @Override
    public void initGui() {
        calcOffset();
        buttonList.clear();

        for(int i = 0, id = 0, number = 0; i < 4; i++)
            for (int j = 0; j < 3; j++, id++) {
                IntercomButton intercomButton = new IntercomButton(id, xOffset + 132 + 22 * j, yOffset + 40 + 18 * i);

                if (id != 9 && id != 11)
                    intercomButton.setNumber(++number % 10);

                buttonList.add(intercomButton);
            }

    }

    private void calcOffset() {
        xOffset = (width - I_WIDTH) / 2;
        yOffset = (height - I_HEIGHT) / 2;
    }

    @Override
    public void updateScreen() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ResourceLocation resourceLocation = new ResourceLocation("povertycharm:textures/gui/intercom.png");
        mc.getTextureManager().bindTexture(resourceLocation);

        calcOffset();

        drawTexturedModalRect(xOffset, yOffset, 0, 0, I_WIDTH, I_HEIGHT);

        updCodeScreen();

        super.drawScreen(mouseX, mouseY, partialTicks);

    }

    private void updCodeScreen() {
        int screenOffsetX = xOffset + 49;
        int screenOffsetY = yOffset + 41;

        for(char c : code.toCharArray()) {
            try {
                int letterNum = Character.getNumericValue(c);

                drawTexturedModalRect(screenOffsetX, screenOffsetY, 10 * letterNum, I_HEIGHT, 10, 14);

                screenOffsetX += 12;
            } catch (Exception ignored) {}

        }
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();

        if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
            code = code.length() == 4 ? code : "ESCD";
            sendCode();

            return;
        }

        if(!Keyboard.getEventKeyState())
            return;

        if(Keyboard.KEY_1 <= Keyboard.getEventKey() && Keyboard.getEventKey() <= Keyboard.KEY_0) {
            pressButton(Keyboard.getEventKey() - 1, 0);
        }

    }


    private void sendCode() {
        PovertyCharm.NETWORK_WRAPPER.sendToServer(new MessageIntercom(code.length() == 4 ? code : TileEntityIntercom.DEFAULT_CODE, pos));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        IntercomButton intercomButton = (IntercomButton) button;

        pressButton(intercomButton.number, button.id);
    }

    private void pressButton(int num, int id) {
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(PovertySounds.INTERCOM_BEEP, 1.0f));

        if(num != -1) {
            enterNumber(num);
            return;
        }

        switch (id) {
            case 9:
                code = "";
                break;

            case 11:
                sendCode();
                mc.player.closeScreen();
                break;
        }
    }

    private void enterNumber(int num) {
        if(code.length() >= 4) {
            code = code.substring(1);
        }

        code += String.valueOf(num);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    private static class IntercomButton extends GuiButton {
        private int number = -1;

        public IntercomButton(int buttonId, int x, int y) {
            super(buttonId, x, y, "");

            height = 13;
            width = 13;
        }

        @Override
        public void playPressSound(SoundHandler soundHandlerIn) {

        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {

        }
    }
}
