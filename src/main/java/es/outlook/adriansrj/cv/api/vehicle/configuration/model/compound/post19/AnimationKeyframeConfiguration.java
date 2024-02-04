package es.outlook.adriansrj.cv.api.vehicle.configuration.model.compound.post19;

import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import lombok.Builder;
import lombok.ToString;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author AdrianSR / 30/11/2023 / 11:52 a. m.
 */
@ToString
public class AnimationKeyframeConfiguration implements ConfigurationSectionWritable {
	
	public static AnimationKeyframeConfiguration load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		int duration = section.getInt ( Constants.Key.DURATION );
		
		if ( duration < 0 ) {
			throw new InvalidConfigurationException ( "duration cannot be negative" );
		}
		
		Map < UUID, Vector3D > translations = loadVectors ( section , Constants.Key.TRANSLATIONS );
		Map < UUID, Vector3D > rotations    = loadVectors ( section , Constants.Key.ROTATIONS );
		Map < UUID, Vector3D > scales       = loadVectors ( section , Constants.Key.SCALES );
		
		if ( translations.isEmpty ( ) && rotations.isEmpty ( ) && scales.isEmpty ( ) ) {
			throw new InvalidConfigurationException ( "at least one transformation is required" );
		}
		
		return new AnimationKeyframeConfiguration ( duration , translations , rotations , scales );
	}
	
	private static Map < UUID, Vector3D > loadVectors (
			ConfigurationSection root , String sectionName ) throws InvalidConfigurationException {
		Map < UUID, Vector3D > map = new HashMap <> ( );
		
		for ( ConfigurationSection section : ConfigurationUtil.getConfigurationSectionsAfter (
				root , sectionName , false ) ) {
			UUID identifier = ConfigurationUtil.loadLibrarySingleEntryObject (
					UUID.class , section , Constants.Key.IDENTIFIER );
			
			if ( identifier == null ) {
				continue;
			}
			
			ConfigurationSection valueSection = section.getConfigurationSection ( Constants.Key.VALUE );
			
			if ( valueSection != null ) {
				Vector3D value = ConfigurationUtil.loadLibraryObject ( Vector3D.class , valueSection );
				
				if ( value != null ) {
					map.put ( identifier , value );
				}
			}
		}
		
		return map;
	}
	
	/** duration in ticks */
	private final int duration;
	
	private final Map < UUID, Vector3D > translations = new HashMap <> ( );
	private final Map < UUID, Vector3D > rotations    = new HashMap <> ( );
	private final Map < UUID, Vector3D > scales       = new HashMap <> ( );
	
	@Builder
	public AnimationKeyframeConfiguration ( int duration ,
			@Nullable Map < UUID, Vector3D > translations ,
			@Nullable Map < UUID, Vector3D > rotations ,
			@Nullable Map < UUID, Vector3D > scales ) {
		this.duration = duration;
		
		if ( translations != null ) { this.translations.putAll ( translations ); }
		if ( rotations != null ) { this.rotations.putAll ( rotations ); }
		if ( scales != null ) { this.scales.putAll ( scales ); }
	}
	
	public int getDuration ( ) {
		return duration;
	}
	
	public Map < UUID, Vector3D > getTranslations ( ) {
		return Collections.unmodifiableMap ( translations );
	}
	
	public @Nullable Vector3D getTranslation ( @NotNull UUID boneIdentifier ) {
		return translations.get ( boneIdentifier );
	}
	
	public @Nullable Vector3D getTranslation ( @NotNull BoneConfiguration bone ) {
		return getTranslation ( bone.getIdentifier ( ) );
	}
	
	public Map < UUID, Vector3D > getRotations ( ) {
		return Collections.unmodifiableMap ( rotations );
	}
	
	public @Nullable Vector3D getRotation ( @NotNull UUID boneIdentifier ) {
		return rotations.get ( boneIdentifier );
	}
	
	public @Nullable Vector3D getRotation ( @NotNull BoneConfiguration bone ) {
		return getRotation ( bone.getIdentifier ( ) );
	}
	
	public Map < UUID, Vector3D > getScales ( ) {
		return Collections.unmodifiableMap ( scales );
	}
	
	public @Nullable Vector3D getScale ( @NotNull UUID boneIdentifier ) {
		return scales.get ( boneIdentifier );
	}
	
	public @Nullable Vector3D getScale ( @NotNull BoneConfiguration bone ) {
		return getScale ( bone.getIdentifier ( ) );
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.DURATION , duration );
		
		writeVectors ( translations , section , Constants.Key.TRANSLATIONS );
		writeVectors ( rotations , section , Constants.Key.ROTATIONS );
		writeVectors ( scales , section , Constants.Key.SCALES );
	}
	
	private void writeVectors ( Map < UUID, Vector3D > vectors , ConfigurationSection root , String sectionName ) {
		if ( vectors.size ( ) == 0 ) {
			root.set ( sectionName , null );
			return;
		}
		
		ConfigurationSection vectorsSection = root.createSection ( sectionName );
		int                  count          = 0;
		
		for ( Map.Entry < UUID, Vector3D > entry : vectors.entrySet ( ) ) {
			ConfigurationSection section = vectorsSection.createSection ( "v-" + count++ );
			
			// identifier
			ConfigurationUtil.writeSingleEntryLibraryObject (
					UUID.class , entry.getKey ( ) ,
					section , Constants.Key.IDENTIFIER
			);
			
			// value
			ConfigurationUtil.writeLibraryObject (
					Vector3D.class , entry.getValue ( ) ,
					section.createSection ( Constants.Key.VALUE )
			);
		}
	}
}