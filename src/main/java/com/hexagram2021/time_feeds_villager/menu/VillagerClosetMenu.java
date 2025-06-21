package com.hexagram2021.time_feeds_villager.menu;

import com.hexagram2021.time_feeds_villager.TimeFeedsVillager;
import com.hexagram2021.time_feeds_villager.entity.IHasCustomSkinEntity;
import com.hexagram2021.time_feeds_villager.entity.IInventoryCarrier;
import com.hexagram2021.time_feeds_villager.util.CommonUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class VillagerClosetMenu extends AbstractContainerMenu {
	private final Villager villager;

	public VillagerClosetMenu(int containerId, final Villager villager) {
		super(null, containerId);
		this.villager = villager;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.villager.isAlive() && this.villager.distanceTo(player) < 8.0F;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean clickMenuButton(Player player, int index) {
		return switch (index) {
			case 0 -> this.defaultSkin();
			case 1 -> this.back(player);
			case 2 -> this.left();
			case 3 -> this.right();
			default -> super.clickMenuButton(player, index);
		};
	}

	private boolean defaultSkin() {
		if(this.villager instanceof IHasCustomSkinEntity hasCustomSkinEntity) {
			hasCustomSkinEntity.time_feeds_villager$setCustomSkin(TimeFeedsVillager.VILLAGER_BASE_SKIN);
			return true;
		}
		return false;
	}

	private boolean back(Player player) {
		if(!player.level().isClientSide && player instanceof ServerPlayer serverPlayer && this.villager instanceof IInventoryCarrier inventoryCarrier) {
			CommonUtils.openVillagerExtraInventory(serverPlayer, this.villager, inventoryCarrier.time_feeds_villager$getExtraInventory());
		}
		return true;
	}

	private boolean left() {
		if(this.villager instanceof IHasCustomSkinEntity hasCustomSkinEntity) {
			ResourceLocation skin = hasCustomSkinEntity.time_feeds_villager$getCustomSkin();
			int id = CommonUtils.getSkinId(skin);
			int totalSize = CommonUtils.getSizeOfSkins();
			id = (id + totalSize - 1) % totalSize;
			skin = CommonUtils.getSkin(id);
			hasCustomSkinEntity.time_feeds_villager$setCustomSkin(skin);
		}
		return true;
	}

	private boolean right() {
		if(this.villager instanceof IHasCustomSkinEntity hasCustomSkinEntity) {
			ResourceLocation skin = hasCustomSkinEntity.time_feeds_villager$getCustomSkin();
			int id = CommonUtils.getSkinId(skin);
			int totalSize = CommonUtils.getSizeOfSkins();
			id = (id + 1) % totalSize;
			skin = CommonUtils.getSkin(id);
			hasCustomSkinEntity.time_feeds_villager$setCustomSkin(skin);
		}
		return true;
	}
}
