package com.hexagram2021.time_feeds_villager.entity.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hexagram2021.time_feeds_villager.register.TFVMemoryModuleTypes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

import java.util.List;

public class VillagerExtraGoalPackages {
	public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getForagePackage(float speedModifier) {
		ImmutableList.Builder<Pair<Integer, ? extends BehaviorControl<? super Villager>>> builder = ImmutableList.builder();
		builder.add(Pair.of(2, VillagerForageGoalPackage.closeContainer(Mth.square(8), 80)))
				.add(Pair.of(2, VillagerForageGoalPackage.eatFoodWhenHungry()))
				.add(Pair.of(3, VillagerForageGoalPackage.findContainerToSteal(200)))
				.add(Pair.of(3, VillagerForageGoalPackage.setWalkTarget(speedModifier, 5, 100, 600)))
				.add(Pair.of(3, VillagerForageGoalPackage.openContainer(Mth.square(6))))
				.add(Pair.of(6, StrollToPoi.create(TFVMemoryModuleTypes.NEAREST_CONTAINER.get(), 0.4F, 1, 10)));
		return builder.build();
	}

	public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getStayPackage(float speedModifier) {
		ImmutableList.Builder<Pair<Integer, ? extends BehaviorControl<? super Villager>>> builder = ImmutableList.builder();
		builder.add(Pair.of(2, new RunOne<>(
				List.of(
						Pair.of(InteractWith.of(EntityType.VILLAGER, 8, AgeableMob::canBreed, AgeableMob::canBreed, MemoryModuleType.BREED_TARGET, speedModifier, 2), 1)
				)
		))).add(Pair.of(3, new GateBehavior<>(
				ImmutableMap.of(),
				ImmutableSet.of(MemoryModuleType.BREED_TARGET),
				GateBehavior.OrderPolicy.ORDERED,
				GateBehavior.RunningPolicy.RUN_ONE,
				ImmutableList.of(Pair.of(new VillagerMakeLove(), 1))
		))).add(VillagerGoalPackages.getFullLookBehavior());
		return builder.build();
	}
}
