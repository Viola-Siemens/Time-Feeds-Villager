package com.hexagram2021.time_feeds_villager.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

import static com.hexagram2021.time_feeds_villager.TimeFeedsVillager.MODID;

public class TFVDamageSources {
	private static final ResourceKey<DamageType> DIE_IN_BED = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(MODID, "die_in_bed"));

	public static DamageSource dieInBed(LivingEntity victim) {
		return victim.damageSources().source(DIE_IN_BED);
	}
}
