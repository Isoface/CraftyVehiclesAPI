package es.outlook.adriansrj.cv.api.vehicle.configuration.data;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 27/1/2024 / 6:48 p. m.
 */
public class DustOptionsParser extends DataParser {
	
	@Override
	public @NotNull String getIdentifier ( ) {
		return "dust-options";
	}
	
	@Override
	public @NotNull Class < ? > getType ( ) {
		return Particle.DustOptions.class;
	}
	
	@Override
	public Object parse ( @NotNull ConfigurationSection section ) throws InvalidConfigurationException {
		int   red   = checkColorComponent ( section.getInt ( "red" ) , "red" );
		int   green = checkColorComponent ( section.getInt ( "green" ) , "green" );
		int   blue  = checkColorComponent ( section.getInt ( "blue" ) , "blue" );
		float size  = ( float ) section.getDouble ( "size" );
		
		return new Particle.DustOptions ( Color.fromRGB ( red , green , blue ) , size );
	}
	
	private int checkColorComponent ( int component , String name ) throws InvalidConfigurationException {
		if ( component < 0 || component > 255 ) {
			throw new InvalidConfigurationException (
					"invalid dust color " + name + " component. must be between 0 and 255" );
		}
		
		return component;
	}
	
	@Override
	public void write ( @NotNull Object value , @NotNull ConfigurationSection section ) {
		super.write ( value , section );
		
		if ( value instanceof Particle.DustOptions ) {
			Particle.DustOptions options = ( Particle.DustOptions ) value;
			Color                color   = options.getColor ( );
			
			section.set ( "red" , color.getRed ( ) );
			section.set ( "green" , color.getGreen ( ) );
			section.set ( "blue" , color.getBlue ( ) );
			section.set ( "size" , options.getSize ( ) );
		}
	}
}
