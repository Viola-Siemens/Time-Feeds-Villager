package com.hexagram2021.time_feeds_villager.mixin;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.time_feeds_villager.entity.behavior.GetFoodsFromContainers;
import com.hexagram2021.time_feeds_villager.register.TFVMemoryModuleTypes;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.StrollToPoi;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerGoalPackages.class)
public class VillagerGoalPackagesMixin {
	@ModifyReturnValue(method = "getIdlePackage", at = @At(value = "RETURN"))
	private static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> time_feeds_villager$tweakIdlePackage(ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> original, @Local(argsOnly = true) float speedModifier) {
		ImmutableList.Builder<Pair<Integer, ? extends BehaviorControl<? super Villager>>> builder = ImmutableList.builder();
		builder.addAll(original)
				.add(Pair.of(3, GetFoodsFromContainers.findContainerToSteal()))
				.add(Pair.of(3, GetFoodsFromContainers.setWalkTarget(speedModifier, 5, 100, 600)))
				.add(Pair.of(3, GetFoodsFromContainers.openContainer(Mth.square(6))))
				.add(Pair.of(2, GetFoodsFromContainers.closeContainer(Mth.square(8), 80)))
				.add(Pair.of(6, StrollToPoi.create(TFVMemoryModuleTypes.NEAREST_CONTAINER.get(), 0.4F, 1, 10)));
		return builder.build();
	}
}
