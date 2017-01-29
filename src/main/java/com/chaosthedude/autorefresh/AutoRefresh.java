package com.chaosthedude.autorefresh;

import com.chaosthedude.autorefresh.config.AutoRefreshConfig;
import com.chaosthedude.autorefresh.gui.GuiAutoRefresh;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = AutoRefresh.MODID, name = AutoRefresh.NAME, version = AutoRefresh.VERSION, acceptedMinecraftVersions = "[1.11,1.11.2]")

public class AutoRefresh {

	public static final String MODID = "autorefresh";
	public static final String NAME = "Auto Refresh";
	public static final String VERSION = "1.1.0";

	@EventHandler
	public void init(FMLPreInitializationEvent event) {
		AutoRefreshConfig.loadConfig(event.getSuggestedConfigurationFile());
		AutoRefreshConfig.init();

		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void onGuiOpen(GuiOpenEvent event) {
		if (event.getGui() != null && event.getGui().getClass() == GuiMultiplayer.class) {
			event.setGui(new GuiAutoRefresh(Minecraft.getMinecraft().currentScreen));
		}
	}

}
