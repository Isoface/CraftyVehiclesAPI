package es.outlook.adriansrj.cv.api.vehicle.configuration;

import com.google.common.base.Preconditions;
import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.util.Constants;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 25/11/2023 / 12:20 a. m.
 */
@Getter
@EqualsAndHashCode
public class VehiclePhysicsConfiguration implements ConfigurationSectionWritable {
	
	public static final VehiclePhysicsConfiguration DEFAULTS = new VehiclePhysicsConfiguration (
			false ,
			3.8 ,
			0.15 ,
			1.5 / 100.0D ,
			20.0 / 100.0D ,
			20.0 / 100.0D ,
			60.0 / 100.0D ,
			55.5 / 100.0D ,
			50.0 / 100.0D ,
			20.0 / 100.0D ,
			30.0 / 100.0D ,
			50.0 / 100.0D ,
			90.0 / 100.0D ,
			1
	);
	
	public static VehiclePhysicsConfiguration load ( ConfigurationSection section )
			throws InvalidConfigurationException {
		boolean floats = section.getBoolean ( Constants.Key.FLOATS );
		
		double gravityMaximum = checkGravityValue ( section.getDouble (
				Constants.Key.GRAVITY_MAXIMUM ) , "gravity maximum" );
		double gravityAcceleration = checkGravityValue ( section.getDouble (
				Constants.Key.GRAVITY_ACCELERATION ) , "gravity acceleration" );
		
		double airFriction = checkFrictionValue ( section.getDouble (
				Constants.Key.FRICTION_AIR ) / 100.0D , "air friction" );
		
		double frictionOnUnknown = checkFrictionValue ( section.getDouble (
				Constants.Key.FRICTION_ON_UNKNOWN ) / 100.0D , "friction on unknown" );
		double frictionOnSolid = checkFrictionValue ( section.getDouble (
				Constants.Key.FRICTION_ON_SOLID ) / 100.0D , "friction on solid" );
		double frictionOnDusty = checkFrictionValue ( section.getDouble (
				Constants.Key.FRICTION_ON_DUSTY ) / 100.0D , "friction on dusty" );
		double frictionOnSnowy = checkFrictionValue ( section.getDouble (
				Constants.Key.FRICTION_ON_SNOWY ) / 100.0D , "friction on snowy" );
		double frictionOnSlippery = checkFrictionValue ( section.getDouble (
				Constants.Key.FRICTION_ON_SLIPPERY ) / 100.0D , "friction on slippery" );
		
		double frictionOnWater = checkFrictionValue ( section.getDouble (
				Constants.Key.FRICTION_ON_WATER ) / 100.0D , "friction on water" );
		double frictionOnLava = checkFrictionValue ( section.getDouble (
				Constants.Key.FRICTION_ON_LAVA ) / 100.0D , "friction on lava" );
		double frictionThroughWater = checkFrictionValue ( section.getDouble (
				Constants.Key.FRICTION_THROUGH_WATER ) / 100.0D , "friction through water" );
		double frictionThroughLava = checkFrictionValue ( section.getDouble (
				Constants.Key.FRICTION_THROUGH_LAVA ) / 100.0D , "friction through lava" );
		
		int blockClimbCapacity = section.getInt ( Constants.Key.BLOCK_CLIMB_CAPACITY );
		
		return new VehiclePhysicsConfiguration (
				floats , gravityMaximum , gravityAcceleration ,
				airFriction ,
				frictionOnUnknown ,
				frictionOnSolid ,
				frictionOnDusty ,
				frictionOnSnowy ,
				frictionOnSlippery ,
				frictionOnWater ,
				frictionOnLava ,
				frictionThroughWater ,
				frictionThroughLava ,
				blockClimbCapacity
		);
	}
	
	private static double checkValue ( double value , String name )
			throws InvalidConfigurationException {
		if ( value > 0.0D ) {
			return value;
		} else {
			throw new InvalidConfigurationException ( name + " must be > 0" );
		}
	}
	
	private static double checkGravityValue ( double value , String name )
			throws InvalidConfigurationException {
		if ( value >= 0.0D ) {
			return value;
		} else {
			throw new InvalidConfigurationException ( name + " cannot be negative" );
		}
	}
	
	private static double checkFrictionValue ( double value , String name )
			throws InvalidConfigurationException {
		if ( value > 0.0D && value <= 1.0D ) {
			return value;
		} else {
			throw new InvalidConfigurationException ( name + " must be > 0 and less or equal to 1.0" );
		}
	}
	
	private final boolean floats;
	
	private final double gravityMaximum;
	private final double gravityAcceleration;
	
	private final double airFriction;
	private final double frictionOnUnknown;
	private final double frictionOnSolid;
	private final double frictionOnDusty;
	private final double frictionOnSnowy;
	private final double frictionOnSlippery;
	private final double frictionOnWater;
	private final double frictionOnLava;
	private final double frictionThroughWater;
	private final double frictionThroughLava;
	
	private final int blockClimbCapacity;
	
	@Builder
	public VehiclePhysicsConfiguration ( boolean floats ,
			double gravityMaximum ,
			double gravityAcceleration ,
			double airFriction ,
			double frictionOnUnknown ,
			double frictionOnSolid ,
			double frictionOnDusty ,
			double frictionOnSnowy ,
			double frictionOnSlippery ,
			double frictionOnWater ,
			double frictionOnLava ,
			double frictionThroughWater ,
			double frictionThroughLava ,
			int blockClimbCapacity ) {
		Preconditions.checkArgument (
				gravityMaximum >= 0 , "gravityMaximum cannot be negative" );
		Preconditions.checkArgument (
				gravityAcceleration >= 0 , "gravityAcceleration cannot be negative" );
		
		Preconditions.checkArgument ( airFriction > 0 , "airFriction must be > 0" );
		Preconditions.checkArgument ( frictionOnUnknown > 0 , "frictionOnUnknown must be > 0" );
		Preconditions.checkArgument ( frictionOnSolid > 0 , "frictionOnSolid must be > 0" );
		Preconditions.checkArgument ( frictionOnDusty > 0 , "frictionOnDusty must be > 0" );
		Preconditions.checkArgument ( frictionOnSnowy > 0 , "frictionOnSnowy must be > 0" );
		Preconditions.checkArgument ( frictionOnSlippery > 0 , "frictionOnSlippery must be > 0" );
		Preconditions.checkArgument ( frictionOnWater > 0 , "frictionOnWater must be > 0" );
		Preconditions.checkArgument ( frictionOnLava > 0 , "frictionOnLava must be > 0" );
		Preconditions.checkArgument ( frictionThroughWater > 0 , "frictionThroughWater must be > 0" );
		Preconditions.checkArgument ( frictionThroughLava > 0 , "frictionThroughLava must be > 0" );
		
		this.floats               = floats;
		this.gravityMaximum       = gravityMaximum;
		this.gravityAcceleration  = gravityAcceleration;
		this.airFriction          = airFriction;
		this.frictionOnUnknown    = frictionOnUnknown;
		this.frictionOnSolid      = frictionOnSolid;
		this.frictionOnDusty      = frictionOnDusty;
		this.frictionOnSnowy      = frictionOnSnowy;
		this.frictionOnSlippery   = frictionOnSlippery;
		this.frictionOnWater      = frictionOnWater;
		this.frictionOnLava       = frictionOnLava;
		this.frictionThroughWater = frictionThroughWater;
		this.frictionThroughLava  = frictionThroughLava;
		this.blockClimbCapacity   = blockClimbCapacity;
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.FLOATS , floats );
		
		section.set ( Constants.Key.GRAVITY_MAXIMUM , gravityMaximum );
		section.set ( Constants.Key.GRAVITY_ACCELERATION , gravityAcceleration );
		
		section.set ( Constants.Key.FRICTION_AIR , airFriction * 100.0D );
		section.set ( Constants.Key.FRICTION_ON_UNKNOWN , frictionOnUnknown * 100.0D );
		section.set ( Constants.Key.FRICTION_ON_SOLID , frictionOnSolid * 100.0D );
		section.set ( Constants.Key.FRICTION_ON_DUSTY , frictionOnDusty * 100.0D );
		section.set ( Constants.Key.FRICTION_ON_SNOWY , frictionOnSnowy * 100.0D );
		section.set ( Constants.Key.FRICTION_ON_SLIPPERY , frictionOnSlippery * 100.0D );
		section.set ( Constants.Key.FRICTION_ON_WATER , frictionOnWater * 100.0D );
		section.set ( Constants.Key.FRICTION_ON_LAVA , frictionOnLava * 100.0D );
		section.set ( Constants.Key.FRICTION_THROUGH_WATER , frictionThroughWater * 100.0D );
		section.set ( Constants.Key.FRICTION_THROUGH_LAVA , frictionThroughLava * 100.0D );
		
		section.set ( Constants.Key.BLOCK_CLIMB_CAPACITY , blockClimbCapacity );
	}
}
