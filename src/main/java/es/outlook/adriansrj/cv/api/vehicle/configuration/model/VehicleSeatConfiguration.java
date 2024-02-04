package es.outlook.adriansrj.cv.api.vehicle.configuration.model;

import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 23/11/2023 / 12:47 p. m.
 */
@Getter
@Builder
@AllArgsConstructor
public class VehicleSeatConfiguration implements ConfigurationSectionWritable {
	
	public static VehicleSeatConfiguration load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		boolean main = section.getBoolean ( Constants.Key.MAIN );
		Vector3D offset = ConfigurationUtil.loadLibraryObject (
				Vector3D.class , section , Constants.Key.OFFSET );
		
		if ( offset == null ) {
			throw new InvalidConfigurationException ( "seat requires offset to be set" );
		}
		
		return new VehicleSeatConfiguration ( main , offset );
	}
	
	// whether is the seat from which the
	// driver controls the vehicle
	private final          boolean  main;
	private final @NotNull Vector3D offset;
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.MAIN , main );
		
		ConfigurationUtil.writeLibraryObject (
				Vector3D.class , offset ,
				section.createSection ( Constants.Key.OFFSET )
		);
	}
}