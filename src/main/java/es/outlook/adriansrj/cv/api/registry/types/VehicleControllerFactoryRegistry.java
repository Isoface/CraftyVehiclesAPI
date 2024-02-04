package es.outlook.adriansrj.cv.api.registry.types;

import es.outlook.adriansrj.cv.api.CraftyVehicles;
import es.outlook.adriansrj.cv.api.interfaces.ControllerFactoryProvider;
import es.outlook.adriansrj.cv.api.registry.RegistryBase;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.vehicle.Vehicle;
import es.outlook.adriansrj.cv.api.vehicle.controller.VehicleController;
import es.outlook.adriansrj.cv.api.vehicle.controller.VehicleControllerProperties;
import es.outlook.adriansrj.cv.api.vehicle.controller.predefined.FixedWingVehicleController;
import es.outlook.adriansrj.cv.api.vehicle.controller.predefined.GroundVehicleController;
import es.outlook.adriansrj.cv.api.vehicle.controller.predefined.HybridVehicleController;
import es.outlook.adriansrj.cv.api.vehicle.controller.predefined.RotorcraftVehicleController;
import es.outlook.adriansrj.cv.api.vehicle.controller.predefined.WatercraftVehicleController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author AdrianSR / 28/12/2023 / 1:36 p. m.
 */
public class VehicleControllerFactoryRegistry extends RegistryBase < VehicleController.Factory > {
	
	public VehicleControllerFactoryRegistry ( ) {
		// registering pre-defined ones
		register ( new VehicleController.Factory ( ) {
			
			@Override
			public @NotNull String getControllerId ( ) {
				return "fixed-wing-vehicle";
			}
			
			@Override
			public @NotNull VehicleController createInstance ( @NotNull Vehicle vehicle ,
					@Nullable VehicleControllerProperties properties ) {
				return new FixedWingVehicleController ( vehicle , properties );
			}
		} );
		
		register ( new VehicleController.Factory ( ) {
			
			@Override
			public @NotNull String getControllerId ( ) {
				return "ground-vehicle";
			}
			
			@Override
			public @NotNull VehicleController createInstance ( @NotNull Vehicle vehicle ,
					@Nullable VehicleControllerProperties properties ) {
				return new GroundVehicleController ( vehicle , properties );
			}
		} );
		
		register ( new VehicleController.Factory ( ) {
			
			@Override
			public @NotNull String getControllerId ( ) {
				return "hybrid-vehicle";
			}
			
			@Override
			public @NotNull VehicleController createInstance ( @NotNull Vehicle vehicle ,
					@Nullable VehicleControllerProperties properties ) {
				return new HybridVehicleController ( vehicle , properties );
			}
		} );
		
		register ( new VehicleController.Factory ( ) {
			
			@Override
			public @NotNull String getControllerId ( ) {
				return "rotorcraft-vehicle";
			}
			
			@Override
			public @NotNull VehicleController createInstance ( @NotNull Vehicle vehicle ,
					@Nullable VehicleControllerProperties properties ) {
				return new RotorcraftVehicleController ( vehicle , properties );
			}
		} );
		
		register ( new VehicleController.Factory ( ) {
			
			@Override
			public @NotNull String getControllerId ( ) {
				return "watercraft-vehicle";
			}
			
			@Override
			public @NotNull VehicleController createInstance ( @NotNull Vehicle vehicle ,
					@Nullable VehicleControllerProperties properties ) {
				return new WatercraftVehicleController ( vehicle , properties );
			}
		} );
	}
	
	@Override
	public void load ( ) {
		File   folder   = Constants.Files.VEHICLE_CONTROLLERS_FOLDER;
		File[] jarFiles = folder.listFiles ( ( dir , name ) -> name.toLowerCase ( ).endsWith ( ".jar" ) );
		
		try {
			int count = 0;
			
			if ( jarFiles != null && jarFiles.length > 0 ) {
				count = load ( jarFiles );
			}
			
			CraftyVehicles.getPlugin ( ).getLogger ( ).info (
					count > 0
							? count + " custom controllers were loaded!"
							: "No custom controllers were loaded!"
			);
		} catch ( MalformedURLException e ) {
			e.printStackTrace ( );
		}
	}
	
	private int load ( File... jarFiles ) throws MalformedURLException {
		int count = 0;
		
		try ( URLClassLoader loader = new URLClassLoader (
				getFileURLs ( jarFiles ) , getClass ( ).getClassLoader ( ) ) ) {
			for ( File file : jarFiles ) {
				count += load ( loader , file );
			}
		} catch ( IOException e ) {
			e.printStackTrace ( );
		}
		
		return count;
	}
	
	private int load ( URLClassLoader loader , File file ) {
		int count = 0;
		
		try ( JarFile jar = new JarFile ( file ) ) {
			Enumeration < JarEntry > entries = jar.entries ( );
			
			while ( entries.hasMoreElements ( ) ) {
				JarEntry entry = entries.nextElement ( );
				
				if ( !entry.getName ( ).endsWith ( ".class" ) ) {
					continue; // not a class
				}
				
				String className = entry.getName ( )
						.replaceAll ( "/" , "." )
						.replace ( ".class" , "" );
				
				Class < ? > clazz = loader.loadClass ( className );
				
				if ( clazz != null && ControllerFactoryProvider.class.isAssignableFrom ( clazz ) ) {
					ControllerFactoryProvider provider = ( ControllerFactoryProvider )
							clazz.getDeclaredConstructor ( ).newInstance ( );
					
					for ( VehicleController.Factory factory : provider.create ( ) ) {
						if ( factory != null ) {
							register ( factory );
							
							count++;
						} else {
							CraftyVehicles.getPlugin ( ).getLogger ( ).info (
									"Factory provider " + clazz.getName ( ) + " provided a null factory"
							);
						}
					}
					break;
				}
			}
		} catch ( IOException | ClassNotFoundException | NoSuchMethodException | InstantiationException |
				  IllegalAccessException | InvocationTargetException e ) {
			e.printStackTrace ( );
		}
		
		return count;
	}
	
	private URL[] getFileURLs ( File... files ) throws MalformedURLException {
		URL[] urls = new URL[ files.length ];
		
		for ( int i = 0 ; i < urls.length ; i++ ) {
			urls[ i ] = files[ i ].toURI ( ).toURL ( );
		}
		
		return urls;
	}
}