package es.outlook.adriansrj.cv.api.vehicle.configuration;

import com.google.common.base.Preconditions;
import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.util.Constants;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 1/2/2024 / 2:43 a. m.
 */
@Getter
public class VehicleFuelConfiguration implements ConfigurationSectionWritable {
	
	public static final VehicleFuelConfiguration DEFAULTS = new VehicleFuelConfiguration (
			80.0F ,
			0.02F ,
			0.05F
	);
	
	public static VehicleFuelConfiguration load ( ConfigurationSection section )
			throws InvalidConfigurationException {
		float capacity = checkValue ( ( float ) section.getDouble (
				Constants.Key.CAPACITY ) , "capacity" );
		float minConsumption = checkValue ( ( float ) section.getDouble (
				Constants.Key.MIN_CONSUMPTION ) , "minimum consumption" );
		float maxConsumption = checkValue ( ( float ) section.getDouble (
				Constants.Key.MAX_CONSUMPTION ) , "maximum consumption" );
		
		return new VehicleFuelConfiguration ( capacity , minConsumption , maxConsumption );
	}
	
	private static float checkValue ( float value , String name )
			throws InvalidConfigurationException {
		if ( value >= 0.0F ) {
			return value;
		} else {
			throw new InvalidConfigurationException ( name + " cannot be negative" );
		}
	}
	
	private final float capacity;
	private final float minConsumption;
	private final float maxConsumption;
	
	public VehicleFuelConfiguration ( float capacity , float minConsumption , float maxConsumption ) {
		Preconditions.checkArgument ( capacity >= 0 , "capacity cannot be negative" );
		Preconditions.checkArgument (
				minConsumption >= 0 ,
				"minimum consumption cannot be negative"
		);
		Preconditions.checkArgument (
				maxConsumption >= 0 ,
				"maximum consumption cannot be negative"
		);
		
		this.capacity       = capacity;
		this.minConsumption = minConsumption;
		this.maxConsumption = maxConsumption;
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.CAPACITY , capacity );
		section.set ( Constants.Key.MIN_CONSUMPTION , minConsumption );
		section.set ( Constants.Key.MAX_CONSUMPTION , maxConsumption );
	}
}