package com.hexagram2021.time_feeds_villager.mixin.accessor;

import com.hexagram2021.time_feeds_villager.block.entity.IContainerWithOpenersCounter;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BarrelBlockEntity.class)
public interface BarrelBlockEntityAccessor extends IContainerWithOpenersCounter {
	@Override @Accessor(value = "openersCounter")
	ContainerOpenersCounter time_feeds_villager$getOpenersCounter();
}
