package es.outlook.adriansrj.cv.api;

import es.outlook.adriansrj.cv.api.configuration.Configuration;
import es.outlook.adriansrj.cv.api.configuration.LangConfiguration;
import es.outlook.adriansrj.cv.api.handler.PlayerWrapperHandler;
import es.outlook.adriansrj.cv.api.handler.PluginHandler;
import es.outlook.adriansrj.cv.api.handler.VehicleHandler;
import es.outlook.adriansrj.cv.api.service.BlockInfoService;
import es.outlook.adriansrj.cv.api.service.PacketService;
import es.outlook.adriansrj.cv.api.service.Service;
import es.outlook.adriansrj.cv.api.service.TexturedHeadService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author AdrianSR / 23/12/2023 / 1:08 p. m.
 */
public final class CraftyVehicles {
	
	private static CraftyVehiclesPluginBase PLUGIN_INSTANCE;
	
	private static final Map < Class < ? extends PluginHandler >, PluginHandler > HANDLER_MAP = new HashMap <> ( );
	private static final Map < Class < ? extends Service >, Service >             SERVICE_MAP = new HashMap <> ( );
	
	public static @NotNull CraftyVehiclesPluginBase getPlugin ( ) {
		if ( PLUGIN_INSTANCE == null ) {
			throw new IllegalStateException ( "api not initialized" );
		}
		
		return PLUGIN_INSTANCE;
	}
	
	public static void initalize ( @NotNull CraftyVehiclesPluginBase plugin ) {
		if ( PLUGIN_INSTANCE != null ) {
			throw new IllegalStateException ( "api already initialized" );
		}
		
		PLUGIN_INSTANCE = plugin;
		
		// loading configurations
		Configuration.load ( plugin );
		LangConfiguration.load ( plugin );
	}
	
	public static void disable ( ) {
		if ( PLUGIN_INSTANCE == null ) {
			return; // never initialized
		}
		
		for ( PluginHandler handler : HANDLER_MAP.values ( ) ) {
			handler.onPluginDisable ( );
		}
	}
	
	// -- handler
	
	public static @Nullable < T extends PluginHandler > T getHandler ( @NotNull Class < T > type ) {
		return type.cast ( HANDLER_MAP.get ( type ) );
	}
	
	public static @NotNull PlayerWrapperHandler getPlayerWrapperHandler ( ) {
		return getVitalHandler ( PlayerWrapperHandler.class );
	}
	
	public static @NotNull VehicleHandler getVehicleHandler ( ) {
		return getVitalHandler ( VehicleHandler.class );
	}
	
	public static void registerHandler ( @NotNull Class < ? extends PluginHandler > type ,
			@NotNull PluginHandler handler ) {
		if ( HANDLER_MAP.containsKey ( type ) ) {
			throw new IllegalArgumentException ( "handler already registered" );
		}
		
		HANDLER_MAP.put ( type , handler );
	}
	
	private static @NotNull < T extends PluginHandler > T getVitalHandler ( @NotNull Class < T > type ) {
		T handler = getHandler ( type );
		
		if ( handler == null ) {
			throw new IllegalStateException (
					"severe error: vital handler " + type.getSimpleName ( ) + " couldn't be initialized" );
		}
		
		return handler;
	}
	
	// -- service
	
	public static @Nullable < T extends Service > T getService ( @NotNull Class < T > type ) {
		return type.cast ( SERVICE_MAP.get ( type ) );
	}
	
	public static @NotNull BlockInfoService getBlockInfoService ( ) {
		return getVitalService ( BlockInfoService.class );
	}
	
	public static @NotNull PacketService getPacketService ( ) {
		return getVitalService ( PacketService.class );
	}
	
	public static @NotNull TexturedHeadService getTexturedHeadService ( ) {
		return getVitalService ( TexturedHeadService.class );
	}
	
	public static void registerService ( @NotNull Class < ? extends Service > type ,
			@NotNull Service service ) {
		if ( SERVICE_MAP.containsKey ( type ) ) {
			throw new IllegalArgumentException ( "service already registered" );
		}
		
		SERVICE_MAP.put ( type , service );
	}
	
	private static @NotNull < T extends Service > T getVitalService ( @NotNull Class < T > type ) {
		T service = getService ( type );
		
		if ( service == null ) {
			throw new IllegalStateException (
					"severe error: vital service " + type.getSimpleName ( ) + " couldn't be initialized" );
		}
		
		return service;
	}
}