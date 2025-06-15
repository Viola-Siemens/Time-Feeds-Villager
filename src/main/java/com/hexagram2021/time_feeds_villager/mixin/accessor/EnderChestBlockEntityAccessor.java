package com.hexagram2021.time_feeds_villager.mixin.accessor;

import com.hexagram2021.time_feeds_villager.block.entity.IContainerWithOpenersCounter;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnderChestBlockEntity.class)
public interface EnderChestBlockEntityAccessor extends IContainerWithOpenersCounter {
	@Override @Accessor(value = "openersCounter")
	ContainerOpenersCounter time_feeds_villager$getOpenersCounter();
}
