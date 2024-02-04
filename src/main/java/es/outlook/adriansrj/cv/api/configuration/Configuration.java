package es.outlook.adriansrj.cv.api.configuration;

import es.outlook.adriansrj.cv.api.CraftyVehiclesPluginBase;
import es.outlook.adriansrj.cv.api.util.reflection.EnumReflection;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;

/**
 * @author AdrianSR / 1/2/2024 / 12:34 a. m.
 */
public final class Configuration {
	
	// -- ownership
	
	public static final Entry OWNERSHIP_ONLY_OWNER
			= new Entry ( "ownership.only-owner-can-operate" , false );
	
	// -- fuel
	
	public static final Entry FUEL_ENABLE
			= new Entry ( "fuel-system.enable" , true );
	
	public static final Entry FUEL_BYPASS_CREATIVE
			= new Entry ( "fuel-system.bypass-creative-mode" , true );
	
	// -- pickup
	
	public static final Entry PICKUP_ENABLE
			= new Entry ( "pickup-system.enable" , true );
	
	public static final Entry PICKUP_PARTICLE_ENABLE
			= new Entry ( "pickup-system.particle.enable" , true );
	
	public static final Entry PICKUP_PARTICLE_TYPE
			= new Entry ( "pickup-system.particle.type" , Particle.CLOUD.name ( ) );
	
	public static final Entry PICKUP_PARTICLE_DISPERSION
			= new Entry ( "pickup-system.particle.dispersion" , 1.5F );
	
	public static final Entry PICKUP_PARTICLE_AMOUNT
			= new Entry ( "pickup-system.particle.amount" , 20 );
	
	public static final Entry PICKUP_SOUND_ENABLE
			= new Entry ( "pickup-system.sound.enable" , true );
	
	public static final Entry PICKUP_SOUND_TYPE
			= new Entry ( "pickup-system.sound.type" , Sound.ENTITY_CHICKEN_EGG.name ( ) );
	
	public static final Entry PICKUP_SOUND_VOLUME
			= new Entry ( "pickup-system.sound.volume" , 1.5F );
	
	public static final Entry PICKUP_SOUND_PITCH
			= new Entry ( "pickup-system.sound.pitch" , 1.0F );
	
	/**
	 * @author AdrianSR / 1/2/2024 / 12:36 a. m.
	 */
	public static final class Entry {
		
		private final @NotNull String key;
		private final @NotNull Object defaultValue;
		private @Nullable      Object value;
		
		private Entry ( @NotNull String key , @NotNull Object defaultValue ) {
			this.key          = key;
			this.defaultValue = defaultValue;
			this.value        = defaultValue;
		}
		
		public @NotNull Object value ( ) {
			return value != null ? value : defaultValue;
		}
		
		public @NotNull < T > T value ( @NotNull Class < T > type ) {
			if ( !compatible ( type ) ) {
				throw new IllegalArgumentException ( "type mismatch" );
			}
			
			return type.cast ( value != null ? value : defaultValue );
		}
		
		public boolean booleanValue ( ) {
			return value ( Boolean.class );
		}
		
		public @NotNull Number numericValue ( ) {
			return value ( Number.class );
		}
		
		public int intValue ( ) {
			return numericValue ( ).intValue ( );
		}
		
		public long longValue ( ) {
			return numericValue ( ).longValue ( );
		}
		
		public double doubleValue ( ) {
			return numericValue ( ).doubleValue ( );
		}
		
		public float floatValue ( ) {
			return numericValue ( ).floatValue ( );
		}
		
		public @NotNull String stringValue ( ) {
			return value ( String.class );
		}
		
		public @Nullable < T extends Enum < T > > T enumValue ( Class < T > type ) {
			return EnumReflection.getEnumConstant ( type , stringValue ( ) );
		}
		
		private void load ( @NotNull ConfigurationSection section ) {
			value = section.get ( key );
			
			if ( value == null || !compatible ( value.getClass ( ) ) ) {
				value = defaultValue;
				
				// must set default value
				section.set ( key , defaultValue );
			}
		}
		
		private boolean compatible ( @NotNull Class < ? > type ) {
			return defaultValue.getClass ( ).isAssignableFrom ( type )
					|| ( defaultValue instanceof Number && Number.class.isAssignableFrom ( type ) );
		}
	}
	
	public static void load ( @NotNull CraftyVehiclesPluginBase plugin ) {
		File file = new File ( plugin.getDataFolder ( ) , "BaseConfiguration.yml" );
		
		if ( !file.exists ( ) ) {
			file.getParentFile ( ).mkdirs ( );
			
			try {
				Files.createFile ( file.toPath ( ) );
			} catch ( IOException e ) {
				throw new IllegalStateException ( "couldn't generate base configuration file" , e );
			}
		}
		
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration ( file );
		
		for ( Field field : Configuration.class.getFields ( ) ) {
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