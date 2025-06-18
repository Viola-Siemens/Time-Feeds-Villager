package com.hexagram2021.time_feeds_villager.entity.behavior;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.time_feeds_villager.register.TFVMemoryModuleTypes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.StrollToPoi;
import net.minecraft.world.entity.npc.Villager;

public class VillagerExtraGoalPackages {
	public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getForagePackage(float speedModifier) {
		ImmutableList.Builder<Pair<Integer, ? extends BehaviorControl<? super Villager>>> builder = ImmutableList.builder();
		builder.add(Pair.of(3, VillagerForageGoalPackage.findContainerToSteal(600)))
				.add(Pair.of(3, VillagerForageGoalPackage.setWalkTarget(speedModifier, 5, 100, 600)))
				.add(Pair.of(3, VillagerForageGoalPackage.openContainer(Mth.square(6))))
				.add(Pair.of(2, VillagerForageGoalPackage.closeContainer(Mth.square(8), 80)))
				.add(Pair.of(2, VillagerForageGoalPackage.eatFoodWhenHungry()))
				.add(Pair.of(6, StrollToPoi.create(TFVMemoryModuleTypes.NEAREST_CONTAINER.get(), 0.4F, 1, 10)));
		return builder.build();
	}
}
