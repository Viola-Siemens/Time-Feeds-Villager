package com.hexagram2021.time_feeds_villager.entity;

public interface IHungryEntity {
	boolean time_feeds_villager$isHungry();
	boolean time_feeds_villager$takeOutFoodAndStartEating();
	void time_feeds_villager$finishEating();
	int time_feeds_villager$remainingEatingTicks();
	void time_feeds_villager$tickEating();
}
