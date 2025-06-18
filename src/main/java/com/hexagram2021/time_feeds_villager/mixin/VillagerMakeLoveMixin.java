package com.hexagram2021.time_feeds_villager.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerMakeLove.class)
public class VillagerMakeLoveMixin {
	@ModifyReturnValue(method = "checkExtraStartConditions(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;)Z", at = @At("RETURN"))
	private boolean time_feeds_villager$onlyWhileStandingStill(boolean original) {
		return original && true;
	}
}
