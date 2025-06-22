package com.hexagram2021.time_feeds_villager.register;

import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.time_feeds_villager.TimeFeedsVillager.MODID;

public class TFVActivities {
	private static final DeferredRegister<Activity> REGISTER = DeferredRegister.create(ForgeRegistries.ACTIVITIES, MODID);

	public static final RegistryObject<Activity> FORAGE = register("forage");
	public static final RegistryObject<Activity> STAY = register("stay");

	private TFVActivities() {
	}

	private static RegistryObject<Activity> register(String name) {
		return REGISTER.register(name, () -> new Activity(MODID + "_" + name));
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
