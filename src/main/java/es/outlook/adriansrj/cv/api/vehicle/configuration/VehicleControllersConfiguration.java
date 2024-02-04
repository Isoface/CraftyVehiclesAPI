package es.outlook.adriansrj.cv.api.vehicle.configuration;

import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.interfaces.IDeyed;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.vehicle.controller.VehicleControllerProperties;
import lombok.Builder;
import lombok.Singular;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author AdrianSR / 25/11/2023 / 12:20 a. m.
 */
public class VehicleControllersConfiguration implements ConfigurationSectionWritable {
	
	public static final VehicleControllersConfiguration EMPTY = new VehicleControllersConfiguration ( null );
	
	public static VehicleControllersConfiguration load ( ConfigurationSection root )
			throws InvalidConfigurationException {
		Map < String, VehicleControllerProperties > entries = new LinkedHashMap <> ( );
		
		for ( ConfigurationSection section : ConfigurationUtil.getConfigurationSections (
				root , false ) ) {
			String controllerId = IDeyed.loadId ( section );
			
			// properties
			VehicleControllerProperties properties = new VehicleControllerProperties ( );
			
			ConfigurationSection propertiesSection = section
					.getConfigurationSection ( Constants.Key.PROPERTIES );
			
			if ( propertiesSection != null ) {
				properties = VehicleControllerProperties.load ( propertiesSection );
			}
			
			entries.put ( controllerId , properties );
		}
		
		return new VehicleControllersConfiguration ( entries );
	}
	
	// [ controller id <-> properties ]
	private final @NotNull Map < String, VehicleControllerProperties > entries = new LinkedHashMap <> ( );
	
	@Builder
	public VehicleControllersConfiguration (
			@Nullable @Singular Map < String, VehicleControllerProperties > entries ) {
		if ( entries != null ) {
			for ( Map.Entry < String, VehicleControllerProperties > entry : entries.entrySet ( ) ) {
				this.entries.put ( IDeyed.idCheck ( entry.getKey ( ) ) , entry.getValue ( ) );
			}
		}
	}
	
	public @NotNull Map < String, VehicleControllerProperties > getEntries ( ) {
		return Collections.unmodifiableMap ( entries );
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection root ) {
		int count = 0;
		
		for ( Map.Entry < String, VehicleControllerProperties > entry : entries.entrySet ( ) ) {
			String                      controllerId = entry.getKey ( );
			VehicleControllerProperties properties   = entry.getValue ( );
			
			// then writing
			ConfigurationSection section = root.createSection ( "controller-" + ( count++ ) );
			
			IDeyed.writeId ( controllerId , section );
			properties.write ( section.createSection ( Constants.Key.PROPERTIES ) );
		}
	}
}
