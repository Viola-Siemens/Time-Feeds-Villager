package com.hexagram2021.time_feeds_villager.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IOpenersCounter {
	void time_feeds_villager$incrementEntityOpeners(Entity entity, Level level, BlockPos pos, BlockState state);
	void time_feeds_villager$decrementEntityOpeners(Entity entity, Level level, BlockPos pos, BlockState state);
}
