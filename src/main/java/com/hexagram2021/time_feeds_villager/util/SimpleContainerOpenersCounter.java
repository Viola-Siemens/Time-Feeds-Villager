package com.hexagram2021.time_feeds_villager.util;

public class SimpleContainerOpenersCounter {
	private int openCount = 0;

	public void incrementOpeners() {
		this.openCount += 1;
	}
	public void decrementOpeners() {
		this.openCount -= 1;
	}

	public int getOpenerCount() {
		return this.openCount;
	}
}
