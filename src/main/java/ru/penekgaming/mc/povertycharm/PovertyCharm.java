package ru.penekgaming.mc.povertycharm;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.penekgaming.mc.povertycharm.block.PovertyBlocks;
import ru.penekgaming.mc.povertycharm.proxy.CommonProxy;

@Mod(
        modid = PovertyCharm.MOD_ID,
        name = PovertyCharm.MOD_NAME,
        version = PovertyCharm.VERSION
)
public class PovertyCharm {
    public static final String MOD_ID = "povertycharm";
    public static final String MOD_NAME = "PovertyCharm";
    public static final String VERSION = "1.0-SNAPSHOT";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MOD_ID + ".main") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(PovertyBlocks.PIPE_VALVE);
        }
    };

    @SidedProxy(
            clientSide = "ru.penekgaming.mc.povertycharm.proxy.ClientProxy",
            serverSide = "ru.penekgaming.mc.povertycharm.proxy.ServerProxy"
    )
    public static CommonProxy proxy;

    @Mod.Instance(MOD_ID)
    public static PovertyCharm INSTANCE;

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
