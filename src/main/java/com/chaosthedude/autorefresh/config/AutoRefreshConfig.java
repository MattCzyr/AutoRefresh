package com.chaosthedude.autorefresh.config;

import java.io.File;

import com.chaosthedude.autorefresh.AutoRefresh;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class AutoRefreshConfig {

	public static Configuration config;

	public static int delay = 10;

	public static void loadConfig(File configFile) {
		config = new Configuration(configFile);

		config.load();
		init();

		MinecraftForge.EVENT_BUS.register(new ChangeListener());
	}

	public static void init() {
		delay = loadInt("secondsUntilRefresh", delay);

		if (config.hasChanged()) {
			config.save();
		}
	}

	public static int loadInt(String name, int def) {
		final Property prop = config.get(Configuration.CATEGORY_GENERAL, name, def);
		int val = prop.getInt(def);
		if (val <= 0) {
			val = def;
			prop.set(def);
		}

		return val;
	}

	public static class ChangeListener {
		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
			if (eventArgs.modID.equals(AutoRefresh.MODID)) {
				init();
			}
		}
	}

}
