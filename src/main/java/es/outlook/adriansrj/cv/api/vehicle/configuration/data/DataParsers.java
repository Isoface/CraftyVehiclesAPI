package es.outlook.adriansrj.cv.api.vehicle.configuration.data;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author AdrianSR / 28/1/2024 / 12:11 a. m.
 */
public final class DataParsers {
	
	private static final Map < String, DataParser >      BY_IDENTIFIER = new HashMap <> ( );
	private static final Map < Class < ? >, DataParser > BY_TYPE       = new HashMap <> ( );
	
	static {
		register ( new MaterialParser ( ) );
		register ( new ItemStackParser ( ) );
		register ( new BlockDataParser ( ) );
		register ( new DustOptionsParser ( ) );
		register ( new HeadTextureParser ( ) );
		register ( new BannerStyleParser ( ) );
	}
	
	public static DataParser getParser ( @NotNull String identifier ) {
		return BY_IDENTIFIER.get ( identifier.trim ( ).toLowerCase ( ) );
	}
	
	public static DataParser getParser ( @NotNull Class < ? > type ) {
		for ( Map.Entry < Class < ? >, DataParser > entry : BY_TYPE.entrySet ( ) ) {
			if ( entry.getKey ( ).isAssignableFrom ( type ) ) {
				return entry.getValue ( );
			}
		}
		
		return null;
	}
	
	public static DataParser matchParser ( @NotNull ConfigurationSection section ) {
		return DataParser.getParser ( section );
	}
	
	public static void register ( @NotNull DataParser parser ) {
		BY_IDENTIFIER.put ( parser.getIdentifier ( ).toLowerCase ( ).trim ( ) , parser );
		BY_TYPE.put ( parser.getType ( ) , parser );
	}
}