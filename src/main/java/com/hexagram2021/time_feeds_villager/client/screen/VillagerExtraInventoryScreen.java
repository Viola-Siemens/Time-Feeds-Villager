package com.hexagram2021.time_feeds_villager.client.screen;

import com.hexagram2021.time_feeds_villager.menu.VillagerExtraInventoryMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Inventory;

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
		int buttonV = this.imageHeight;
		int buttonX = this.leftPos + 68;
		int buttonY = this.topPos + 60;
		if(this.clicked == 0) {
			buttonV += 10;
		} else if(mouseX >= buttonX && mouseX < buttonX + 10 && mouseY >= buttonY && mouseY < buttonY + 10) {
			buttonV += 20;
		}
		transform.blit(LOCATION, buttonX, buttonY, 0, buttonV, 10, 10);
	}

	@Override
	public void render(GuiGraphics transform, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(transform);
		super.render(transform, mouseX, mouseY, partialTick);
		this.renderTooltip(transform, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double x, double y, int mouseButton) {
		double dx = x - this.leftPos - 68;
		double dy = y - this.topPos - 60;
		if(dx >= 0.0D && dy >= 0.0D && dx < 10.0D && dy < 10.0D) {
			this.clicked = 0;
		}
		return super.mouseClicked(x, y, mouseButton);
	}

	@SuppressWarnings("DataFlowIssue")
	@Override
	public boolean mouseReleased(double x, double y, int mouseButton) {
		if(this.clicked >= 0) {
			boolean flag = false;

			double dx = x - this.leftPos - 68;
			double dy = y - this.topPos - 60;
			if(dx >= 0.0D && dy >= 0.0D && dx < 10.0D && dy < 10.0D) {
				if(this.clicked == 0) {
					flag = true;
				}
			}

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
}
