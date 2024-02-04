package es.outlook.adriansrj.cv.api.vehicle.controller.predefined;

import es.outlook.adriansrj.cv.api.configuration.Configuration;
import es.outlook.adriansrj.cv.api.enums.EnumSurface;
import es.outlook.adriansrj.cv.api.vehicle.Vehicle;
import es.outlook.adriansrj.cv.api.vehicle.VehicleState;
import es.outlook.adriansrj.cv.api.vehicle.configuration.VehicleFuelConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.controller.VehicleController;
import es.outlook.adriansrj.cv.api.vehicle.controller.VehicleControllerProperties;
import es.outlook.adriansrj.cv.api.vehicle.input.PlayerSteerInput;
import org.apache.commons.math3.util.FastMath;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Controller for vehicles that operate on land.
 * <br>
 * @author AdrianSR / 28/12/2023 / 2:53 p. m.
 */
public class GroundVehicleController extends VehicleController {
	
	protected static final Collection < EnumSurface > SURFACE_SCAN_IGNORE = Arrays.asList (
			EnumSurface.EMPTY ,
			EnumSurface.UNKNOWN
	);
	
	protected double maxPositiveAcceleration;
	protected double maxNegativeAcceleration;
	
	protected double positiveAccelerationOnSolid;
	protected double positiveAccelerationOnDusty;
	protected double positiveAccelerationOnSnowy;
	protected double positiveAccelerationOnSlippery;
	
	protected double negativeAccelerationOnSolid;
	protected double negativeAccelerationOnDusty;
	protected double negativeAccelerationOnSnowy;
	protected double negativeAccelerationOnSlippery;
	
	protected double deceleration;
	protected int    turningAngleMin;
	protected int    turningAngleMax;
	
	protected float minFuelConsumption;
	protected float maxFuelConsumption;
	
	// currents
	protected boolean forward;
	protected boolean backward;
	protected boolean turningLeft;
	protected boolean turningRight;
	
	protected double currentSpeed;
	protected int    currentTurningAngle;
	
	/**
	 * Constructs a new VehicleController instance.
	 * <br>
	 * @param vehicle    the associated vehicle instance.
	 * @param properties the properties of the vehicle controller (<b>null to keep defaults</b>)
	 */
	public GroundVehicleController ( @NotNull Vehicle vehicle ,
			@Nullable VehicleControllerProperties properties ) {
		super ( vehicle , properties );
	}
	
	@Override
	protected void loadProperties ( ) {
		maxPositiveAcceleration = properties.getDoubleProperty (
				"max-positive-acceleration" , 0.20 );
		maxNegativeAcceleration = properties.getDoubleProperty (
				"max-negative-acceleration" , 0.20 );
		
		positiveAccelerationOnSolid    = properties.getDoubleProperty (
				"positive-acceleration-on-solid" , 0.025 );
		positiveAccelerationOnDusty    = properties.getDoubleProperty (
				"positive-acceleration-on-dusty" , 0.022 );
		positiveAccelerationOnSnowy    = properties.getDoubleProperty (
				"positive-acceleration-on-snowy" , 0.020 );
		positiveAccelerationOnSlippery = properties.getDoubleProperty (
				"positive-acceleration-on-slippery" , 0.020 );
		
		negativeAccelerationOnSolid    = properties.getDoubleProperty (
				"negative-acceleration-on-solid" , 0.025 );
		negativeAccelerationOnDusty    = properties.getDoubleProperty (
				"negative-acceleration-on-dusty" , 0.022 );
		negativeAccelerationOnSnowy    = properties.getDoubleProperty (
				"negative-acceleration-on-snowy" , 0.020 );
		negativeAccelerationOnSlippery = properties.getDoubleProperty (
				"negative-acceleration-on-slippery" , 0.020 );
		
		deceleration    = properties.getDoubleProperty ( "deceleration" , 0.015 );
		turningAngleMin = properties.getIntegerProperty ( "turning-angle-min" , 1 );
		turningAngleMax = properties.getIntegerProperty ( "turning-angle-max" , 4 );
		
		// fuel
		VehicleFuelConfiguration fuelConfiguration = vehicle.getConfiguration ( ).getFuel ( );
		
		minFuelConsumption = properties.getMinFuelConsumptionOverride (
				fuelConfiguration.getMinConsumption ( ) );
		maxFuelConsumption = properties.getMaxFuelConsumptionOverride (
				fuelConfiguration.getMaxConsumption ( ) );
	}
	
	@Override
	public void tick ( ) {
		Map < EnumSurface, Double > surface = null;
		
		// acceleration
		if ( ( forward || backward ) && hasFuel ( ) ) {
			surface = vehicle.getCurrentSurface ( SURFACE_SCAN_IGNORE );
			
			// calculating
			double acceleration = calculateAcceleration ( surface , forward );
			
			if ( acceleration != 0 && vehicle.containsAnyWithin (
					EnumSurface.WATER , EnumSurface.LAVA , EnumSurface.SOLID ) ) {
				acceleration = 0.0D; // cannot go through water/lava/solid blocks
			}
			
			// then applying
			if ( forward && acceleration != 0 ) {
				currentSpeed = Math.min ( currentSpeed + acceleration , maxPositiveAcceleration );
			} else if ( backward && acceleration != 0 ) {
				currentSpeed = Math.max ( currentSpeed - acceleration , -maxNegativeAcceleration );
			}
		}
		
		// turning
		tickTurning ( );
		
		// applying speed
		if ( currentSpeed != 0.0D && ( isOnLand (
				surface != null ? surface : vehicle.getCurrentSurface ( SURFACE_SCAN_IGNORE ) ) ) ) {
			float facing = vehicle.getRotation ( );
			
			if ( currentTurningAngle != 0 ) {
				if ( currentSpeed > 0 ) { // forward
					if ( turningLeft ) { // turning left
						facing -= currentTurningAngle;
					} else { // turning right
						facing += currentTurningAngle;
					}
				} else { // backwards
					if ( turningLeft ) { // turning left
						facing += currentTurningAngle;
					} else { // turning right
						facing -= currentTurningAngle;
					}
				}
				
				vehicle.setRotation ( facing );
			}
			
			double x = -FastMath.sin ( FastMath.toRadians ( facing ) );
			double z = FastMath.cos ( FastMath.toRadians ( facing ) );
			
			vehicle.addMomentumX ( currentSpeed * x );
			vehicle.addMomentumZ ( currentSpeed * z );
			
			// fuel consumption
			tickFuelConsumption ( );
		}
		
		// deceleration
		tickDeceleration ( );
		
		// updating state
		tickState ( );
	}
	
	private boolean hasFuel ( ) {
		if ( maxFuelConsumption <= 0.0F ) {
			return true; // no fuel consumption
		}
		
		float   fuelLevel          = vehicle.getFuelLevel ( );
		boolean fuelSystemDisabled = !Configuration.FUEL_ENABLE.booleanValue ( );
		boolean bypassCreative     = Configuration.FUEL_BYPASS_CREATIVE.booleanValue ( );
		Entity  operator           = vehicle.getOperator ( );
		
		return fuelSystemDisabled || fuelLevel > 0.0F || ( bypassCreative && operator instanceof Player
				&& ( ( Player ) operator ).getGameMode ( ) == GameMode.CREATIVE );
	}
	
	private void tickTurning ( ) {
		if ( turningLeft || turningRight ) {
			double maxAcceleration = currentSpeed > 0 ? maxPositiveAcceleration : maxNegativeAcceleration;
			double relative        = FastMath.abs ( currentSpeed ) / FastMath.abs ( maxAcceleration );
			
			int angle = turningAngleMin + ( int ) FastMath.round (
					( turningAngleMax - turningAngleMin ) * relative );
			
			currentTurningAngle = Math.max ( angle , 0 );
		} else {
			currentTurningAngle = 0;
		}
	}
	
	private void tickFuelConsumption ( ) {
		if ( currentSpeed == 0.0D
				|| minFuelConsumption > maxFuelConsumption
				|| maxFuelConsumption <= 0.0D ) {
			return;
		}
		
		double maxAcceleration = currentSpeed > 0 ? maxPositiveAcceleration : maxNegativeAcceleration;
		double relative        = FastMath.abs ( currentSpeed ) / FastMath.abs ( maxAcceleration );
		
		float consumption = minFuelConsumption + FastMath.round (
				( maxFuelConsumption - minFuelConsumption ) * relative
		);
		
		if ( consumption > 0.0F ) {
			vehicle.consumeFuel ( consumption );
		}
	}
	
	private void tickDeceleration ( ) {
		if ( currentSpeed > 0.0D ) {
			currentSpeed = Math.max ( 0 , currentSpeed - deceleration );
		} else if ( currentSpeed < 0.0D ) {
			currentSpeed = Math.min ( 0 , currentSpeed + deceleration );
		}
	}
	
	private void tickState ( ) {
		if ( vehicle.getMomentumX ( ) != 0.0D
				|| vehicle.getMomentumZ ( ) != 0.0D ) {
			boolean      backwards = currentSpeed < 0;
			VehicleState state     = backwards ? VehicleState.MOVING_BACKWARDS : VehicleState.MOVING;
			
			if ( turningLeft ) {
				state = backwards
						? VehicleState.MOVING_BACKWARDS_TURNING_LEFT
						: VehicleState.MOVING_TURNING_LEFT;
			} else if ( turningRight ) {
				state = backwards
						? VehicleState.MOVING_BACKWARDS_TURNING_RIGHT
						: VehicleState.MOVING_TURNING_RIGHT;
			}
			
			vehicle.setState ( state );
		} else {
			if ( turningLeft ) {
				vehicle.setState ( VehicleState.TURNING_LEFT );
			} else if ( turningRight ) {
				vehicle.setState ( VehicleState.TURNING_RIGHT );
			} else {
				vehicle.setState ( VehicleState.IDLE );
			}
		}
	}
	
	protected boolean isOnLand ( @NotNull Map < EnumSurface, Double > surface ) {
		return surface.containsKey ( EnumSurface.SOLID )
				|| surface.containsKey ( EnumSurface.DUSTY )
				|| surface.containsKey ( EnumSurface.SNOWY )
				|| surface.containsKey ( EnumSurface.SLIPPERY );
	}
	
	protected double calculateAcceleration ( @NotNull Map < EnumSurface, Double > surface , boolean positive ) {
		double acceleration = 0.0D;
		
		for ( Map.Entry < EnumSurface, Double > entry : surface.entrySet ( ) ) {
			acceleration += getAccelerationOn ( entry.getKey ( ) , positive ) * entry.getValue ( );
		}
		
		return acceleration;
	}
	
	protected double getAccelerationOn ( @NotNull EnumSurface surface , boolean positive ) {
		switch ( surface ) {
			case SOLID:
				return positive ? positiveAccelerationOnSolid : negativeAccelerationOnSolid;
			case DUSTY:
				return positive ? positiveAccelerationOnDusty : negativeAccelerationOnDusty;
			case SNOWY:
				return positive ? positiveAccelerationOnSnowy : negativeAccelerationOnSnowy;
			case SLIPPERY:
				return positive ? positiveAccelerationOnSlippery : negativeAccelerationOnSlippery;
		}
		
		return 0.0D;
	}
	
	@Override
	public void process ( @NotNull PlayerSteerInput input ) {
		forward  = input.forward > 0;
		backward = input.forward < 0;
		
		turningLeft  = input.sideways > 0;
		turningRight = input.sideways < 0;
	}
	
	@Override
	public void standby ( ) {
		forward      = false;
		backward     = false;
		turningLeft  = false;
		turningRight = false;
	}
}