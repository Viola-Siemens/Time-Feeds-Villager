package com.hexagram2021.time_feeds_villager.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.hexagram2021.time_feeds_villager.TimeFeedsVillager.MODID;

@SuppressWarnings("unused")
public class TFVLogger {
	public static boolean debugMode = true;
	public static Logger logger = LogManager.getLogger(MODID);

	public static void log(Level logLevel, Object object) {
		logger.log(logLevel, String.valueOf(object));
	}

	public static void error(Object object) {
		log(Level.ERROR, object);
	}

	public static void info(Object object) {
		log(Level.INFO, object);
	}

	public static void warn(Object object) {
		log(Level.WARN, object);
	}

	public static void error(String message, Object... params) {
		logger.log(Level.ERROR, message, params);
	}
	public static void error(String message, Throwable t) {
		logger.log(Level.ERROR, message, t);
	}

	public static void info(String message, Object... params) {
		logger.log(Level.INFO, message, params);
	}
	public static void info(String message, Throwable t) {
		logger.log(Level.INFO, message, t);
	}

	public static void warn(String message, Object... params) {
		logger.log(Level.WARN, message, params);
	}
	public static void warn(String message, Throwable t) {
		logger.log(Level.WARN, message, t);
	}

	public static void debug(Object object) {
		if(debugMode) {
			log(Level.INFO, "[DEBUG:] " + object);
		}
	}

	public static void debug(String format, Object... params) {
		if(debugMode) {
			info("[DEBUG:] " + format, params);
		}
	}
}
