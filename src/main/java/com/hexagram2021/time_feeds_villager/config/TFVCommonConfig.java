package com.hexagram2021.time_feeds_villager.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class TFVCommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.IntValue MAX_AGE;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> FOODS_HELPS_IMMUNE_TO_AGING;

	static {
		BUILDER.push("time_feeds_villager-common-config");
		MAX_AGE = BUILDER.comment("Max age (in ticks) of a villager.").defineInRange("MAX_AGE", 144000, 0, 7200000);
		FOODS_HELPS_IMMUNE_TO_AGING = BUILDER.comment("Players who hold these foods in their hand can interact with villagers. After consuming the food, villagers will immune to aging.")
				.defineList("FOODS_HELPS_IMMUNE_TO_AGING", List.of(
						new ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, "golden_apple").toString(),
						new ResourceLocation(ResourceLocation.DEFAULT_NAMESPACE, "enchanted_golden_apple").toString()
				), o -> o instanceof String str && ResourceLocation.isValidResourceLocation(str));
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

	public static ForgeConfigSpec getConfig() {
		return SPEC;
	}
}
