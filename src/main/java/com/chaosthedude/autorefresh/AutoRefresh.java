package com.chaosthedude.autorefresh;

import com.chaosthedude.autorefresh.config.AutoRefreshConfig;
import com.chaosthedude.autorefresh.gui.GuiAutoRefresh;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = AutoRefresh.MODID, name = AutoRefresh.NAME, version = AutoRefresh.VERSION, acceptedMinecraftVersions = "[1.7.10]")

public class AutoRefresh {

	public static final String MODID = "autorefresh";
	public static final String NAME = "Auto Refresh";
	public static final String VERSION = "1.0.0";

	@EventHandler
	public void init(FMLPreInitializationEvent event) {
		AutoRefreshConfig.loadConfig(event.getSuggestedConfigurationFile());
		AutoRefreshConfig.init();

		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void onGuiOpen(GuiOpenEvent event) {
		if (event.gui != null && event.gui.getClass() == GuiMultiplayer.class) {
			event.gui = new GuiAutoRefresh(Minecraft.getMinecraft().currentScreen);
		}
	}

}
