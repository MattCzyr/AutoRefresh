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
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAutoRefresh extends GuiMultiplayer {

	private GuiScreen parentScreen;
	private GuiButton buttonAutoRefresh;
	private GuiButton buttonAutoJoin;
	private ServerSelectionList serverListSelector;
	private boolean autoRefreshEnabled;
	private boolean autoJoinEnabled;
	private int ticksSinceRefresh;
	private int selected;
	private int amountScrolled;
	private boolean hasAlerted;

	public GuiAutoRefresh(GuiScreen parentScreen, int selected, int amountScrolled, boolean autoJoin) {
		super(parentScreen);
		this.parentScreen = parentScreen;
		this.selected = selected;
		this.amountScrolled = amountScrolled;
		autoRefreshEnabled = true;
		autoJoinEnabled = autoJoin;
	}

	public GuiAutoRefresh(GuiScreen parentScreen) {
		this(parentScreen, -1, -1, false);
	}

	@Override
	public void initGui() {
		super.initGui();
		setupServerSelector();
		if (selected >= 0 && selected < getServerList().countServers()) {
			selectServer(selected);
		}

		if (serverListSelector != null && amountScrolled >= -1) {
			serverListSelector.scrollBy(amountScrolled);
		}
	}

	@Override
	public void createButtons() {
		super.createButtons();
		buttonAutoRefresh = addButton(new GuiButton(9, width - 125, 5, 120, 20, I18n.format("string.autoRefresh") + ": " + I18n.format("string.on")));
		buttonAutoJoin = addButton(new GuiButton(10, 5, 5, 120, 20, I18n.format("string.autoJoin") + ": " + (autoJoinEnabled ? I18n.format("string.on") : I18n.format("string.off"))));
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
		}

		if (serverListSelector != null) {
			final int index = serverListSelector.getSelected();
			if (index >= 0 && index < getServerList().countServers() && getServerList().getServerData(index).pingToServer > 0) {
				if (!hasAlerted) {
					mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
					hasAlerted = true;
				}

				if (autoJoinEnabled) {
					connectToSelected();
					toggleAutoJoin();
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == buttonAutoRefresh) {
			toggleAutoRefresh();
		} else if (button == buttonAutoJoin) {
			toggleAutoJoin();
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

	private void toggleAutoJoin() {
		autoJoinEnabled = !autoJoinEnabled;
		buttonAutoJoin.displayString = I18n.format("string.autoJoin") + ": " + (autoJoinEnabled ? I18n.format("string.on") : I18n.format("string.off"));
	}

	private void refresh() {
		if (serverListSelector != null) {
			mc.displayGuiScreen(new GuiAutoRefresh(parentScreen, serverListSelector.getSelected(), serverListSelector.getAmountScrolled(), autoJoinEnabled));
		} else {
			mc.displayGuiScreen(new GuiAutoRefresh(parentScreen));
		}
	}

	private void setupServerSelector() {
		try {
			final Field f = ReflectionHelper.findField(GuiMultiplayer.class, "serverListSelector", "field_146803_h");
			f.setAccessible(true);
			serverListSelector = (ServerSelectionList) f.get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
