package es.outlook.adriansrj.cv.api.vehicle.configuration.data;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AdrianSR / 28/1/2024 / 12:32 a. m.
 */
public class HeadTextureParser extends DataParser {
	
	@Override
	public @NotNull String getIdentifier ( ) {
		return "head-texture";
	}
	
	@Override
	public @NotNull Class < ? > getType ( ) {
		return HeadTexture.class;
	}
	
	@Override
	public @Nullable HeadTexture parse ( @NotNull ConfigurationSection section ) {
		String value = section.getString ( "value" );
		
		return value != null ? new HeadTexture ( value ) : null;
	}
	
	@Override
	public void write ( @NotNull Object value , @NotNull ConfigurationSection section ) {
		super.write ( value , section );
		
		if ( value instanceof HeadTexture ) {
			section.set ( "value" , ( ( HeadTexture ) value ).getValue ( ) );
		}
	}
}
