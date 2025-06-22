package com.hexagram2021.time_feeds_villager.mixin;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.time_feeds_villager.entity.behavior.VillagerForageTrigger;
import com.hexagram2021.time_feeds_villager.entity.behavior.VillagerStayTrigger;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerGoalPackages.class)
public class VillagerGoalPackagesMixin {
	@ModifyReturnValue(method = "getCorePackage", at = @At(value = "RETURN"))
	private static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> time_feeds_villager$tweakCorePackage(ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> original) {
		ImmutableList.Builder<Pair<Integer, ? extends BehaviorControl<? super Villager>>> builder = ImmutableList.builder();
		builder.addAll(original).add(Pair.of(0, new VillagerForageTrigger())).add(Pair.of(0, new VillagerStayTrigger()));
		return builder.build();
	}
}
