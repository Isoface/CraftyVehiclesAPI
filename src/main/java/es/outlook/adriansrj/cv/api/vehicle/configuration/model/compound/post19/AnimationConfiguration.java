package es.outlook.adriansrj.cv.api.vehicle.configuration.model.compound.post19;

import es.outlook.adriansrj.cv.api.enums.EnumInterpolationMode;
import es.outlook.adriansrj.cv.api.enums.EnumLoopMode;
import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.interfaces.Named;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.vehicle.VehicleState;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author AdrianSR / 30/11/2023 / 11:52 a. m.
 */
@Getter
@ToString
public class AnimationConfiguration implements ConfigurationSectionWritable, Named {
	
	public static AnimationConfiguration load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		String name = Named.loadName ( section );
		
		EnumInterpolationMode interpolationMode = ConfigurationUtil.loadEnum (
				EnumInterpolationMode.class , section , Constants.Key.INTERPOLATION_MODE );
		EnumLoopMode loopMode = ConfigurationUtil.loadEnum (
				EnumLoopMode.class , section , Constants.Key.LOOP_MODE );
		
		if ( interpolationMode == null ) {
			throw new InvalidConfigurationException ( "invalid interpolation mode" );
		} else if ( loopMode == null ) {
			throw new InvalidConfigurationException ( "invalid loop mode" );
		}
		
		ConfigurationSection keyframesSection = section
				.getConfigurationSection ( Constants.Key.KEYFRAMES );
		List < AnimationKeyframeConfiguration > keyframes = new ArrayList <> ( );
		
		if ( keyframesSection != null ) {
			for ( String key : keyframesSection.getKeys ( false ) ) {
				ConfigurationSection keyframeSection = keyframesSection.getConfigurationSection ( key );
				AnimationKeyframeConfiguration keyframe = keyframeSection != null
						? AnimationKeyframeConfiguration.load ( keyframeSection ) : null;
				
				if ( keyframe != null ) {
					keyframes.add ( keyframe );
				}
			}
		}
		
		if ( keyframes.size ( ) == 0 ) {
			throw new InvalidConfigurationException ( "at least one keyframe must be set" );
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
		
		return new AnimationConfiguration (
				name , interpolationMode , loopMode , keyframes , statesToApply );
	}
	
	private final @NotNull           String                                  name;
	private final @NotNull           EnumInterpolationMode                   interpolationMode;
	private final @NotNull           EnumLoopMode                            loopMode;
	private final @NotNull @Singular List < AnimationKeyframeConfiguration > keyframes;
	
	// states this animation applies to
	private final @NotNull Set < String > statesToApply = new THashSet <> ( );
	
	public AnimationConfiguration ( @NotNull String name , @NotNull EnumInterpolationMode interpolationMode ,
			@NotNull EnumLoopMode loopMode , @NotNull List < AnimationKeyframeConfiguration > keyframes ,
			@NotNull Collection < String > statesToApply ) {
		this.name              = name;
		this.interpolationMode = interpolationMode;
		this.loopMode          = loopMode;
		this.keyframes         = keyframes;
		
		this.statesToApply.addAll ( statesToApply );
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
		Named.writeName ( this , section );
		
		ConfigurationUtil.writeEnum (
				interpolationMode , section , Constants.Key.INTERPOLATION_MODE );
		ConfigurationUtil.writeEnum (
				loopMode , section , Constants.Key.LOOP_MODE );
		
		// writing keyframes
		ConfigurationSection keyframesSection = section
				.createSection ( Constants.Key.KEYFRAMES );
		
		for ( int i = 0 ; i < keyframes.size ( ) ; i++ ) {
			keyframes.get ( i ).write (
					keyframesSection.createSection ( "keyframe-" + i ) );
		}
		
		// states
		if ( statesToApply.size ( ) > 0 ) {
			section.set ( Constants.Key.STATES_TO_APPLY , String.join ( "," , statesToApply ) );
		} else {
			section.set ( Constants.Key.STATES_TO_APPLY , null );
		}
	}
}