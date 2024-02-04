package es.outlook.adriansrj.cv.api.vehicle.configuration.model;

import com.google.common.base.Preconditions;
import es.outlook.adriansrj.cv.api.enums.EnumServerVersion;
import es.outlook.adriansrj.cv.api.enums.EnumVehicleModelType;
import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.interfaces.IDeyed;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.util.reflection.EnumReflection;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.compound.post19.Post19CompoundModelConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.compound.pre19.Pre19CompoundModelConfiguration;
import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author AdrianSR / 23/11/2023 / 12:39 p. m.
 */
public abstract class VehicleModelConfiguration implements IDeyed, ConfigurationSectionWritable {
	
	public static VehicleModelConfiguration load ( ConfigurationSection section )
			throws InvalidConfigurationException {
		EnumVehicleModelType modelType = EnumReflection.getEnumConstant (
				EnumVehicleModelType.class ,
				section.getString ( Constants.Key.TYPE , "" ) );
		
		if ( modelType == null ) {
			throw new InvalidConfigurationException ( "unknown model type" );
		}
		
		switch ( modelType ) {
			case COMPOUND:
				if ( EnumServerVersion.getServerVersion ( ).isSupportsDisplayEntities ( ) ) {
					return new Post19CompoundModelConfiguration ( section );
				} else {
					return new Pre19CompoundModelConfiguration ( section );
				}

//			case SIMPLE:
//				return new SimpleModelConfiguration ( section );
//			case COMPLEX:
//				return new ComplexModelConfiguration ( section );
		}
		
		throw new IllegalStateException ( modelType.name ( ) );
	}
	
	protected final @NotNull         String                               id;
	protected final @Getter @NotNull VehicleHitBoxConfiguration           hitBox;
	protected final @NotNull         Set < VehicleSeatConfiguration >     seats;
	protected final @NotNull         Set < VehicleParticleConfiguration > particles;
	protected final @NotNull         Set < VehicleSoundConfiguration >    sounds;
	
	protected VehicleModelConfiguration ( @NotNull String id ,
			@NotNull VehicleHitBoxConfiguration hitBox ,
			@NotNull Collection < VehicleSeatConfiguration > seats ,
			@Nullable Collection < VehicleParticleConfiguration > particles ,
			@Nullable Collection < VehicleSoundConfiguration > sounds ) {
		Preconditions.checkArgument (
				seats.size ( ) > 0 ,
				"at least one seat is required" );
		
		this.id        = IDeyed.idCheck ( id.toLowerCase ( ) );
		this.hitBox    = hitBox;
		this.seats     = new HashSet <> ( seats );
		this.particles = new THashSet <> ( );
		this.sounds    = new THashSet <> ( );
		
		if ( particles != null ) {
			this.particles.addAll ( particles );
		}
		
		if ( sounds != null ) {
			this.sounds.addAll ( sounds );
		}
	}
	
	protected VehicleModelConfiguration ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		id = IDeyed.loadId ( section );
		
		// loading hitbox
		ConfigurationSection hitBoxSection = section
				.getConfigurationSection ( Constants.Key.HIT_BOX );
		
		if ( hitBoxSection == null ) {
			throw new InvalidConfigurationException ( "vehicles require a hitbox" );
		}
		
		hitBox = VehicleHitBoxConfiguration.load ( hitBoxSection );
		
		// loading seats
		seats = new HashSet <> ( );
		
		for ( ConfigurationSection seatSection : ConfigurationUtil.getConfigurationSectionsAfter (
				section , Constants.Key.SEATS , false ) ) {
			seats.add ( VehicleSeatConfiguration.load ( seatSection ) );
		}
		
		if ( seats.size ( ) == 0 ) {
			throw new InvalidConfigurationException ( "vehicles require at least one seat" );
		}
		
		// loading particles
		particles = new THashSet <> ( );
		
		for ( ConfigurationSection particleSection : ConfigurationUtil.getConfigurationSectionsAfter (
				section , Constants.Key.PARTICLES , false ) ) {
			particles.add ( VehicleParticleConfiguration.load ( particleSection ) );
		}
		
		// loading sounds
		sounds = new THashSet <> ( );
		
		for ( ConfigurationSection soundSection : ConfigurationUtil.getConfigurationSectionsAfter (
				section , Constants.Key.SOUNDS , false ) ) {
			sounds.add ( VehicleSoundConfiguration.load ( soundSection ) );
		}
	}
	
	@Override
	public @NotNull String getId ( ) {
		return id;
	}
	
	public abstract @NotNull EnumVehicleModelType getType ( );
	
	public @NotNull Set < VehicleSeatConfiguration > getSeats ( ) {
		return Collections.unmodifiableSet ( seats );
	}
	
	public @NotNull Set < VehicleParticleConfiguration > getParticles ( ) {
		return Collections.unmodifiableSet ( particles );
	}
	
	public @NotNull Set < VehicleSoundConfiguration > getSounds ( ) {
		return Collections.unmodifiableSet ( sounds );
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		IDeyed.writeId ( this , section );
		
		section.set ( Constants.Key.TYPE , getType ( ).name ( ) );
		
		hitBox.write ( section.createSection ( Constants.Key.HIT_BOX ) );
		
		ConfigurationUtil.writeConfigurationSectionWritables (
				section.createSection ( Constants.Key.SEATS ) ,
				seats , "seat-"
		);
		
		ConfigurationUtil.writeConfigurationSectionWritables (
				section.createSection ( Constants.Key.PARTICLES ) ,
				particles , "particle-"
		);
		
		ConfigurationUtil.writeConfigurationSectionWritables (
				section.createSection ( Constants.Key.SOUNDS ) ,
				sounds , "sound-"
		);
	}
}