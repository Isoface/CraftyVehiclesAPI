package es.outlook.adriansrj.cv.api.interfaces;

import es.outlook.adriansrj.cv.api.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 23/11/2023 / 6:45 p. m.
 */
public interface Named {
	
	static boolean isValidName ( @NotNull String name ) {
		return StringUtils.isNotBlank ( name )
				&& name.toLowerCase ( ).matches ( Constants.VALID_NAME_PATTERN );
	}
	
	static String nameCheck ( @NotNull String name ) {
		if ( StringUtils.isBlank ( name ) ) {
			throw new IllegalArgumentException ( "name cannot be blank" );
		} else if ( !name.matches ( Constants.VALID_NAME_PATTERN ) ) {
			throw new IllegalArgumentException ( "name contains invalid characters" );
		}
		
		return name;
	}
	
	static String loadName ( @NotNull ConfigurationSection section , @Nullable String defaultName )
			throws InvalidConfigurationException {
		String name = section.getString ( Constants.Key.NAME , defaultName );
		
		if ( name == null ) {
			throw new InvalidConfigurationException ( "name must be set" );
		}
		
		try {
			return Named.nameCheck ( name );
		} catch ( IllegalArgumentException ex ) {
			throw new InvalidConfigurationException ( ex.getMessage ( ) );
		}
	}
	
	static String loadName ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		return loadName ( section , null );
	}
	
	static void writeName ( Named named , @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.NAME , nameCheck ( named.getName ( ) ) );
	}
	
	@NotNull String getName ( );
}