package es.outlook.adriansrj.cv.api.interfaces;

import es.outlook.adriansrj.cv.api.util.Constants;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author AdrianSR / 15/1/2024 / 5:48 p. m.
 */
public interface Identifiable {
	
	static @Nullable UUID loadIdentifier ( @NotNull ConfigurationSection section ) {
		String identifierString = section.getString ( Constants.Key.IDENTIFIER );
		
		if ( identifierString != null ) {
			try {
				return UUID.fromString ( identifierString );
			} catch ( IllegalArgumentException ignored ) {
				// ignored
			}
		}
		
		return null;
	}
	
	static @NotNull UUID loadIdentifierOrGenerate ( @NotNull ConfigurationSection section ) {
		UUID identifier = loadIdentifier ( section );
		
		return identifier != null ? identifier : UUID.randomUUID ( );
	}
	
	static void writeIdentifier ( @NotNull Identifiable identifiable , @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.IDENTIFIER , identifiable.getIdentifier ( ).toString ( ) );
	}
	
	static void writeIdentifier ( @NotNull UUID identifier , @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.IDENTIFIER , identifier.toString ( ) );
	}
	
	@NotNull UUID getIdentifier ( );
}