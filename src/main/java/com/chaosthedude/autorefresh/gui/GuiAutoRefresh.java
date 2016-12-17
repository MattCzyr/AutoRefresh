package com.chaosthedude.autorefresh.gui;

import java.io.IOException;
import java.lang.reflect.Field;

import com.chaosthedude.autorefresh.config.AutoRefreshConfig;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ServerSelectionList;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAutoRefresh extends GuiMultiplayer {

	private GuiScreen parentScreen;
	private GuiButton buttonAutoRefresh;
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
		if (selected >= 0 && selected < getServerList().countServers()) {
			selectServer(selected);
		}
	}

	@Override
	public void createButtons() {
		super.createButtons();
		buttonAutoRefresh = addButton(new GuiButton(8, width - 105, 5, 100, 20, I18n.format("string.autoRefresh") + ": " + I18n.format("string.on")));
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

			if (!hasAlerted) {
				final int index = getServerSelector().getSelected();
				if (index >= 0 && index < getServerList().countServers() && getServerList().getServerData(index).pingToServer > 0) {
					mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
					hasAlerted = true;
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == buttonAutoRefresh) {
			toggleAutoRefresh();
		} else {
			super.actionPerformed(button);
		}
	}

	private void toggleAutoRefresh() {
		autoRefreshEnabled = !autoRefreshEnabled;
		buttonAutoRefresh.displayString = I18n.format("string.autoRefresh") + ": " + (autoRefreshEnabled ? I18n.format("string.on") : I18n.format("string.off"));
	}

	private void refresh() {
		final ServerSelectionList serverSelector = getServerSelector();
		if (serverSelector != null) {
			mc.displayGuiScreen(new GuiAutoRefresh(parentScreen, serverSelector != null ? serverSelector.getSelected() : -1));
		}
	}

	private ServerSelectionList getServerSelector() {
		try {
			final Field f = getClass().getSuperclass().getDeclaredField("serverListSelector");
			f.setAccessible(true);
			return (ServerSelectionList) f.get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
