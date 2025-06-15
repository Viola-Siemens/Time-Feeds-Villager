package com.hexagram2021.time_feeds_villager.mixin;

import com.hexagram2021.time_feeds_villager.config.TFVCommonConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.TradeWithVillager;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TradeWithVillager.class)
public class TradeWithVillagerMixin {
	@Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V", at = @At(value = "HEAD"), cancellable = true)
	private void time_feeds_villager$tick(ServerLevel level, Villager owner, long gameTime, CallbackInfo ci) {
		if(TFVCommonConfig.REMOVE_TRADE_WITH_VILLAGER.get()) {
			ci.cancel();
		}
	}
}
