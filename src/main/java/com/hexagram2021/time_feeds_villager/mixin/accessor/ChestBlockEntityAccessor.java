package com.hexagram2021.time_feeds_villager.mixin.accessor;

import com.hexagram2021.time_feeds_villager.block.entity.IContainerWithOpenersCounter;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChestBlockEntity.class)
public interface ChestBlockEntityAccessor extends IContainerWithOpenersCounter {
	@Override @Accessor(value = "openersCounter")
	ContainerOpenersCounter time_feeds_villager$getOpenersCounter();
}
