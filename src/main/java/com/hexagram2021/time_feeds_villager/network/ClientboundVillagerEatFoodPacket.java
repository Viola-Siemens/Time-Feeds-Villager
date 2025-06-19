package com.hexagram2021.time_feeds_villager.network;

import com.hexagram2021.time_feeds_villager.client.ClientUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record ClientboundVillagerEatFoodPacket(int entityId) implements ITFVPacket {
	public ClientboundVillagerEatFoodPacket(FriendlyByteBuf buf) {
		this(buf.readVarInt());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId);
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ClientUtils.handleVillagerEatFood(this.entityId);
	}
}
