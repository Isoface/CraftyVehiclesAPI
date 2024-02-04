package es.outlook.adriansrj.cv.api.player;

import io.netty.channel.ChannelPipeline;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author AdrianSR / 19/11/2023 / 2:01 p. m.
 */
public interface PlayerWrapper {
	
	@NotNull UUID getUUID ( );
	
	@Nullable Player get ( );
	
	@Nullable ChannelPipeline getPipeline ( );
	
	boolean isOnline ( );
	
	boolean isOffline ( );
}