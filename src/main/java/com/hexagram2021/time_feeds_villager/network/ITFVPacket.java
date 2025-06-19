package com.hexagram2021.time_feeds_villager.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public interface ITFVPacket {
	void write(FriendlyByteBuf buf);
	void handle(NetworkEvent.Context context);
}
