package es.outlook.adriansrj.cv.api.vehicle.configuration.model.compound.pre19;

import es.outlook.adriansrj.cv.api.enums.EnumVehicleModelType;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleHitBoxConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleModelConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleParticleConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleSeatConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleSoundConfiguration;
import lombok.Builder;
import lombok.Singular;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author AdrianSR / 23/11/2023 / 12:53 p. m.
 */
public class Pre19CompoundModelConfiguration extends VehicleModelConfiguration {
	
	private final @NotNull Set < PartConfiguration > parts;
	
	@Builder
	public Pre19CompoundModelConfiguration ( @NotNull String id ,
			@NotNull VehicleHitBoxConfiguration hitBox ,
			@NotNull @Singular Collection < VehicleSeatConfiguration > seats ,
			@NotNull @Singular Collection < PartConfiguration > parts ,
			@Nullable @Singular Collection < VehicleParticleConfiguration > particles ,
			@Nullable @Singular Collection < VehicleSoundConfiguration > sounds ) {
		super ( id , hitBox , seats , particles , sounds );
		
		this.parts = new HashSet <> ( parts );
	}
	
	public Pre19CompoundModelConfiguration ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		super ( section );
		
		// loading parts
		parts = new HashSet <> ( );
		
		for ( ConfigurationSection partSection : ConfigurationUtil.getConfigurationSectionsAfter (
				section , Constants.Key.PARTS , false ) ) {
			try {
				parts.add ( PartConfiguration.load ( partSection ) );
			} catch ( InvalidConfigurationException ex ) {
				ex.printStackTrace ( );
			}
		}
		
		if ( parts.size ( ) == 0 ) {
			throw new InvalidConfigurationException ( "at least one valid part is required" );
		}
	}
	
	@Override
	public @NotNull EnumVehicleModelType getType ( ) {
		return EnumVehicleModelType.COMPOUND;
	}
	
	public @NotNull Set < PartConfiguration > getParts ( ) {
		return Collections.unmodifiableSet ( parts );
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		super.write ( section );
		
		ConfigurationUtil.writeConfigurationSectionWritables (
				section.createSection ( Constants.Key.PARTS ) ,
				parts , "part-"
		);
	}
}