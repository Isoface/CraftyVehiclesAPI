package es.outlook.adriansrj.cv.api.interfaces;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 23/11/2023 / 4:27 p. m.
 */
public interface ConfigurationSectionWritable {
	
	void write ( @NotNull ConfigurationSection section );
}