package com.hexagram2021.time_feeds_villager.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

public interface ISwitchableEntity {
	Mode time_feeds_villager$getMode();
	void time_feeds_villager$setMode(Mode mode);

	enum Mode {
		STAY,
		WORK;

		@Nullable
		private static Int2ObjectMap<Mode> MODES = null;

		private static void lazyInitMaps() {
			MODES = new Int2ObjectOpenHashMap<>();
			for (Mode mode : Mode.values()) {
				MODES.put(mode.ordinal(), mode);
			}
		}

		public static int getModeId(Mode mode) {
			return mode.ordinal();
		}

		public static Mode getMode(int id) {
			if(MODES == null) {
				lazyInitMaps();
			}
			return MODES.getOrDefault(id, WORK);
		}

		@Contract(pure = true)
		public static int getSizeOfModes() {
			return Mode.values().length;
		}
	}
}
