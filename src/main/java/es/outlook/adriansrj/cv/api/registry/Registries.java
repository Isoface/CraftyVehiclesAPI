package es.outlook.adriansrj.cv.api.registry;

import es.outlook.adriansrj.cv.api.interfaces.IDeyed;
import es.outlook.adriansrj.cv.api.item.ItemConfiguration;
import es.outlook.adriansrj.cv.api.registry.types.ItemConfigurationRegistry;
import es.outlook.adriansrj.cv.api.registry.types.VehicleConfigurationRegistry;
import es.outlook.adriansrj.cv.api.registry.types.VehicleControllerFactoryRegistry;
import es.outlook.adriansrj.cv.api.registry.types.VehicleModelConfigurationRegistry;
import es.outlook.adriansrj.cv.api.vehicle.configuration.VehicleConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleModelConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.controller.VehicleController;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author AdrianSR / 23/11/2023 / 5:41 p. m.
 */
public final class Registries {
	
	private static final Map < Class < ? extends IDeyed >, Registry < ? extends IDeyed > >
			REGISTRY_MAP = new LinkedHashMap <> ( ); // order is vital
	
	static {
		// order here is vital
		REGISTRY_MAP.put ( ItemConfiguration.class , new ItemConfigurationRegistry ( ) );
		REGISTRY_MAP.put ( VehicleModelConfiguration.class , new VehicleModelConfigurationRegistry ( ) );
		REGISTRY_MAP.put ( VehicleController.Factory.class , new VehicleControllerFactoryRegistry ( ) );
		REGISTRY_MAP.put ( VehicleConfiguration.class , new VehicleConfigurationRegistry ( ) );
	}
	
	public static void load ( ) {
		for ( Registry < ? > registry : REGISTRY_MAP.values ( ) ) {
			if ( registry instanceof ConfigurationRegistry ) {
				ConfigurationRegistry < ? > configurations = ( ConfigurationRegistry < ? > ) registry;
				
				try {
					configurations.saveDefaults ( );
				} catch ( IOException e ) {
					e.printStackTrace ( );
				}
			}
			
			registry.load ( );
		}
	}
	
	@SuppressWarnings ( "unchecked" )
	public static < T extends IDeyed, R extends Registry < T > > R getRegistry ( Class < T > type ) {
		return ( R ) REGISTRY_MAP.get ( type );
	}
}