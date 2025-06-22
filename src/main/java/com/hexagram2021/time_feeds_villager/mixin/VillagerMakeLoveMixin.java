package com.hexagram2021.time_feeds_villager.mixin;

import com.hexagram2021.time_feeds_villager.entity.ISwitchableEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerMakeLove.class)
public class VillagerMakeLoveMixin {
	@Inject(method = "checkExtraStartConditions(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;)Z", at = @At(value = "HEAD"),  cancellable = true)
	private void time_feeds_villager$onlyWhileStaying(ServerLevel level, Villager owner, CallbackInfoReturnable<Boolean> cir) {
		if(owner instanceof ISwitchableEntity switchableEntity && switchableEntity.time_feeds_villager$getMode() != ISwitchableEntity.Mode.STAY) {
			cir.setReturnValue(false);
		}
	}
}
