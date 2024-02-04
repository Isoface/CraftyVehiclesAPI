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
 * Controller for airplane-like vehicles.
 * <br>
 * @author AdrianSR / 28/12/2023 / 2:54 p. m.
 */
public class FixedWingVehicleController extends VehicleController {
	
	protected static final Collection < EnumSurface > SURFACE_SCAN_IGNORE = Arrays.asList (
			EnumSurface.EMPTY ,
			EnumSurface.UNKNOWN
	);
	
	protected double  gravityCompensation;
	protected boolean canTakeOffFromLandOnly;
	
	protected double takeOffSpeed;
	protected double stallSpeed;
	
	protected double increaseHeightMinSpeed;
	protected double increaseHeightMaxSpeed;
	
	protected double decreaseHeightMinSpeed;
	protected double decreaseHeightMaxSpeed;
	
	protected double maxFlyingPositiveAcceleration;
	protected double maxTakingOffPositiveAcceleration;
	protected double maxNegativeAcceleration;
	
	protected double positiveAccelerationFlying;
	protected double positiveAccelerationOnSolid;
	protected double positiveAccelerationOnDusty;
	protected double positiveAccelerationOnSnowy;
	protected double positiveAccelerationOnSlippery;
	protected double positiveAccelerationOnWater;
	protected double positiveAccelerationOnLava;
	
	protected double negativeAccelerationFlying;
	protected double negativeAccelerationOnSolid;
	protected double negativeAccelerationOnDusty;
	protected double negativeAccelerationOnSnowy;
	protected double negativeAccelerationOnSlippery;
	protected double negativeAccelerationOnWater;
	protected double negativeAccelerationOnLava;
	
	protected double deceleration;
	
	protected int turningAngleMin;
	protected int turningAngleMax;
	
	protected float minFuelConsumption;
	protected float maxFuelConsumption;
	
	// currents
	protected boolean forward;
	protected boolean backward;
	protected boolean increasingHeight;
	protected boolean decreasingHeight;
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
	public FixedWingVehicleController ( @NotNull Vehicle vehicle ,
			@Nullable VehicleControllerProperties properties ) {
		super ( vehicle , properties );
	}
	
	@Override
	protected void loadProperties ( ) {
		gravityCompensation = vehicle.getConfiguration ( ).getPhysics ( ).getGravityAcceleration ( );
		
		canTakeOffFromLandOnly = properties.getBooleanProperty (
				"take-off-from-land-only" , true );
		
		takeOffSpeed = properties.getDoubleProperty (
				"take-off-speed" , 2.0 );
		stallSpeed   = properties.getDoubleProperty (
				"stall-speed" , 1.5 );
		
		increaseHeightMinSpeed = properties.getDoubleProperty (
				"increase-height-min-speed" , 0.23D );
		increaseHeightMaxSpeed = properties.getDoubleProperty (
				"increase-height-max-speed" , 0.23D );
		
		decreaseHeightMinSpeed = properties.getDoubleProperty (
				"decrease-height-min-speed" , 0.23D );
		decreaseHeightMaxSpeed = properties.getDoubleProperty (
				"decrease-height-max-speed" , 0.23D );
		
		maxFlyingPositiveAcceleration    = properties.getDoubleProperty (
				"max-flying-positive-acceleration" , 0.20 );
		maxTakingOffPositiveAcceleration = properties.getDoubleProperty (
				"max-land-positive-acceleration" , 0.20 );
		maxNegativeAcceleration          = properties.getDoubleProperty (
				"max-negative-acceleration" , 0.05 );
		
		positiveAccelerationFlying     = properties.getDoubleProperty (
				"positive-acceleration-flying" , 0.025 );
		positiveAccelerationOnSolid    = properties.getDoubleProperty (
				"positive-acceleration-on-solid" , 0.025 );
		positiveAccelerationOnDusty    = properties.getDoubleProperty (
				"positive-acceleration-on-dusty" , 0.022 );
		positiveAccelerationOnSnowy    = properties.getDoubleProperty (
				"positive-acceleration-on-snowy" , 0.020 );
		positiveAccelerationOnSlippery = properties.getDoubleProperty (
				"positive-acceleration-on-slippery" , 0.020 );
		positiveAccelerationOnWater    = properties.getDoubleProperty (
				"positive-acceleration-on-water" , 0.025 );
		positiveAccelerationOnLava     = properties.getDoubleProperty (
				"positive-acceleration-on-lava" , 0.025 );
		
		negativeAccelerationFlying     = properties.getDoubleProperty (
				"negative-acceleration-flying" , 0.010 );
		negativeAccelerationOnSolid    = properties.getDoubleProperty (
				"negative-acceleration-on-solid" , 0.010 );
		negativeAccelerationOnDusty    = properties.getDoubleProperty (
				"negative-acceleration-on-dusty" , 0.009 );
		negativeAccelerationOnSnowy    = properties.getDoubleProperty (
				"negative-acceleration-on-snowy" , 0.008 );
		negativeAccelerationOnSlippery = properties.getDoubleProperty (
				"negative-acceleration-on-slippery" , 0.008 );
		negativeAccelerationOnWater    = properties.getDoubleProperty (
				"negative-acceleration-on-water" , 0.010 );
		negativeAccelerationOnLava     = properties.getDoubleProperty (
				"negative-acceleration-on-lava" , 0.010 );
		
		deceleration    = properties.getDoubleProperty ( "deceleration" , 0.008 );
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
		Map < EnumSurface, Double > surface     = null;
		boolean                     consumeFuel = false;
		
		// height will be maintained counteracting gravity
		if ( currentSpeed > 0.0D ) {
			vehicle.addMomentumY ( gravityCompensation * ( currentSpeed / maxFlyingPositiveAcceleration ) );
		}
		
		// increasing/decreasing height
		if ( ( increasingHeight || decreasingHeight ) && currentSpeed >= takeOffSpeed && hasFuel ( ) ) {
			double maxAcceleration = currentSpeed > 0 ? maxFlyingPositiveAcceleration : maxNegativeAcceleration;
			double relative        = FastMath.abs ( currentSpeed ) / FastMath.abs ( maxAcceleration );
			double yChange         = 0.0D;
			
			if ( increasingHeight && currentSpeed >= stallSpeed ) {
				yChange = increaseHeightMinSpeed
						+ ( ( increaseHeightMaxSpeed - increaseHeightMinSpeed ) * relative );
			} else if ( decreasingHeight ) {
				double decreaseHeightSpeed = decreaseHeightMinSpeed
						+ ( ( decreaseHeightMaxSpeed - decreaseHeightMinSpeed ) * relative );
				
				yChange = -decreaseHeightSpeed;
			}
			
			if ( yChange != 0 && vehicle.containsAnyWithin (
					EnumSurface.WATER , EnumSurface.LAVA , EnumSurface.SOLID ) ) {
				yChange = 0.0D; // cannot go through water/lava/solid blocks
			}
			
			vehicle.addMomentumY ( yChange );
			
			// fuel consumption
			consumeFuel = true;
		}
		
		// acceleration
		double previousSpeed = currentSpeed;
		
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
				double maxAcceleration = isInTheAir ( surface )
						? maxFlyingPositiveAcceleration : maxTakingOffPositiveAcceleration;
				
				currentSpeed = Math.min ( currentSpeed + acceleration , maxAcceleration );
			} else if ( backward && acceleration != 0 && !isInTheAir ( surface ) ) {
				currentSpeed = Math.max ( currentSpeed - acceleration , -maxNegativeAcceleration );
			}
		}
		
		// taking off
		if ( previousSpeed < takeOffSpeed && currentSpeed >= takeOffSpeed
				&& ( canTakeOffFrom ( surface != null ? surface : (
				surface = vehicle.getCurrentSurface ( SURFACE_SCAN_IGNORE ) ) ) ) ) {
			vehicle.addMomentumY ( increaseHeightMinSpeed );
		}
		
		// turning
		tickTurning ( );
		
		// applying speed
		if ( currentSpeed != 0.0D ) {
			if ( surface == null ) {
				surface = vehicle.getCurrentSurface ( SURFACE_SCAN_IGNORE );
			}
			
			if ( isInTheAir ( surface ) || canTakeOffFrom ( surface ) ) {
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
				consumeFuel = true;
			}
		}
		
		// fuel consumption
		if ( consumeFuel ) {
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
	
	private void tickFuelConsumption ( ) {
		if ( currentSpeed == 0.0D
				|| minFuelConsumption > maxFuelConsumption
				|| maxFuelConsumption <= 0.0D ) {
			return;
		}
		
		double maxAcceleration = currentSpeed > 0 ? maxFlyingPositiveAcceleration : maxNegativeAcceleration;
		double relative        = FastMath.abs ( currentSpeed ) / FastMath.abs ( maxAcceleration );
		
		float consumption = minFuelConsumption + FastMath.round (
				( maxFuelConsumption - minFuelConsumption ) * relative
		);
		
		if ( consumption > 0.0F ) {
			vehicle.consumeFuel ( consumption );
		}
	}
	
	private void tickTurning ( ) {
		if ( turningLeft || turningRight ) {
			double maxAcceleration = currentSpeed > 0 ? maxFlyingPositiveAcceleration : maxNegativeAcceleration;
			double relative        = FastMath.abs ( currentSpeed ) / FastMath.abs ( maxAcceleration );
			
			int angle = turningAngleMin + ( int ) FastMath.round (
					( turningAngleMax - turningAngleMin ) * relative );
			
			currentTurningAngle = Math.max ( angle , 0 );
		} else {
			currentTurningAngle = 0;
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
	
	protected boolean canTakeOffFrom ( @NotNull Map < EnumSurface, Double > surface ) {
		return surface.containsKey ( EnumSurface.SOLID )
				|| surface.containsKey ( EnumSurface.DUSTY )
				|| surface.containsKey ( EnumSurface.SNOWY )
				|| surface.containsKey ( EnumSurface.SLIPPERY )
				|| ( surface.containsKey ( EnumSurface.WATER ) && !canTakeOffFromLandOnly )
				|| ( surface.containsKey ( EnumSurface.LAVA ) && !canTakeOffFromLandOnly );
	}
	
	protected boolean isInTheAir ( @NotNull Map < EnumSurface, Double > surface ) {
		return surface.size ( ) == 0 || ( surface.size ( ) == 1
				&& surface.containsKey ( EnumSurface.EMPTY ) );
	}
	
	protected double calculateAcceleration ( @NotNull Map < EnumSurface, Double > surface , boolean positive ) {
		double acceleration = 0.0D;
		
		if ( isInTheAir ( surface ) ) {
			return positive ? positiveAccelerationFlying : negativeAccelerationFlying;
		}
		
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
			
			case WATER:
				if ( !canTakeOffFromLandOnly ) {
					return positive ? positiveAccelerationOnWater : negativeAccelerationOnWater;
				}
				
				break;
			
			case LAVA:
				if ( !canTakeOffFromLandOnly ) {
					return positive ? positiveAccelerationOnLava : negativeAccelerationOnLava;
				}
				
				break;
		}
		
		return 0.0D;
	}
	
	@Override
	public void process ( @NotNull PlayerSteerInput input ) {
		forward  = input.forward > 0;
		backward = input.forward < 0;
		
		increasingHeight = input.jump;
		decreasingHeight = input.unmount;
		
		turningLeft  = input.sideways > 0;
		turningRight = input.sideways < 0;
	}
	
	@Override
	public void standby ( ) {
		forward          = false;
		backward         = false;
		increasingHeight = false;
		decreasingHeight = false;
		turningLeft      = false;
		turningRight     = false;
	}
}
