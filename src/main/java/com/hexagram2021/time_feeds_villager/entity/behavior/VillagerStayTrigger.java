package com.hexagram2021.time_feeds_villager.entity.behavior;

import com.google.common.collect.ImmutableMap;
import com.hexagram2021.time_feeds_villager.entity.ISwitchableEntity;
import com.hexagram2021.time_feeds_villager.register.TFVActivities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;

public class VillagerStayTrigger extends Behavior<Villager> {
	public VillagerStayTrigger() {
		super(ImmutableMap.of());
	}

	@Override
	protected boolean canStillUse(ServerLevel level, Villager entity, long gameTime) {
		return !entity.isTrading() && entity instanceof ISwitchableEntity switchableEntity && switchableEntity.time_feeds_villager$getMode() == ISwitchableEntity.Mode.STAY;
	}

	@Override
	protected void start(ServerLevel level, Villager entity, long gameTime) {
		if(entity instanceof ISwitchableEntity switchableEntity && switchableEntity.time_feeds_villager$getMode() == ISwitchableEntity.Mode.STAY) {
			Brain<?> brain = entity.getBrain();
			if(brain.isActive(Activity.PANIC) || brain.isActive(TFVActivities.FORAGE.get())) {
				return;
			}
			if(!brain.isActive(TFVActivities.STAY.get())) {
				brain.eraseMemory(MemoryModuleType.PATH);
				brain.eraseMemory(MemoryModuleType.WALK_TARGET);
				brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
				brain.eraseMemory(MemoryModuleType.BREED_TARGET);
				brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
			}
			brain.setActiveActivityIfPossible(TFVActivities.STAY.get());
		}
	}
}
