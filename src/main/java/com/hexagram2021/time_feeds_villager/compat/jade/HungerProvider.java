package com.hexagram2021.time_feeds_villager.compat.jade;

import com.hexagram2021.time_feeds_villager.entity.IHungryEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import static com.hexagram2021.time_feeds_villager.TimeFeedsVillager.MODID;

public enum HungerProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
	INSTANCE;

	public static final ResourceLocation ID = new ResourceLocation(MODID, "hunger");
	private static final String TAG_IS_HUNGRY = "IsHungry";

	@Override
	public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
		if(entityAccessor.getServerData().getBoolean(TAG_IS_HUNGRY)) {
			iTooltip.add(Component.translatable("jade.time_feeds_villager.hungry"));
		}
	}

	@Override
	public void appendServerData(CompoundTag compoundTag, EntityAccessor entityAccessor) {
		Villager mob = (Villager) entityAccessor.getEntity();
		if(mob instanceof IHungryEntity hungryEntity) {
			compoundTag.putBoolean(TAG_IS_HUNGRY, hungryEntity.time_feeds_villager$isHungry());
		}
	}

	@Override
	public ResourceLocation getUid() {
		return ID;
	}
}
