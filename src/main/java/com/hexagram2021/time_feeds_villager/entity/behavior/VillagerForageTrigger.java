package com.hexagram2021.time_feeds_villager.entity.behavior;

import com.google.common.collect.ImmutableMap;
import com.hexagram2021.time_feeds_villager.entity.IHungryEntity;
import com.hexagram2021.time_feeds_villager.register.TFVActivities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;

public class VillagerForageTrigger extends Behavior<Villager> {
	public VillagerForageTrigger() {
		super(ImmutableMap.of());
	}

	@Override
	protected boolean canStillUse(ServerLevel level, Villager entity, long gameTime) {
		return !entity.isTrading() && !entity.isSleeping() && entity instanceof IHungryEntity hungryEntity && hungryEntity.time_feeds_villager$isHungry();
	}

	@Override
	protected void start(ServerLevel level, Villager entity, long gameTime) {
		if(entity instanceof IHungryEntity hungryEntity && hungryEntity.time_feeds_villager$isHungry()) {
			Brain<?> brain = entity.getBrain();
			if(!brain.isActive(TFVActivities.FORAGE.get())) {
				brain.eraseMemory(MemoryModuleType.PATH);
				brain.eraseMemory(MemoryModuleType.WALK_TARGET);
				brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
				brain.eraseMemory(MemoryModuleType.BREED_TARGET);
				brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
			}
			brain.setActiveActivityIfPossible(TFVActivities.FORAGE.get());
		}
	}
}
