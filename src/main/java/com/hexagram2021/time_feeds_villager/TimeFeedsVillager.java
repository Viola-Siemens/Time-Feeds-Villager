package com.hexagram2021.time_feeds_villager;

import com.hexagram2021.time_feeds_villager.config.TFVCommonConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(TimeFeedsVillager.MODID)
public class TimeFeedsVillager {
	public static final String MODID = "time_feeds_villager";

	public TimeFeedsVillager() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TFVCommonConfig.getConfig());
	}
}