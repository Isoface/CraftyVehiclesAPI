package es.outlook.adriansrj.cv.api.registry.types;

import es.outlook.adriansrj.cv.api.registry.ConfigurationRegistryBase;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.vehicle.configuration.VehicleConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Set;

/**
 * @author AdrianSR / 23/11/2023 / 3:46 p. m.
 */
public final class VehicleConfigurationRegistry
		extends ConfigurationRegistryBase < VehicleConfiguration > {
	
	@Override
	protected @NotNull File getFolder ( ) {
		return Constants.Files.VEHICLES_FOLDER;
	}
	
	@Override
	protected VehicleConfiguration loadEntry ( File file ) throws Exception {
		return VehicleConfiguration.load ( YamlConfiguration.loadConfiguration ( file ) );
	}
	
	@Override
	protected @Nullable Set < VehicleConfiguration > getDefaults ( ) {
		return null;
	}
	
	@Override
	protected void writeEntry ( @NotNull VehicleConfiguration entry , YamlConfiguration yaml ) {
		entry.write ( yaml );
	}
}