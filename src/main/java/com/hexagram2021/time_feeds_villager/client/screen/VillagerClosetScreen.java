package com.hexagram2021.time_feeds_villager.client.screen;

import com.hexagram2021.time_feeds_villager.entity.IHasCustomSkinEntity;
import com.hexagram2021.time_feeds_villager.menu.VillagerClosetMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Inventory;

import static com.hexagram2021.time_feeds_villager.TimeFeedsVillager.MODID;

public class VillagerClosetScreen extends AbstractContainerScreen<VillagerClosetMenu> {
	private static final ResourceLocation LOCATION = new ResourceLocation(MODID, "textures/gui/container/villager_closet.png");
	private final Villager villager;

	public VillagerClosetScreen(VillagerClosetMenu menu, Inventory playerInventory, Villager villager) {
		super(menu, playerInventory, villager.getDisplayName());
		this.villager = villager;
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partialTick, int mouseX, int mouseY) {
		transform.blit(LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		this.renderButtons(transform, mouseX, mouseY);
		InventoryScreen.renderEntityInInventoryFollowsMouse(transform, this.leftPos + 87, this.topPos + 118, 44, this.leftPos + 87 - mouseX, this.topPos + 45 - mouseY, this.villager);
	}

	private int clicked = -1;
	private void renderButtons(GuiGraphics transform, int mouseX, int mouseY) {
		// default skin: 0
		this.renderButton(transform, mouseX, mouseY, 123, 101, 36, 0, 10);
		// back: 1
		this.renderButton(transform, mouseX, mouseY, 123, 111, 46, 1, 10);
		// left: 2
		this.renderButton(transform, mouseX, mouseY, 8, 64, 0, 2, 18);
		// right: 3
		this.renderButton(transform, mouseX, mouseY, 150, 64, 18, 3, 18);
	}

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
	protected void renderLabels(GuiGraphics transform, int mouseX, int mouseY) {
		transform.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
		if(this.villager instanceof IHasCustomSkinEntity hasCustomSkinEntity) {
			ResourceLocation name = hasCustomSkinEntity.time_feeds_villager$getCustomSkin();
			String i18nKey = "time_feeds_villager.skins.%s.%s".formatted(name.getNamespace(), name.getPath());
			Component component;
			if(I18n.exists(i18nKey)) {
				component = Component.translatable(i18nKey);
			} else {
				component = Component.literal(name.toString());
			}
			transform.drawString(this.font, component, 28, 136, 0x404040, false);
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int mouseButton) {
		return this.mouseClickedButton(x, y, 123, 101,0, 10) ||
				this.mouseClickedButton(x, y, 123, 111,1, 10) ||
				this.mouseClickedButton(x, y, 8, 64,2, 18) ||
				this.mouseClickedButton(x, y, 150, 64,3, 18) ||
				super.mouseClicked(x, y, mouseButton);
	}

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
			boolean flag = this.mouseReleasedButton(x, y, 123, 101,0, 10) ||
					this.mouseReleasedButton(x, y, 123, 111,1, 10) ||
					this.mouseReleasedButton(x, y, 8, 64,2, 18) ||
					this.mouseReleasedButton(x, y, 150, 64,3, 18);

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

	private boolean mouseReleasedButton(double x, double y, double buttonX, double buttonY, int buttonIndex, double buttonSize) {
		double dx = x - this.leftPos - buttonX;
		double dy = y - this.topPos - buttonY;
		if(dx >= 0.0D && dy >= 0.0D && dx < buttonSize && dy < buttonSize) {
			return this.clicked == buttonIndex;
		}
		return false;
	}
}
