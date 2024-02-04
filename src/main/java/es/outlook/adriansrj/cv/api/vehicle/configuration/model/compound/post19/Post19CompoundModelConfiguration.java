package es.outlook.adriansrj.cv.api.vehicle.configuration.model.compound.post19;

import es.outlook.adriansrj.cv.api.enums.EnumVehicleModelType;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.vehicle.VehicleState;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleHitBoxConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleModelConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleParticleConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleSeatConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleSoundConfiguration;
import lombok.Builder;
import lombok.Singular;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author AdrianSR / 23/11/2023 / 12:53 p. m.
 */
public class Post19CompoundModelConfiguration extends VehicleModelConfiguration {
	
	private final @NotNull  Set < PartConfiguration >      parts;
	private final @NotNull  Set < BoneConfiguration >      bones;
	private final @Nullable RigConfiguration               rig;
	private final @NotNull  Set < AnimationConfiguration > animations;
	
	@Builder
	public Post19CompoundModelConfiguration ( @NotNull String id ,
			@NotNull VehicleHitBoxConfiguration hitBox ,
			@NotNull @Singular Collection < VehicleSeatConfiguration > seats ,
			@NotNull @Singular Collection < PartConfiguration > parts ,
			@Nullable @Singular Collection < BoneConfiguration > bones ,
			@Nullable RigConfiguration rig ,
			@Nullable @Singular Collection < AnimationConfiguration > animations ,
			@Nullable @Singular Collection < VehicleParticleConfiguration > particles ,
			@Nullable @Singular Collection < VehicleSoundConfiguration > sounds ) {
		super ( id , hitBox , seats , particles , sounds );
		
		this.parts      = new HashSet <> ( parts );
		this.bones      = new HashSet <> ( );
		this.animations = new HashSet <> ( );
		
		if ( bones != null ) {
			this.bones.addAll ( bones );
		}
		
		if ( animations != null ) {
			this.animations.addAll ( animations );
		}
		
		this.rig = rig;
	}
	
	public Post19CompoundModelConfiguration ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		super ( section );
		
		// loading parts
		parts = new HashSet <> ( );
		
		for ( ConfigurationSection partSection : ConfigurationUtil.getConfigurationSectionsAfter (
				section , Constants.Key.PARTS , false ) ) {
			try {
				parts.add ( PartConfiguration.load ( partSection ) );
			} catch ( InvalidConfigurationException ex ) {
				ex.printStackTrace ( );
			}
		}
		
		if ( parts.size ( ) == 0 ) {
			throw new InvalidConfigurationException ( "at least one valid part is required" );
		}
		
		// loading bones
		bones = new HashSet <> ( );
		
		for ( ConfigurationSection boneSection : ConfigurationUtil.getConfigurationSectionsAfter (
				section , Constants.Key.BONES , false ) ) {
			try {
				bones.add ( BoneConfiguration.load ( boneSection ) );
			} catch ( InvalidConfigurationException ex ) {
				ex.printStackTrace ( );
			}
		}
		
		// loading rig
		ConfigurationSection rigSection = section.getConfigurationSection ( Constants.Key.RIG );
		
		this.rig = rigSection != null ? RigConfiguration.load ( rigSection , this ) : null;
		
		// loading animations
		animations = new HashSet <> ( );
		
		for ( ConfigurationSection animationSection : ConfigurationUtil.getConfigurationSectionsAfter (
				section , Constants.Key.ANIMATIONS , false ) ) {
			try {
				animations.add ( AnimationConfiguration.load ( animationSection ) );
			} catch ( InvalidConfigurationException ex ) {
				ex.printStackTrace ( );
			}
		}
	}
	
	@Override
	public @NotNull EnumVehicleModelType getType ( ) {
		return EnumVehicleModelType.COMPOUND;
	}
	
	public @NotNull Set < PartConfiguration > getParts ( ) {
		return Collections.unmodifiableSet ( parts );
	}
	
	public @Nullable PartConfiguration getPartByIdentifier ( @NotNull UUID identifier ) {
		for ( PartConfiguration part : parts ) {
			if ( Objects.equals ( part.getIdentifier ( ) , identifier ) ) {
				return part;
			}
		}
		
		return null;
	}
	
	public @NotNull Set < BoneConfiguration > getBones ( ) {
		return Collections.unmodifiableSet ( bones );
	}
	
	public @Nullable BoneConfiguration getBoneByIdentifier ( @NotNull UUID identifier ) {
		for ( BoneConfiguration bone : bones ) {
			if ( Objects.equals ( bone.getIdentifier ( ) , identifier ) ) {
				return bone;
			}
		}
		
		return null;
	}
	
	public @Nullable RigConfiguration getRig ( ) {
		return rig;
	}
	
	public @NotNull Set < AnimationConfiguration > getAnimations ( ) {
		return Collections.unmodifiableSet ( animations );
	}
	
	public @Nullable AnimationConfiguration getAnimationByState ( @NotNull VehicleState state ) {
		for ( AnimationConfiguration other : animations ) {
			if ( other.appliesTo ( state ) ) {
				return other;
			}
		}
		
		return null;
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		super.write ( section );
		
		ConfigurationUtil.writeConfigurationSectionWritables (
				section.createSection ( Constants.Key.PARTS ) ,
				parts , "part-"
		);
		
		ConfigurationUtil.writeConfigurationSectionWritables (
				section.createSection ( Constants.Key.BONES ) ,
				bones , "bone-"
		);
		
		if ( rig != null ) {
			rig.write ( section.createSection ( Constants.Key.RIG ) );
		}
		
		ConfigurationUtil.writeConfigurationSectionWritables (
				section.createSection ( Constants.Key.ANIMATIONS ) ,
				animations , "animation-"
		);
	}
}