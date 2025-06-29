package com.hexagram2021.time_feeds_villager.entity;

import com.hexagram2021.time_feeds_villager.config.TFVCommonConfig;

public interface IAgingEntity {
	default int time_feeds_villager$getMaxAge() {
		return TFVCommonConfig.MAX_AGE.get();
	}

	int time_feeds_villager$getAge();
	void time_feeds_villager$setAge(int newAge);
	boolean time_feeds_villager$isImmuneToAging();
	void time_feeds_villager$setImmuneToAging();
	void time_feeds_villager$setImmuneToAging(boolean value);
}
