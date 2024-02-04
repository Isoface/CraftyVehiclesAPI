package es.outlook.adriansrj.cv.api.vehicle.controller;

import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.interfaces.IDeyed;
import es.outlook.adriansrj.cv.api.util.reflection.EnumReflection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author AdrianSR / 28/12/2023 / 1:54 p. m.
 */
public class VehicleControllerProperties implements ConfigurationSectionWritable {
	
	public static VehicleControllerProperties load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		Map < String, Object > values = new HashMap <> ( );
		
		for ( Map.Entry < String, Object > entry : section.getValues ( false ).entrySet ( ) ) {
			Object value = entry.getValue ( );
			
			if ( value != null ) {
				values.put ( IDeyed.idCheck ( entry.getKey ( ) ) , value );
			}
		}
		
		return new VehicleControllerProperties ( values );
	}
	
	protected final @NotNull Map < String, Object > values = new HashMap <> ( );
	
	public VehicleControllerProperties ( @Nullable Map < String, Object > values ) {
		if ( values != null ) {
			for ( Map.Entry < String, Object > entry : values.entrySet ( ) ) {
				this.values.put ( IDeyed.idCheck ( entry.getKey ( ) ) , entry.getValue ( ) );
			}
		}
	}
	
	public VehicleControllerProperties ( ) {
		this ( null ); // empty
	}
	
	public Map < String, Object > getValues ( ) {
		return Collections.unmodifiableMap ( values );
	}
	
	public String getStringProperty ( @NotNull String key , @Nullable String defaultValue ) {
		return getProperty ( String.class , key , defaultValue );
	}
	
	public boolean getBooleanProperty ( @NotNull String key , boolean defaultValue ) {
		return getProperty ( Boolean.class , key , defaultValue );
	}
	
	public int getIntegerProperty ( @NotNull String key , int defaultValue ) {
		return getNumericProperty ( key , defaultValue ).intValue ( );
	}
	
	public float getFloatProperty ( @NotNull String key , float defaultValue ) {
		return getNumericProperty ( key , defaultValue ).floatValue ( );
	}
	
	public double getDoubleProperty ( @NotNull String key , double defaultValue ) {
		return getNumericProperty ( key , defaultValue ).doubleValue ( );
	}
	
	public Number getNumericProperty ( @NotNull String key , @Nullable Number defaultValue ) {
		return getProperty ( Number.class , key , defaultValue );
	}
	
	public < T extends Enum < T > > T getEnumProperty (
			@NotNull String key , @NotNull Class < T > enumClass , @Nullable T defaultValue ) {
		Object nameRaw = values.get ( key );
		
		if ( nameRaw instanceof String ) {
			T value = EnumReflection.getEnumConstant ( enumClass , ( String ) nameRaw );
			
			if ( value != null ) {
				return value;
			}
		}
		
		return defaultValue;
	}
	
	public < T > T getProperty ( @NotNull Class < T > type ,
			@NotNull String key , @Nullable T defaultValue ) {
		Object value = values.get ( key );
		
		return value != null && type.isAssignableFrom ( value.getClass ( ) )
				? type.cast ( value ) : defaultValue;
	}
	
	// -- common cases
	
	public float getMinFuelConsumptionOverride ( float original ) {
		return getFloatProperty ( "min-fuel-consumption" , original );
	}
	
	public float getMaxFuelConsumptionOverride ( float original ) {
		return getFloatProperty ( "max-fuel-consumption" , original );
	}
	
	// -- serialization
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		for ( Map.Entry < String, Object > entry : values.entrySet ( ) ) {
			section.set ( entry.getKey ( ) , entry.getValue ( ) );
		}
	}
}