package com.hexagram2021.time_feeds_villager.register;

import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;

import static com.hexagram2021.time_feeds_villager.TimeFeedsVillager.MODID;

public final class TFVMemoryModuleTypes {
	private static final DeferredRegister<MemoryModuleType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, MODID);

	public static final RegistryObject<MemoryModuleType<GlobalPos>> NEAREST_CONTAINER = register("nearest_container", GlobalPos.CODEC);
	public static final RegistryObject<MemoryModuleType<Long>> LAST_TRIED_TO_STEAL_FOOD = register("last_tried_to_steal_food", Codec.LONG);
	public static final RegistryObject<MemoryModuleType<Long>> LAST_OPEN_CONTAINER = register("last_open_container", Codec.LONG);

	private TFVMemoryModuleTypes() {
	}

	private static <U> RegistryObject<MemoryModuleType<U>> register(String name, Codec<U> codec) {
		return REGISTER.register(name, () -> new MemoryModuleType<>(Optional.of(codec)));
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
