package com.hexagram2021.time_feeds_villager.client;

import com.hexagram2021.time_feeds_villager.entity.IHasCustomSkinEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

public final class ClientUtils {
	public static void setVillagerSkin(int id, ResourceLocation skin) {
		LocalPlayer player = Minecraft.getInstance().player;
		if(player != null && player.level().getEntity(id) instanceof IHasCustomSkinEntity hasCustomSkinEntity) {
			hasCustomSkinEntity.time_feeds_villager$setCustomSkin(skin);
		}
	}

	private ClientUtils() {
	}
}
