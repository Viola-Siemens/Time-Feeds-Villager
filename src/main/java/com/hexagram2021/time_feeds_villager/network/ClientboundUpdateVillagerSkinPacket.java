package com.hexagram2021.time_feeds_villager.network;

import com.hexagram2021.time_feeds_villager.client.ClientUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

public class ClientboundUpdateVillagerSkinPacket implements ITFVPacket {
	private final int id;
	private final ResourceLocation skin;

	public ClientboundUpdateVillagerSkinPacket(int id, ResourceLocation skin) {
		this.id = id;
		this.skin = skin;
	}

	public ClientboundUpdateVillagerSkinPacket(FriendlyByteBuf buf) {
		this.id = buf.readVarInt();
		this.skin = buf.readResourceLocation();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(this.id);
		buf.writeResourceLocation(this.skin);
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		ClientUtils.setVillagerSkin(this.id, this.skin);
	}
}
