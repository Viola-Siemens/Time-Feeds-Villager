package com.hexagram2021.time_feeds_villager.network;

import com.hexagram2021.time_feeds_villager.TimeFeedsVillager;
import com.hexagram2021.time_feeds_villager.entity.IHasCustomSkinEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

public record ServerboundRequestVillagerSkinPacket(UUID uuid) implements ITFVPacket {
	public ServerboundRequestVillagerSkinPacket(FriendlyByteBuf buf) {
		this(buf.readUUID());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUUID(this.uuid);
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ServerPlayer sender = context.getSender();
		if(sender != null) {
			Entity entity = sender.serverLevel().getEntity(this.uuid);
			if(entity instanceof IHasCustomSkinEntity hasCustomSkinEntity) {
				TimeFeedsVillager.packetHandler.send(PacketDistributor.PLAYER.with(() -> sender), new ClientboundUpdateVillagerSkinPacket(entity.getId(), hasCustomSkinEntity.time_feeds_villager$getCustomSkin()));
			}
		}
	}
}
