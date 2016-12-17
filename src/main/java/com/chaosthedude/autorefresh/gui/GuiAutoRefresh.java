package com.chaosthedude.autorefresh.gui;

import java.lang.reflect.Field;

import com.chaosthedude.autorefresh.config.AutoRefreshConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ServerSelectionList;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiAutoRefresh extends GuiMultiplayer {

	private GuiScreen parentScreen;
	private GuiButton buttonAutoRefresh;
	private ServerSelectionList serverListSelector;
	private boolean autoRefreshEnabled;
	private int ticksSinceRefresh;
	private int selected;
	private boolean hasAlerted;

	public GuiAutoRefresh(GuiScreen parentScreen, int selected) {
		super(parentScreen);
		this.parentScreen = parentScreen;
		this.selected = selected;
		autoRefreshEnabled = true;
	}

	@Override
	public void initGui() {
		super.initGui();
		setupServerSelector();
		if (selected >= 0 && selected < func_146795_p().countServers()) {
			func_146790_a(selected);
		}
	}

	@Override
	public void func_146794_g() {
		super.func_146794_g();
		buttonAutoRefresh = new GuiButton(8, width - 105, 5, 100, 20, I18n.format("string.autoRefresh") + ": " + I18n.format("string.on"));
		buttonList.add(buttonAutoRefresh);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (autoRefreshEnabled) {
			if (ticksSinceRefresh >= AutoRefreshConfig.delay * 20) {
				refresh();
				ticksSinceRefresh = 0;
			} else {
				ticksSinceRefresh++;
			}

			if (!hasAlerted && serverListSelector != null) {
				final int index = serverListSelector.func_148193_k();
				if (index >= 0 && index < func_146795_p().countServers() && func_146795_p().getServerData(index).pingToServer > 0) {
					mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("random.orb"), 1.0F));
					hasAlerted = true;
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button == buttonAutoRefresh) {
			toggleAutoRefresh();
		} else if (button.id == 8) {
			refresh();
		} else {
			super.actionPerformed(button);
		}
	}

	private void toggleAutoRefresh() {
		autoRefreshEnabled = !autoRefreshEnabled;
		buttonAutoRefresh.displayString = I18n.format("string.autoRefresh") + ": " + (autoRefreshEnabled ? I18n.format("string.on") : I18n.format("string.off"));
	}

	private void refresh() {
		mc.displayGuiScreen(new GuiAutoRefresh(parentScreen, serverListSelector != null ? serverListSelector.func_148193_k() : -1));
	}

	private void setupServerSelector() {
		try {
			final Field f = getClass().getSuperclass().getDeclaredField("field_146803_h");
			f.setAccessible(true);
			serverListSelector = (ServerSelectionList) f.get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
