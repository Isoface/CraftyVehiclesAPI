package es.outlook.adriansrj.cv.api.vehicle.configuration.model;

import com.google.common.base.Preconditions;
import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.interfaces.Validable;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.vehicle.VehicleState;
import es.outlook.adriansrj.cv.api.vehicle.configuration.data.DataParser;
import es.outlook.adriansrj.cv.api.vehicle.configuration.data.DataParsers;
import gnu.trove.set.hash.THashSet;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * @author AdrianSR / 24/12/2023 / 5:16 p. m.
 */
@Getter
public class VehicleParticleConfiguration implements ConfigurationSectionWritable, Validable {
	
	public static VehicleParticleConfiguration load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		Particle type = ConfigurationUtil.loadEnum (
				Particle.class , section , Constants.Key.TYPE );
		
		if ( type == null ) {
			throw new InvalidConfigurationException ( "invalid particle type" );
		}
		
		Vector3D offset = ConfigurationUtil.loadLibraryObject (
				Vector3D.class , section , Constants.Key.OFFSET );
		
		if ( offset == null ) {
			throw new InvalidConfigurationException ( "particle requires offset to be set" );
		}
		
		int   delay      = section.getInt ( Constants.Key.DELAY );
		int   count      = section.getInt ( Constants.Key.COUNT );
		float dispersion = ( float ) section.getDouble ( Constants.Key.DISPERSION );
		
		// parsing data
		Object               data        = null;
		ConfigurationSection dataSection = section.getConfigurationSection ( Constants.Key.DATA );
		
		if ( dataSection != null ) {
			DataParser parser = DataParsers.matchParser ( dataSection );
			
			if ( parser != null ) {
				data = parser.parse ( dataSection );
			}
		}
		
		// states
		Set < String > statesToApply       = new THashSet <> ( );
		String         statesToApplyString = section.getString ( Constants.Key.STATES_TO_APPLY );
		
		if ( StringUtils.isNotBlank ( statesToApplyString ) ) {
			String[] states = statesToApplyString.split ( "," );
			
			Arrays.stream ( states )
					.filter ( StringUtils :: isNotBlank )
					.map ( string -> string.toLowerCase ( ).trim ( ) )
					.forEach ( statesToApply :: add );
		}
		
		return new VehicleParticleConfiguration (
				type , offset , delay , count , dispersion , data , statesToApply );
	}
	
	private final @NotNull  Particle type;
	private final @NotNull  Vector3D offset;
	private final           int      delay; // ticks
	private final           int      count;
	private final           float    dispersion;
	private final @Nullable Object   data;
	
	// states this particle applies to
	private final Set < String > statesToApply = new THashSet <> ( );
	
	@Builder
	public VehicleParticleConfiguration ( @NotNull Particle type , @NotNull Vector3D offset ,
			int delay , int count , float dispersion , @Nullable Object data ,
			@Nullable Collection < String > statesToApply ) {
		Preconditions.checkArgument ( delay >= 0 , "delay cannot be negative" );
		Preconditions.checkArgument ( count > 0 , "count must be > 0" );
		Preconditions.checkArgument ( dispersion >= 0.0F , "dispersion cannot be negative" );
		
		this.type       = type;
		this.offset     = offset;
		this.delay      = delay;
		this.count      = count;
		this.dispersion = dispersion;
		this.data       = data;
		
		if ( statesToApply != null ) {
			this.statesToApply.addAll ( statesToApply );
		}
	}
	
	@Override
	public boolean isValid ( ) {
		Class < ? > dataType = type.getDataType ( );
		
		return dataType == Void.class || data != null && dataType.isAssignableFrom ( data.getClass ( ) );
	}
	
	public < T > @Nullable T getDataAs ( Class < T > type ) {
		return type.cast ( data );
	}
	
	public boolean appliesTo ( @NotNull VehicleState state ) {
		for ( String name : statesToApply ) {
			if ( state.getName ( ).equalsIgnoreCase ( name ) ) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		ConfigurationUtil.writeEnum ( type , section , Constants.Key.TYPE );
		
		ConfigurationUtil.writeLibraryObject (
				Vector3D.class , offset ,
				section.createSection ( Constants.Key.OFFSET )
		);
		
		section.set ( Constants.Key.DELAY , delay );
		section.set ( Constants.Key.COUNT , count );
		section.set ( Constants.Key.DISPERSION , dispersion );
		
		if ( data != null ) {
			DataParser parser = DataParsers.getParser ( data.getClass ( ) );
			
			if ( parser != null ) {
				parser.write ( data , section.createSection ( Constants.Key.DATA ) );
			}
		}
		
		if ( statesToApply.size ( ) > 0 ) {
			section.set ( Constants.Key.STATES_TO_APPLY , String.join ( "," , statesToApply ) );
		} else {
			section.set ( Constants.Key.STATES_TO_APPLY , null );
		}
	}
}