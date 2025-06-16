package com.hexagram2021.time_feeds_villager.compat.jade;

import com.hexagram2021.time_feeds_villager.entity.IAgingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgeableMob;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import static com.hexagram2021.time_feeds_villager.TimeFeedsVillager.MODID;

public enum AgingProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
	INSTANCE;

	public static final ResourceLocation ID = new ResourceLocation(MODID, "aging");
	private static final String TAG_AGING = "AgingRatio";
	private static final String TAG_IS_IMMUNE_TO_AGING = "IsImmuneToAging";

	@Override
	public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
		CompoundTag serverData = entityAccessor.getServerData();
		if(serverData.getBoolean(TAG_IS_IMMUNE_TO_AGING)) {
			iTooltip.add(Component.translatable("jade.time_feeds_villager.immune_to_aging"));
			return;
		}
		double ratio = serverData.getDouble(TAG_AGING);
		MutableComponent component;
		if(ratio < 0) {
			component = Component.translatable("jade.time_feeds_villager.childhood");
		} else if(ratio < 0.4D) {
			component = Component.translatable("jade.time_feeds_villager.adulthood");
		} else if(ratio < 0.75D) {
			component = Component.translatable("jade.time_feeds_villager.midlife");
		} else {
			component = Component.translatable("jade.time_feeds_villager.mature_adulthood");
		}
		iTooltip.add(Component.translatable("jade.time_feeds_villager.bracket", component));
	}

	@Override
	public void appendServerData(CompoundTag compoundTag, EntityAccessor entityAccessor) {
		AgeableMob mob = (AgeableMob) entityAccessor.getEntity();
		if(mob instanceof IAgingEntity agingEntity && agingEntity.time_feeds_villager$getMaxAge() > 0) {
			compoundTag.putDouble(TAG_AGING, (double)agingEntity.time_feeds_villager$getAge() / agingEntity.time_feeds_villager$getMaxAge());
			if(agingEntity.time_feeds_villager$isImmuneToAging()) {
				compoundTag.putBoolean(TAG_IS_IMMUNE_TO_AGING, true);
			}
		}
	}

	@Override
	public ResourceLocation getUid() {
		return ID;
	}
}
