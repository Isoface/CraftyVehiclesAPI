package es.outlook.adriansrj.cv.api.vehicle.configuration.model;

import com.google.common.base.Preconditions;
import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.util.Constants;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 23/11/2023 / 12:09 p. m.
 */
public class VehicleHitBoxConfiguration implements ConfigurationSectionWritable {
	
	public static VehicleHitBoxConfiguration load ( ConfigurationSection section )
			throws InvalidConfigurationException {
		double width  = section.getDouble ( Constants.Key.WIDTH );
		double height = section.getDouble ( Constants.Key.HEIGHT );
		double depth  = section.getDouble ( Constants.Key.DEPTH );
		
		if ( width <= 0.0D || height <= 0.0D || depth <= 0.0D ) {
			throw new InvalidConfigurationException (
					"hitbox width, height and depth must all be >= "
							+ Constants.HitBox.MIN_HIT_BOX_SIZE );
		}
		
		return new VehicleHitBoxConfiguration ( width , height , depth );
	}
	
	private final @Getter double width;
	private final @Getter double height;
	private final @Getter double depth;
	
	@Builder
	public VehicleHitBoxConfiguration ( double width , double height , double depth ) {
		Preconditions.checkArgument (
				width > 0.0D ,
				"width must be >= " + Constants.HitBox.MIN_HIT_BOX_SIZE
		);
		Preconditions.checkArgument (
				height > 0.0D ,
				"height must be >= " + Constants.HitBox.MIN_HIT_BOX_SIZE
		);
		Preconditions.checkArgument (
				depth > 0.0D ,
				"depth must be >= " + Constants.HitBox.MIN_HIT_BOX_SIZE
		);
		
		this.width  = width;
		this.height = height;
		this.depth  = depth;
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.WIDTH , width );
		section.set ( Constants.Key.HEIGHT , height );
		section.set ( Constants.Key.DEPTH , depth );
	}
}