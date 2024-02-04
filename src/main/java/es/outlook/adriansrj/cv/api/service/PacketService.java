package es.outlook.adriansrj.cv.api.service;

import es.outlook.adriansrj.cv.api.player.PlayerWrapper;
import io.netty.channel.ChannelPipeline;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * We use nms for sending packets for optimization purposes.
 * <br>
 * @author AdrianSR / 19/11/2023 / 2:13 p. m.
 */
public interface PacketService extends Service {
	
	ChannelPipeline getChannelPipeline ( @NotNull Player player );
	
	void sendPacket ( @NotNull Player player , @NotNull Object packet , boolean compress );
	
	default void sendPacket ( @NotNull Player player , @NotNull Object packet ) {
		sendPacket ( player , packet , false );
	}
	
	default void sendPacket ( @NotNull PlayerWrapper wrapper , @NotNull Object packet , boolean compress ) {
		Player player = wrapper.get ( );
		
		if ( player != null && player.isOnline ( ) ) {
			sendPacket ( player , packet , compress );
		}
	}
	
	default void sendPacket ( @NotNull PlayerWrapper wrapper , @NotNull Object packet ) {
		sendPacket ( wrapper , packet , false );
	}
}