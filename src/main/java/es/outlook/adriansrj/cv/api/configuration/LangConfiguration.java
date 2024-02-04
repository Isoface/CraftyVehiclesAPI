package es.outlook.adriansrj.cv.api.configuration;

import es.outlook.adriansrj.cv.api.CraftyVehiclesPluginBase;
import es.outlook.adriansrj.cv.api.util.LangFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;

/**
 * @author AdrianSR / 2/2/2024 / 1:03 a. m.
 */
public final class LangConfiguration {
	
	// -- spawning
	
	public static final Entry SPAWN_CANNOT_PLACE_HERE = new Entry (
			"spawn.cannot-place-here" ,
			ChatColor.DARK_RED + "Cannot place this vehicle here!"
	);
	
	// -- fuel
	
	public static final Entry FUEL_TANK_FULL = new Entry (
			"fuel.tank-full" ,
			ChatColor.DARK_RED + "Vehicle tank is already full!"
	);
	
	/**
	 * @author AdrianSR / 2/2/2024 / 1:10 a. m.
	 */
	public static final class Entry {
		
		private final @NotNull String key;
		private final @NotNull String defaultValue;
		private @NotNull       String value;
		
		private Entry ( @NotNull String key , @NotNull String defaultValue ) {
			this.key          = key;
			this.defaultValue = defaultValue;
			this.value        = defaultValue;
		}
		
		public @NotNull String value ( ) {
			return value;
		}
		
		public @NotNull LangFormatter valueWithFormat ( boolean single ) {
			return single ? LangFormatter.single ( value ( ) )
					: LangFormatter.multi ( value ( ) );
		}
		
		private void load ( @NotNull ConfigurationSection section ) {
			String value = section.getString ( key );
			
			if ( StringUtils.isBlank ( value ) ) {
				this.value = defaultValue;
				
				// setting default
				section.set ( key , decompileColors ( defaultValue ) );
			} else {
				this.value = ChatColor.translateAlternateColorCodes ( '&' , value );
			}
		}
		
		private String decompileColors ( String string ) {
			char[] chars = string.toCharArray ( );
			
			for ( int i = 0 ; i < chars.length - 1 ; i++ ) {
				if ( chars[ i ] == ChatColor.COLOR_CHAR
						&& "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf ( chars[ i + 1 ] ) != -1 ) {
					chars[ i ] = '&';
				}
			}
			
			return new String ( chars );
		}
	}
	
	public static void load ( @NotNull CraftyVehiclesPluginBase plugin ) {
		File file = new File ( plugin.getDataFolder ( ) , "LanguageConfiguration.yml" );
		
		if ( !file.exists ( ) ) {
			file.getParentFile ( ).mkdirs ( );
			
			try {
				Files.createFile ( file.toPath ( ) );
			} catch ( IOException e ) {
				throw new IllegalStateException ( "couldn't generate language configuration file" , e );
			}
		}
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration ( file );
		
		for ( Field field : LangConfiguration.class.getFields ( ) ) {
			if ( Entry.class.isAssignableFrom ( field.getType ( ) ) ) {
				try {
					( ( Entry ) field.get ( null ) ).load ( yaml );
				} catch ( IllegalAccessException e ) {
					e.printStackTrace ( );
				}
			}
		}
		
		try {
			yaml.save ( file );
		} catch ( IOException e ) {
			e.printStackTrace ( );
		}
	}
}