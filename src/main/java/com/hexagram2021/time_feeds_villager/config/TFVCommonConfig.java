package com.hexagram2021.time_feeds_villager.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TFVCommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.IntValue MAX_AGE;

	static {
		BUILDER.push("time_feeds_villager-common-config");
		MAX_AGE = BUILDER.comment("Max age (in ticks) of a villager.").defineInRange("MAX_AGE", 144000, 0, 7200000);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

	public static ForgeConfigSpec getConfig() {
		return SPEC;
	}
}
