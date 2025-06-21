package com.hexagram2021.time_feeds_villager.util;

import com.hexagram2021.time_feeds_villager.TimeFeedsVillager;
import com.hexagram2021.time_feeds_villager.config.TFVCommonConfig;
import com.hexagram2021.time_feeds_villager.menu.VillagerClosetMenu;
import com.hexagram2021.time_feeds_villager.menu.VillagerExtraInventoryMenu;
import com.hexagram2021.time_feeds_villager.network.ClientboundVillagerClosetOpenPacket;
import com.hexagram2021.time_feeds_villager.network.ClientboundVillagerExtraInventoryOpenPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.Random;

public final class CommonUtils {
	@Nullable
	private static Int2ObjectMap<ResourceLocation> SKINS = null;
	@Nullable
	private static Object2IntMap<ResourceLocation> SKIN_IDS = null;
	private static final Random random = new Random();

	private static void lazyInitMaps() {
		SKINS = new Int2ObjectOpenHashMap<>();
		SKIN_IDS = new Object2IntOpenHashMap<>();
		TFVCommonConfig.VILLAGER_SKINS.get().forEach(str -> {
			int id = SKINS.size();
			ResourceLocation skin = new ResourceLocation(str);
			SKINS.put(id, skin);
			SKIN_IDS.put(skin, id);
		});
		if(!SKIN_IDS.containsKey(TimeFeedsVillager.VILLAGER_BASE_SKIN)) {
			TFVLogger.warn("Default skin was not found in entry `VILLAGER_SKINS`! Please check your config file!");
		}
	}

	public static int getSkinId(ResourceLocation skin) {
		if(SKIN_IDS == null) {
			lazyInitMaps();
		}
		assert SKINS != null;
		return SKIN_IDS.getOrDefault(skin, random.nextInt(SKINS.size()));
	}
	public static ResourceLocation getSkin(int id) {
		if(SKINS == null) {
			lazyInitMaps();
		}
		return SKINS.getOrDefault(id, TimeFeedsVillager.VILLAGER_BASE_SKIN);
	}
	@Contract(pure = true)
	public static int getSizeOfSkins() {
		if(SKINS == null) {
			lazyInitMaps();
		}
		return SKINS.size();
	}

	public static void openVillagerExtraInventory(ServerPlayer serverPlayer, Villager villager, Container extraInventory) {
		if (serverPlayer.containerMenu != serverPlayer.inventoryMenu) {
			serverPlayer.closeContainer();
		}

		serverPlayer.nextContainerCounter();
		TimeFeedsVillager.packetHandler.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ClientboundVillagerExtraInventoryOpenPacket(serverPlayer.containerCounter, villager.getId()));
		serverPlayer.containerMenu = new VillagerExtraInventoryMenu(serverPlayer.containerCounter, serverPlayer.getInventory(), extraInventory, villager);
		serverPlayer.initMenu(serverPlayer.containerMenu);
		MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(serverPlayer, serverPlayer.containerMenu));
	}

	public static void openVillagerCloset(ServerPlayer serverPlayer, Villager villager) {
		if (serverPlayer.containerMenu != serverPlayer.inventoryMenu) {
			serverPlayer.closeContainer();
		}

		serverPlayer.nextContainerCounter();
		TimeFeedsVillager.packetHandler.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ClientboundVillagerClosetOpenPacket(serverPlayer.containerCounter, villager.getId()));
		serverPlayer.containerMenu = new VillagerClosetMenu(serverPlayer.containerCounter, villager);
		serverPlayer.initMenu(serverPlayer.containerMenu);
		MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(serverPlayer, serverPlayer.containerMenu));
	}

	private CommonUtils() {
	}
}
