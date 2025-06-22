package com.hexagram2021.time_feeds_villager.client;

import com.hexagram2021.time_feeds_villager.client.screen.VillagerClosetScreen;
import com.hexagram2021.time_feeds_villager.client.screen.VillagerExtraInventoryScreen;
import com.hexagram2021.time_feeds_villager.entity.IHasCustomSkinEntity;
import com.hexagram2021.time_feeds_villager.entity.IInventoryCarrier;
import com.hexagram2021.time_feeds_villager.entity.ISwitchableEntity;
import com.hexagram2021.time_feeds_villager.menu.VillagerClosetMenu;
import com.hexagram2021.time_feeds_villager.menu.VillagerExtraInventoryMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class ClientUtils {
	public static void setVillagerSkin(int id, ResourceLocation skin) {
		LocalPlayer player = Minecraft.getInstance().player;
		if(player != null && player.level().getEntity(id) instanceof IHasCustomSkinEntity hasCustomSkinEntity) {
			hasCustomSkinEntity.time_feeds_villager$setCustomSkin(skin);
		}
	}
	public static void setVillagerMode(int id, ISwitchableEntity.Mode mode) {
		LocalPlayer player = Minecraft.getInstance().player;
		if(player != null && player.level().getEntity(id) instanceof ISwitchableEntity switchableEntity) {
			switchableEntity.time_feeds_villager$setMode(mode);
		}
	}

	public static void openVillagerExtraInventory(int containerId, int entityId) {
		// assert: running on Render thread
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if(player != null && player.level().getEntity(entityId) instanceof Villager villager && villager instanceof IInventoryCarrier inventoryCarrier) {
			VillagerExtraInventoryMenu menu = new VillagerExtraInventoryMenu(containerId, player.getInventory(), inventoryCarrier.time_feeds_villager$getExtraInventory(), villager);
			player.containerMenu = menu;
			minecraft.setScreen(new VillagerExtraInventoryScreen(menu, player.getInventory(), villager));
		}
	}

	public static void openVillagerCloset(int containerId, int entityId) {
		// assert: running on Render thread
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if(player != null && player.level().getEntity(entityId) instanceof Villager villager) {
			VillagerClosetMenu menu = new VillagerClosetMenu(containerId, villager);
			player.containerMenu = menu;
			minecraft.setScreen(new VillagerClosetScreen(menu, player.getInventory(), villager));
		}
	}

	public static void handleVillagerEatFood(int entityId) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if(player != null && player.level().getEntity(entityId) instanceof Villager villager) {
			ItemStack itemStack = villager.getItemInHand(InteractionHand.MAIN_HAND);
			if(!itemStack.isEmpty()) {
				for (int ignored = 0; ignored < 8; ++ignored) {
					Vec3 speed = new Vec3((villager.getRandom().nextDouble() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
					speed = speed.xRot(-villager.getXRot() * ((float) Math.PI / 180F));
					speed = speed.yRot(-villager.getYRot() * ((float) Math.PI / 180F));
					Vec3 position = new Vec3((villager.getRandom().nextDouble() - 0.5D) * 0.3D, -villager.getRandom().nextDouble() * 0.6D - 0.3D, 0.6D);
					position = position.xRot(-villager.getXRot() * ((float) Math.PI / 180F));
					position = position.yRot(-villager.getYRot() * ((float) Math.PI / 180F));
					position = position.add(villager.getX(), villager.getEyeY(), villager.getZ());
					villager.level().addParticle(
							new ItemParticleOption(ParticleTypes.ITEM, itemStack),
							position.x(), position.y(), position.z(),
							speed.x(), speed.y(), speed.z()
					);
				}
			}
		}
	}

	private ClientUtils() {
	}
}
