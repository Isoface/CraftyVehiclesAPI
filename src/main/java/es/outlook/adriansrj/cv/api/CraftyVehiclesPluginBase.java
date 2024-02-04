package es.outlook.adriansrj.cv.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author AdrianSR / 9/1/2024 / 4:42 p. m.
 */
public abstract class CraftyVehiclesPluginBase extends JavaPlugin {
	
	public abstract @NotNull File getJarFile ( );
}
