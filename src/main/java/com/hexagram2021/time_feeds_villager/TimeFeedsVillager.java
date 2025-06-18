package com.hexagram2021.time_feeds_villager;

import com.hexagram2021.time_feeds_villager.config.TFVCommonConfig;
import com.hexagram2021.time_feeds_villager.register.TFVActivities;
import com.hexagram2021.time_feeds_villager.register.TFVMemoryModuleTypes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TimeFeedsVillager.MODID)
public class TimeFeedsVillager {
	public static final String MODID = "time_feeds_villager";

	public TimeFeedsVillager() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		TFVMemoryModuleTypes.init(bus);
		TFVActivities.init(bus);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TFVCommonConfig.getConfig());
	}
}