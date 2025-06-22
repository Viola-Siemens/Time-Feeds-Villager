package com.hexagram2021.time_feeds_villager.client.screen;

import com.hexagram2021.time_feeds_villager.entity.ISwitchableEntity;
import com.hexagram2021.time_feeds_villager.menu.VillagerExtraInventoryMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

import static com.hexagram2021.time_feeds_villager.TimeFeedsVillager.MODID;

public class VillagerExtraInventoryScreen extends AbstractContainerScreen<VillagerExtraInventoryMenu> {
	private static final ResourceLocation LOCATION = new ResourceLocation(MODID, "textures/gui/container/villager.png");
	private final Villager villager;

	public VillagerExtraInventoryScreen(VillagerExtraInventoryMenu menu, Inventory playerInventory, Villager villager) {
		super(menu, playerInventory, villager.getDisplayName());
		this.villager = villager;
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partialTick, int mouseX, int mouseY) {
		transform.blit(LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		this.renderButtons(transform, mouseX, mouseY);
		InventoryScreen.renderEntityInInventoryFollowsMouse(transform, this.leftPos + 51, this.topPos + 68, 24, this.leftPos + 51 - mouseX, this.topPos + 25 - mouseY, this.villager);
	}

	private int clicked = -1;
	private void renderButtons(GuiGraphics transform, int mouseX, int mouseY) {
		// closet: 0
		this.renderButton(transform, mouseX, mouseY, 68, 60, 0, 0, 10);
		// mode: 1
		int modeButtonU = 20;
		if(this.villager instanceof ISwitchableEntity switchableEntity) {
			modeButtonU = switchableEntity.time_feeds_villager$getMode().ordinal() * 10 + 10;
		}
		this.renderButton(transform, mouseX, mouseY, 68, 18, modeButtonU, 1, 10);
	}

	@SuppressWarnings("SameParameterValue")
	private void renderButton(GuiGraphics transform, int mouseX, int mouseY, int buttonX, int buttonY, int buttonU, int buttonIndex, int buttonSize) {
		int buttonV = this.imageHeight;
		buttonX = this.leftPos + buttonX;
		buttonY = this.topPos + buttonY;
		if(this.clicked == buttonIndex) {
			buttonV += buttonSize;
		} else if(mouseX >= buttonX && mouseX < buttonX + buttonSize && mouseY >= buttonY && mouseY < buttonY + buttonSize) {
			buttonV += buttonSize * 2;
		}
		transform.blit(LOCATION, buttonX, buttonY, buttonU, buttonV, buttonSize, buttonSize);
	}

	@Override
	public void render(GuiGraphics transform, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(transform);
		super.render(transform, mouseX, mouseY, partialTick);
		this.renderTooltip(transform, mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(GuiGraphics transform, int x, int y) {
		super.renderTooltip(transform, x, y);
		this.renderButtonTooltip(transform, x, y, 68, 60, "closet", 10);
		String modeName = "work";
		if(this.villager instanceof ISwitchableEntity switchableEntity) {
			modeName = switchableEntity.time_feeds_villager$getMode().name().toLowerCase(Locale.ROOT);
		}
		this.renderButtonTooltip(transform, x, y, 68, 18, "mode.%s".formatted(modeName), 10);
	}

	@SuppressWarnings("SameParameterValue")
	private void renderButtonTooltip(GuiGraphics transform, int x, int y, int buttonX, int buttonY, String buttonTooltip, int buttonSize) {
		double dx = x - this.leftPos - buttonX;
		double dy = y - this.topPos - buttonY;
		if(dx >= 0.0D && dy >= 0.0D && dx < buttonSize && dy < buttonSize) {
			transform.renderTooltip(this.font, Component.translatable("gui.time_feeds_villager." + buttonTooltip), x, y);
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int mouseButton) {
		return this.mouseClickedButton(x, y, 68, 60, 0, 10) ||
				this.mouseClickedButton(x, y, 68, 18, 1, 10) ||
				super.mouseClicked(x, y, mouseButton);
	}

	@SuppressWarnings("SameParameterValue")
	private boolean mouseClickedButton(double x, double y, double buttonX, double buttonY, int buttonIndex, double buttonSize) {
		double dx = x - this.leftPos - buttonX;
		double dy = y - this.topPos - buttonY;
		if(dx >= 0.0D && dy >= 0.0D && dx < buttonSize && dy < buttonSize) {
			this.clicked = buttonIndex;
			return true;
		}
		return false;
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	public boolean mouseReleased(double x, double y, int mouseButton) {
		if(this.clicked >= 0) {
			boolean flag = this.mouseReleasedButton(x, y, 68, 60, 0, 10) ||
					this.mouseReleasedButton(x, y, 68, 18, 1, 10);

			if(flag && this.menu.clickMenuButton(this.minecraft.player, this.clicked)) {
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
				this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, this.clicked);
				this.clicked = -1;
				return true;
			}
		}
		this.clicked = -1;
		return super.mouseReleased(x, y, mouseButton);
	}

	@SuppressWarnings("SameParameterValue")
	private boolean mouseReleasedButton(double x, double y, double buttonX, double buttonY, int buttonIndex, double buttonSize) {
		double dx = x - this.leftPos - buttonX;
		double dy = y - this.topPos - buttonY;
		if(dx >= 0.0D && dy >= 0.0D && dx < buttonSize && dy < buttonSize) {
			return this.clicked == buttonIndex;
		}
		return false;
	}
}
