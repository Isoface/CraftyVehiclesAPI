package es.outlook.adriansrj.cv.api.handler;

import es.outlook.adriansrj.cv.api.player.PlayerWrapper;
import org.bukkit.entity.Player;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 19/11/2023 / 2:01 p. m.
 */
public interface PlayerWrapperHandler extends PluginHandler {
	
	@NotNull PlayerWrapper getWrapper ( @NotNull Player player );
	
	@NotNull PlayerWrapper getWrapper ( @NotNull UUID uuid );
}