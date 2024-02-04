package es.outlook.adriansrj.cv.api.vehicle.configuration.model.compound.pre19;

import es.outlook.adriansrj.cv.api.enums.EnumStandSlot;
import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.util.reflection.EnumReflection;
import gnu.trove.map.hash.THashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author AdrianSR / 23/11/2023 / 12:55 p. m.
 */
@Getter
@AllArgsConstructor
@Builder
public class PartConfiguration implements ConfigurationSectionWritable {
	
	public static PartConfiguration load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		Map < EnumStandSlot, PartTextureConfiguration > textures   = loadTextures ( section );
		Map < String, PartAnimationConfiguration >      animations = loadAnimations ( section );
		
		boolean small = section.getBoolean ( Constants.Key.SMALL );
		Vector3D offset = ConfigurationUtil.loadLibraryObject (
				Vector3D.class , section , Constants.Key.OFFSET );
		
		return new PartConfiguration (
				textures , animations , small , offset
		);
	}
	
	private static Map < EnumStandSlot, PartTextureConfiguration > loadTextures ( ConfigurationSection root )
			throws InvalidConfigurationException {
		ConfigurationSection texturesSection = root
				.getConfigurationSection ( Constants.Key.TEXTURES );
		
		if ( texturesSection == null ) {
			throw new InvalidConfigurationException ( "invalid textures" );
		}
		
		Map < EnumStandSlot, PartTextureConfiguration > textures = new THashMap <> ( );
		
		for ( String key : texturesSection.getKeys ( false ) ) {
			EnumStandSlot slot = EnumReflection.getEnumConstant (
					EnumStandSlot.class , key.trim ( ).toUpperCase ( ) );
			ConfigurationSection textureSection = texturesSection
					.getConfigurationSection ( key );
			
			if ( slot != null && textureSection != null ) {
				textures.put ( slot , PartTextureConfiguration.load ( textureSection ) );
			}
		}
		
		if ( textures.size ( ) == 0 ) {
			throw new InvalidConfigurationException ( "at least one texture must be set" );
		}
		
		return textures;
	}
	
	private static Map < String, PartAnimationConfiguration > loadAnimations ( ConfigurationSection root )
			throws InvalidConfigurationException {
		Map < String, PartAnimationConfiguration > animations = new THashMap <> ( );
		ConfigurationSection animationsSection = root
				.getConfigurationSection ( Constants.Key.ANIMATIONS );
		
		if ( animationsSection == null ) {
			return animations;
		}
		
		for ( String key : animationsSection.getKeys ( false ) ) {
			ConfigurationSection section = animationsSection
					.getConfigurationSection ( key );
			PartAnimationConfiguration animation = section != null ?
					PartAnimationConfiguration.load ( section ) : null;
			
			if ( animation != null ) {
				animations.put ( key.trim ( ).toLowerCase ( ) , animation );
			}
		}
		
		return animations;
	}
	
	private final @NotNull @Singular Map < EnumStandSlot, PartTextureConfiguration > textures;
	private final @NotNull @Singular Map < String, PartAnimationConfiguration >      animations;
	private final                    boolean                                         small;
	private final @Nullable          Vector3D                                        offset;
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.SMALL , small );
		
		if ( offset != null ) {
			ConfigurationUtil.writeLibraryObject (
					Vector3D.class , offset ,
					section.createSection ( Constants.Key.OFFSET )
			);
		}
		
		// textures
		ConfigurationSection texturesSection = section.createSection ( Constants.Key.TEXTURES );
		
		for ( Map.Entry < EnumStandSlot, PartTextureConfiguration > entry :
				textures.entrySet ( ) ) {
			entry.getValue ( ).write ( texturesSection.createSection ( entry.getKey ( ).name ( ) ) );
		}
		
		// animations
		if ( animations.size ( ) > 0 ) {
			ConfigurationSection animationsSection = section.createSection ( Constants.Key.ANIMATIONS );
			
			for ( Map.Entry < String, PartAnimationConfiguration > entry : animations.entrySet ( ) ) {
				entry.getValue ( ).write ( animationsSection.createSection (
						entry.getKey ( ).toLowerCase ( ).trim ( ) )
				);
			}
		} else {
			section.set ( Constants.Key.ANIMATIONS , null );
		}
	}
}