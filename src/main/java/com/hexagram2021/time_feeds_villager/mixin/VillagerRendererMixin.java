package com.hexagram2021.time_feeds_villager.mixin;

import com.hexagram2021.time_feeds_villager.entity.IHasCustomSkinEntity;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerRenderer.class)
public class VillagerRendererMixin {
	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/npc/Villager;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "HEAD"), cancellable = true)
	private void time_feeds_villager$getCustomSkin(Villager entity, CallbackInfoReturnable<ResourceLocation> cir) {
		if(entity instanceof IHasCustomSkinEntity hasCustomSkinEntity) {
			cir.setReturnValue(hasCustomSkinEntity.time_feeds_villager$getCustomSkin());
		}
	}
}
