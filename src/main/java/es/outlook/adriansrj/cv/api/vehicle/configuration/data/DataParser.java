package es.outlook.adriansrj.cv.api.vehicle.configuration.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AdrianSR / 27/1/2024 / 6:42 p. m.
 */
public abstract class DataParser {
	
	public static DataParser getParser ( @NotNull ConfigurationSection section ) {
		return DataParsers.getParser ( section.getString ( "data-type" , "" ) );
	}
	
	public abstract @NotNull String getIdentifier ( );
	
	public abstract @NotNull Class < ? > getType ( );
	
	// -- parsing
	
	public abstract @Nullable Object parse ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException;
	
	// -- writing
	
	public void write ( @NotNull Object value , @NotNull ConfigurationSection section ) {
		writeIdentifier ( section );
	}
	
	protected void writeIdentifier ( @NotNull ConfigurationSection section ) {
		section.set ( "data-type" , getIdentifier ( ).toLowerCase ( ).trim ( ) );
	}
}