package com.hexagram2021.time_feeds_villager.register;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hexagram2021.time_feeds_villager.TimeFeedsVillager.MODID;

public final class TFVMenuTypes {
	private static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

	public TFVMenuTypes() {
	}

	private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, MenuType.MenuSupplier<T> constructor) {
		return REGISTER.register(name, () -> new MenuType<>(constructor, FeatureFlags.VANILLA_SET));
	}

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
