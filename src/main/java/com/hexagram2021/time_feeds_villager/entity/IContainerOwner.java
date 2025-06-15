package com.hexagram2021.time_feeds_villager.entity;

import com.hexagram2021.time_feeds_villager.block.entity.IOpenersCounter;

public interface IContainerOwner {
	void time_feeds_villager$addOwnContainer(IOpenersCounter container);
	void time_feeds_villager$removeOwnContainer(IOpenersCounter container);
	boolean time_feeds_villager$isOwnContainer(IOpenersCounter container);
}
