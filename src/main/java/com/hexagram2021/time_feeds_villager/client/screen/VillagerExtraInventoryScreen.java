package com.hexagram2021.time_feeds_villager.client.screen;

import com.hexagram2021.time_feeds_villager.menu.VillagerExtraInventoryMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Inventory;

import static com.hexagram2021.time_feeds_villager.TimeFeedsVillager.MODID;

public class VillagerExtraInventoryScreen extends AbstractContainerScreen<VillagerExtraInventoryMenu> {
	private static final ResourceLocation LOCATION = new ResourceLocation(MODID, "textures/gui/container/villager.png");
	private final Villager villager;
	private float xMouse;
	private float yMouse;

	public VillagerExtraInventoryScreen(VillagerExtraInventoryMenu menu, Inventory playerInventory, Villager villager) {
		super(menu, playerInventory, villager.getDisplayName());
		this.villager = villager;
	}

	@Override
	protected void renderBg(GuiGraphics transform, float partialTick, int mouseX, int mouseY) {
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		transform.blit(LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
		InventoryScreen.renderEntityInInventoryFollowsMouse(transform, i + 51, j + 60, 17, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.villager);
	}

	@Override
	public void render(GuiGraphics transform, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(transform);
		this.xMouse = (float)mouseX;
		this.yMouse = (float)mouseY;
		super.render(transform, mouseX, mouseY, partialTick);
		this.renderTooltip(transform, mouseX, mouseY);
	}
}
