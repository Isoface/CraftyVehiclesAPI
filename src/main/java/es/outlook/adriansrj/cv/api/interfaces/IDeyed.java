package es.outlook.adriansrj.cv.api.interfaces;

import es.outlook.adriansrj.cv.api.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 23/11/2023 / 6:45 p. m.
 */
public interface IDeyed {
	
	static boolean isValidId ( @NotNull String id ) {
		return StringUtils.isNotBlank ( id )
				&& id.toLowerCase ( ).matches ( Constants.VALID_ID_PATTERN );
	}
	
	static String idCheck ( @NotNull String id ) {
		if ( StringUtils.isBlank ( id ) ) {
			throw new IllegalArgumentException ( "id cannot be blank" );
		} else if ( !id.toLowerCase ( ).matches ( Constants.VALID_ID_PATTERN ) ) {
			throw new IllegalArgumentException ( "id contains invalid characters" );
		}
		
		return id.toLowerCase ( );
	}
	
	static String loadId ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		String id = section.getString ( Constants.Key.ID );
		
		if ( id == null ) {
			throw new InvalidConfigurationException ( "id must be set" );
		}
		
		try {
			return IDeyed.idCheck ( id.toLowerCase ( ) );
		} catch ( IllegalArgumentException ex ) {
			throw new InvalidConfigurationException ( ex.getMessage ( ) );
		}
	}
	
	static void writeId ( IDeyed ideyed , @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.ID , idCheck ( ideyed.getId ( ) ) );
	}
	
	static void writeId ( @NotNull String id , @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.ID , idCheck ( id ) );
	}
	
	@NotNull String getId ( );
}