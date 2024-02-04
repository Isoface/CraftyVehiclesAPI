package es.outlook.adriansrj.cv.api.registry.types;

import es.outlook.adriansrj.cv.api.CraftyVehicles;
import es.outlook.adriansrj.cv.api.registry.ConfigurationRegistryBase;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleModelConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author AdrianSR / 23/11/2023 / 3:44 p. m.
 */
public final class VehicleModelConfigurationRegistry
		extends ConfigurationRegistryBase < VehicleModelConfiguration > {
	
	private static final Set < VehicleModelConfiguration > DEFAULTS = new HashSet <> ( );
	
	static {
		// TODO: ADD DEFAULT MODEL
	}
	
	@Override
	protected @NotNull File getFolder ( ) {
		return Constants.Files.VEHICLE_MODELS_FOLDER;
	}
	
	@Override
	protected VehicleModelConfiguration loadEntry ( File file ) throws Exception {
		try {
			return VehicleModelConfiguration.load ( YamlConfiguration.loadConfiguration ( file ) );
		} catch ( Exception ex ) {
			CraftyVehicles.getPlugin ( ).getLogger ( ).severe (
					"couldn't load vehicle model: " + file.getName ( ) );
			
			throw ex;
		}
	}
	
	@Override
	protected Set < VehicleModelConfiguration > getDefaults ( ) {
		return DEFAULTS;
	}
	
	@Override
	protected void writeEntry ( @NotNull VehicleModelConfiguration entry , YamlConfiguration yaml ) {
		entry.write ( yaml );
	}
}