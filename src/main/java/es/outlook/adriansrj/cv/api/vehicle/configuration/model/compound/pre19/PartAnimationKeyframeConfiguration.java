package es.outlook.adriansrj.cv.api.vehicle.configuration.model.compound.pre19;

import es.outlook.adriansrj.cv.api.enums.EnumRotableLimb;
import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.util.reflection.EnumReflection;
import gnu.trove.map.hash.THashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author AdrianSR / 30/11/2023 / 11:52 a. m.
 */
@Getter
@AllArgsConstructor
@Builder
@ToString
public class PartAnimationKeyframeConfiguration implements ConfigurationSectionWritable {
	
	public static PartAnimationKeyframeConfiguration load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		long                              duration  = section.getLong ( Constants.Key.DURATION );
		Map < EnumRotableLimb, Vector3D > rotations = new THashMap <> ( );
		
		if ( duration < 0L ) {
			throw new InvalidConfigurationException ( "duration cannot be negative" );
		}
		
		// offset
		Vector3D offset = ConfigurationUtil.loadLibraryObject (
				Vector3D.class , section , Constants.Key.OFFSET
		);
		
		// rotations
		ConfigurationSection rotationsSection = section
				.getConfigurationSection ( Constants.Key.ROTATIONS );
		
		if ( rotationsSection != null ) {
			for ( String key : rotationsSection.getKeys ( false ) ) {
				EnumRotableLimb limb = EnumReflection.getEnumConstant (
						EnumRotableLimb.class , key.trim ( ).toUpperCase ( ) );
				Vector3D rotation = ConfigurationUtil.loadLibraryObject (
						Vector3D.class , rotationsSection , key );
				
				if ( limb != null && rotation != null ) {
					rotations.put ( limb , rotation );
				}
			}
		}
		
		if ( offset == null && rotations.size ( ) == 0 ) {
			throw new InvalidConfigurationException ( "at least one rotation, or offset must be set" );
		}
		
		return new PartAnimationKeyframeConfiguration ( duration , offset , rotations );
	}
	
	/** duration in ticks */
	private final                    long                              duration;
	private final @Nullable          Vector3D                          offset;
	private final @NotNull @Singular Map < EnumRotableLimb, Vector3D > rotations;
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.DURATION , duration );
		
		if ( offset != null ) {
			ConfigurationUtil.writeLibraryObject (
					Vector3D.class , offset ,
					section.createSection ( Constants.Key.OFFSET )
			);
		} else {
			section.set ( Constants.Key.OFFSET , null );
		}
		
		if ( rotations.size ( ) > 0 ) {
			ConfigurationSection rotationsSection = section
					.createSection ( Constants.Key.ROTATIONS );
			
			for ( Map.Entry < EnumRotableLimb, Vector3D > entry : rotations.entrySet ( ) ) {
				EnumRotableLimb limb     = entry.getKey ( );
				Vector3D        rotation = entry.getValue ( );
				
				ConfigurationUtil.writeLibraryObject (
						Vector3D.class , rotation ,
						rotationsSection.createSection ( limb.name ( ) )
				);
			}
		} else {
			section.set ( Constants.Key.ROTATIONS , null );
		}
	}
}