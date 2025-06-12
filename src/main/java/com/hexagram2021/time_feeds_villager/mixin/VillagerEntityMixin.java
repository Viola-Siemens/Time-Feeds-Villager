package com.hexagram2021.time_feeds_villager.mixin;

import com.hexagram2021.time_feeds_villager.entity.IAgingEntity;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Villager.class)
public class VillagerEntityMixin implements IAgingEntity {
	@Unique
	private boolean time_feeds_villager$immuneToAging = false;

	@Override
	public boolean time_feeds_villager$isImmuneToAging() {
		return this.time_feeds_villager$immuneToAging;
	}
}
