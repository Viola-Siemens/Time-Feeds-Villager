package com.hexagram2021.time_feeds_villager.network;

import com.hexagram2021.time_feeds_villager.client.ClientUtils;
import com.hexagram2021.time_feeds_villager.entity.ISwitchableEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class ClientboundUpdateVillagerModePacket implements ITFVPacket {
	private final int id;
	private final ISwitchableEntity.Mode mode;

	public ClientboundUpdateVillagerModePacket(int id, ISwitchableEntity.Mode mode) {
		this.id = id;
		this.mode = mode;
	}

	public ClientboundUpdateVillagerModePacket(FriendlyByteBuf buf) {
		this.id = buf.readVarInt();
		this.mode = ISwitchableEntity.Mode.valueOf(buf.readUtf());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeUtf(this.mode.name());
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ClientUtils.setVillagerMode(this.id, this.mode);
	}
}
