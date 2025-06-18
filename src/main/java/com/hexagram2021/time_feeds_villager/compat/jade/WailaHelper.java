package com.hexagram2021.time_feeds_villager.compat.jade;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.npc.Villager;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class WailaHelper implements IWailaPlugin {
	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerEntityDataProvider(AgingProvider.INSTANCE, AgeableMob.class);
		registration.registerEntityDataProvider(HungerProvider.INSTANCE, Villager.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerEntityComponent(AgingProvider.INSTANCE, AgeableMob.class);
		registration.registerEntityComponent(HungerProvider.INSTANCE, Villager.class);
	}
}
