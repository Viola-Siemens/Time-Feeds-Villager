package com.hexagram2021.time_feeds_villager.network;

import com.hexagram2021.time_feeds_villager.client.ClientUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ClientboundVillagerExtraInventoryOpenPacket implements ITFVPacket {
	private final int containerId;
	private final int entityId;

	public ClientboundVillagerExtraInventoryOpenPacket(int containerId, int entityId) {
		this.containerId = containerId;
		this.entityId = entityId;
	}

	public ClientboundVillagerExtraInventoryOpenPacket(FriendlyByteBuf buf) {
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
		context.enqueueWork(() -> ClientUtils.openVillagerExtraInventory(this.containerId, this.entityId));
	}
}
