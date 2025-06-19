package com.hexagram2021.time_feeds_villager.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.hexagram2021.time_feeds_villager.TimeFeedsVillager.MODID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModClientEventSubscriber {
	@SubscribeEvent
	public static void setup(final FMLClientSetupEvent event) {
		event.enqueueWork(ModClientEventSubscriber::onRegisterScreens);
	}

	private static void onRegisterScreens() {
	}

	private ModClientEventSubscriber() {
	}
}
