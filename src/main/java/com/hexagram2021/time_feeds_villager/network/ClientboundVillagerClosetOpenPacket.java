package com.hexagram2021.time_feeds_villager.network;

import com.hexagram2021.time_feeds_villager.client.ClientUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ClientboundVillagerClosetOpenPacket implements ITFVPacket {
	private final int containerId;
	private final int entityId;

	public ClientboundVillagerClosetOpenPacket(int containerId, int entityId) {
		this.containerId = containerId;
		this.entityId = entityId;
	}

	public ClientboundVillagerClosetOpenPacket(FriendlyByteBuf buf) {
		this.containerId = buf.readVarInt();
		this.entityId = buf.readVarInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.containerId);
		buf.writeVarInt(this.entityId);
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		context.enqueueWork(() -> ClientUtils.openVillagerCloset(this.containerId, this.entityId));
	}
}
