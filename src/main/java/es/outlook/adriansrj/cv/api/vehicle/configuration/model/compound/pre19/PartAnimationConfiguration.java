package es.outlook.adriansrj.cv.api.vehicle.configuration.model.compound.pre19;

import es.outlook.adriansrj.cv.api.enums.EnumInterpolationMode;
import es.outlook.adriansrj.cv.api.enums.EnumLoopMode;
import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AdrianSR / 30/11/2023 / 11:52 a. m.
 */
@Getter
@AllArgsConstructor
@Builder
@ToString
public class PartAnimationConfiguration implements ConfigurationSectionWritable {
	
	public static PartAnimationConfiguration load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
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
		List < PartAnimationKeyframeConfiguration > keyframes = new ArrayList <> ( );
		
		if ( keyframesSection != null ) {
			for ( String key : keyframesSection.getKeys ( false ) ) {
				ConfigurationSection keyframeSection = keyframesSection.getConfigurationSection ( key );
				PartAnimationKeyframeConfiguration keyframe = keyframeSection != null
						? PartAnimationKeyframeConfiguration.load ( keyframeSection ) : null;
				
				if ( keyframe != null ) {
					keyframes.add ( keyframe );
				}
			}
		}
		
		if ( keyframes.size ( ) == 0 ) {
			throw new InvalidConfigurationException ( "at least one keyframe must be set" );
		}
		
		return new PartAnimationConfiguration ( interpolationMode , loopMode , keyframes );
	}
	
	private final @NotNull           EnumInterpolationMode                       interpolationMode;
	private final @NotNull           EnumLoopMode                                loopMode;
	private final @NotNull @Singular List < PartAnimationKeyframeConfiguration > keyframes;
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
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
	}
}