package com.hexagram2021.time_feeds_villager;

import com.hexagram2021.time_feeds_villager.config.TFVCommonConfig;
import com.hexagram2021.time_feeds_villager.network.*;
import com.hexagram2021.time_feeds_villager.register.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;

@Mod(TimeFeedsVillager.MODID)
public class TimeFeedsVillager {
	public static final String MODID = "time_feeds_villager";
	public static final String VERSION = ModList.get().getModFileById(MODID).versionString();

	public static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/villager/villager.png");

	public static final SimpleChannel packetHandler = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "main"))
			.networkProtocolVersion(() -> VERSION)
			.serverAcceptedVersions(VERSION::equals)
			.clientAcceptedVersions(VERSION::equals)
			.simpleChannel();

	public TimeFeedsVillager() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		TFVMemoryModuleTypes.init(bus);
		TFVActivities.init(bus);
		TFVMenuTypes.init(bus);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TFVCommonConfig.getConfig());
		bus.addListener(this::setup);
	}

	private static int messageId = 0;
	private static <T extends ITFVPacket> void registerMessage(Class<T> packetType,
															   Function<FriendlyByteBuf, T> constructor) {
		packetHandler.registerMessage(messageId++, packetType, ITFVPacket::write, constructor, (packet, ctx) -> packet.handle(ctx.get()));
	}

	private void setup(final FMLCommonSetupEvent event) {
		registerMessage(ServerboundRequestVillagerSkinPacket.class, ServerboundRequestVillagerSkinPacket::new);
		registerMessage(ServerboundRequestVillagerModePacket.class, ServerboundRequestVillagerModePacket::new);
		registerMessage(ClientboundUpdateVillagerSkinPacket.class, ClientboundUpdateVillagerSkinPacket::new);
		registerMessage(ClientboundUpdateVillagerModePacket.class, ClientboundUpdateVillagerModePacket::new);
		registerMessage(ClientboundVillagerExtraInventoryOpenPacket.class, ClientboundVillagerExtraInventoryOpenPacket::new);
		registerMessage(ClientboundVillagerClosetOpenPacket.class, ClientboundVillagerClosetOpenPacket::new);
		registerMessage(ClientboundVillagerEatFoodPacket.class, ClientboundVillagerEatFoodPacket::new);
	}
}