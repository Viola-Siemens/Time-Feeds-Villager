package com.hexagram2021.time_feeds_villager.mixin;

import com.hexagram2021.time_feeds_villager.entity.IHungryEntity;
import net.minecraft.world.entity.ai.behavior.ShowTradesToPlayer;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShowTradesToPlayer.class)
public class ShowTradesToPlayerMixin {
	@Inject(method = "clearHeldItem", at = @At(value = "HEAD"), cancellable = true)
	private static void time_feeds_villager$stopShowingWhenHungry(Villager villager, CallbackInfo ci) {
		if(villager instanceof IHungryEntity hungryEntity && hungryEntity.time_feeds_villager$isHungry()) {
			ci.cancel();
		}
	}
}
