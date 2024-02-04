package es.outlook.adriansrj.cv.api.vehicle;

import com.google.common.base.Preconditions;
import es.outlook.adriansrj.cv.api.util.Constants;
import gnu.trove.map.hash.THashMap;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author AdrianSR / 30/11/2023 / 12:44 p. m.
 */
public final class VehicleState {
	
	private static final Map < String, VehicleState > REGISTRY = new THashMap <> ( );
	
	// -- defaults
	
	/**
	 * Vehicle is not moving or turning.
	 */
	public static final VehicleState IDLE = new VehicleState (
			"idle" , "Vehicle is not moving/turning" ,
			true
	);
	
	/**
	 * Vehicle is moving.
	 */
	public static final VehicleState MOVING = new VehicleState (
			"moving" , "Vehicle is just moving" ,
			true
	);
	
	/**
	 * Vehicle is moving.
	 */
	public static final VehicleState MOVING_BACKWARDS = new VehicleState (
			"moving_backwards" , "Vehicle is just moving" ,
			true
	);
	
	/**
	 * Vehicle is increasing height.
	 */
	public static final VehicleState INCREASING_HEIGHT = new VehicleState (
			"increasing_height" , "Vehicle is increasing height" ,
			true
	);
	
	/**
	 * Vehicle is decreasing height.
	 */
	public static final VehicleState DECREASING_HEIGHT = new VehicleState (
			"decreasing_height" , "Vehicle is decreasing height" ,
			true
	);
	
	/**
	 * Vehicle is turning to the left.
	 */
	public static final VehicleState TURNING_LEFT = new VehicleState (
			"turning_left" , "Vehicle is just turning to the left" ,
			true
	);
	
	/**
	 * Vehicle is turning to the right.
	 */
	public static final VehicleState TURNING_RIGHT = new VehicleState (
			"turning_right" , "Vehicle is just turning to the right" ,
			true
	);
	
	/**
	 * Vehicle is moving and turning to the left at the same time.
	 */
	public static final VehicleState MOVING_TURNING_LEFT = new VehicleState (
			"moving_turning_left" ,
			"Vehicle is moving and turning to the left at the same time" ,
			true
	);
	
	/**
	 * Vehicle is moving and turning to the right at the same time.
	 */
	public static final VehicleState MOVING_TURNING_RIGHT = new VehicleState (
			"moving_turning_right" ,
			"Vehicle is moving and turning to the right at the same time" ,
			true
	);
	
	/**
	 * Vehicle is moving and turning to the left at the same time.
	 */
	public static final VehicleState MOVING_BACKWARDS_TURNING_LEFT = new VehicleState (
			"moving_backwards_turning_left" ,
			"Vehicle is moving and turning to the left at the same time" ,
			true
	);
	
	/**
	 * Vehicle is moving and turning to the right at the same time.
	 */
	public static final VehicleState MOVING_BACKWARDS_TURNING_RIGHT = new VehicleState (
			"moving_backwards_turning_right" ,
			"Vehicle is moving and turning to the right at the same time" ,
			true
	);
	
	/**
	 * Vehicle is increasing height and turning to the left at the same time.
	 */
	public static final VehicleState INCREASING_HEIGHT_TURNING_LEFT = new VehicleState (
			"increasing_height_turning_left" ,
			"Vehicle is increasing height and turning to the left at the same time" ,
			true
	);
	
	/**
	 * Vehicle is increasing height and turning to the right at the same time.
	 */
	public static final VehicleState INCREASING_HEIGHT_TURNING_RIGHT = new VehicleState (
			"increasing_height_turning_right" ,
			"Vehicle is increasing height and turning to the right at the same time" ,
			true
	);
	
	/**
	 * Vehicle is decreasing height and turning to the left at the same time.
	 */
	public static final VehicleState DECREASING_HEIGHT_TURNING_LEFT = new VehicleState (
			"decreasing_height_turning_left" ,
			"Vehicle is decreasing height and turning to the left at the same time" ,
			true
	);
	
	/**
	 * Vehicle is decreasing height and turning to the right at the same time.
	 */
	public static final VehicleState DECREASING_HEIGHT_TURNING_RIGHT = new VehicleState (
			"decreasing_height_turning_right" ,
			"Vehicle is decreasing height and turning to the right at the same time" ,
			true
	);
	
	// -- registration
	
	static {
		// registering predefined ones
		for ( Field field : VehicleState.class.getFields ( ) ) {
			if ( Modifier.isStatic ( field.getModifiers ( ) )
					&& VehicleState.class.isAssignableFrom ( field.getType ( ) ) ) {
				try {
					VehicleState state = ( VehicleState ) field.get ( null );
					
					REGISTRY.put ( state.getName ( ) , state );
				} catch ( IllegalAccessException e ) {
					throw new IllegalStateException ( e );
				}
			}
		}
	}
	
	public static Collection < VehicleState > getValues ( ) {
		return Collections.unmodifiableCollection ( REGISTRY.values ( ) );
	}
	
	public static void register ( @NotNull VehicleState state ) {
		VehicleState predefined = REGISTRY.get ( state.getName ( ) );
		
		if ( predefined != null && predefined.isPredefined ( ) ) {
			throw new IllegalArgumentException ( "cannot override a pre-defined state" );
		}
		
		REGISTRY.put ( state.getName ( ) , state );
	}
	
	public static void unregister ( @NotNull VehicleState state ) {
		if ( state.isPredefined ( ) ) {
			throw new IllegalArgumentException ( "cannot unregister override a pre-defined" );
		}
		
		REGISTRY.remove ( state.getName ( ) );
	}
	
	// -- construction
	
	private final @NotNull String  name;
	private final          boolean predefined;
	private final @NotNull String  description;
	
	public VehicleState ( @NotNull String name , @NotNull String description ) {
		this ( name , description , false );
	}
	
	private VehicleState ( @NotNull String name , @NotNull String description , boolean predefined ) {
		Preconditions.checkArgument (
				StringUtils.isNotBlank ( name ) ,
				"name cannot be blank"
		);
		Preconditions.checkArgument (
				StringUtils.isNotBlank ( description ) ,
				"description cannot be blank"
		);
		Preconditions.checkArgument (
				name.matches ( Constants.VALID_STATE_NAME_PATTERN ) ,
				"invalid name. must match: " + Constants.VALID_STATE_NAME_PATTERN
		);
		
		this.name        = name;
		this.description = description;
		this.predefined  = predefined;
	}
	
	public @NotNull String getName ( ) {
		return name;
	}
	
	public @NotNull String getDescription ( ) {
		return description;
	}
	
	public boolean isPredefined ( ) {
		return predefined;
	}
	
	@Override
	public String toString ( ) {
		return "VehicleState{" +
				"name='" + name + '\'' +
				"description='" + description + '\'' +
				"predefined='" + predefined + '\'' +
				'}';
	}
	
	@Override
	public boolean equals ( Object o ) {
		if ( this == o ) { return true; }
		if ( o == null || getClass ( ) != o.getClass ( ) ) { return false; }
		
		VehicleState state = ( VehicleState ) o;
		
		return name.equals ( state.name );
	}
	
	@Override
	public int hashCode ( ) {
		return name.hashCode ( );
	}
}