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

/**
 * Controller for rotorcraft-like vehicles (e.g helicopters).
 * <br>
 * @author AdrianSR / 28/12/2023 / 2:54 p. m.
 */
public class RotorcraftVehicleController extends VehicleController {
	
	protected double maxPositiveAcceleration;
	protected double maxNegativeAcceleration;
	
	protected double positiveAcceleration;
	protected double negativeAcceleration;
	
	protected double deceleration;
	
	protected double increaseHeightSpeed;
	protected double decreaseHeightSpeed;
	
	protected int rotationAngle;
	
	protected float minFuelConsumption;
	protected float maxFuelConsumption;
	
	// currents
	protected boolean forward;
	protected boolean backward;
	protected boolean increasingHeight;
	protected boolean decreasingHeight;
	protected boolean rotatingLeft;
	protected boolean rotatingRight;
	protected double  currentSpeed;
	
	/**
	 * Constructs a new VehicleController instance.
	 * <br>
	 * @param vehicle    the associated vehicle instance.
	 * @param properties the properties of the vehicle controller (<b>null to keep defaults</b>)
	 */
	public RotorcraftVehicleController ( @NotNull Vehicle vehicle ,
			@Nullable VehicleControllerProperties properties ) {
		super ( vehicle , properties );
	}
	
	@Override
	protected void loadProperties ( ) {
		maxPositiveAcceleration = properties.getDoubleProperty ( "max-positive-acceleration" , 0.95 );
		maxNegativeAcceleration = properties.getDoubleProperty ( "max-negative-acceleration" , 0.25 );
		
		positiveAcceleration = properties.getDoubleProperty ( "positive-acceleration" , 0.055 );
		negativeAcceleration = properties.getDoubleProperty ( "negative-acceleration" , 0.035 );
		
		deceleration = properties.getDoubleProperty ( "deceleration" , 0.04 );
		
		increaseHeightSpeed = properties.getDoubleProperty (
				"increase-height-speed" , 0.23D );
		decreaseHeightSpeed = properties.getDoubleProperty (
				"decrease-height-speed" , 0.23D );
		
		rotationAngle = properties.getIntegerProperty ( "rotation-angle" , 3 );
		
		// fuel
		VehicleFuelConfiguration fuelConfiguration = vehicle.getConfiguration ( ).getFuel ( );
		
		minFuelConsumption = properties.getMinFuelConsumptionOverride (
				fuelConfiguration.getMinConsumption ( ) );
		maxFuelConsumption = properties.getMaxFuelConsumptionOverride (
				fuelConfiguration.getMaxConsumption ( ) );
	}
	
	@Override
	public void tick ( ) {
		boolean consumeFuel = false;
		
		// acceleration.
		// cannot go through water/lava/solid blocks
		if ( ( forward || backward ) && hasFuel ( ) && !vehicle.containsAnyWithin (
				EnumSurface.WATER , EnumSurface.LAVA , EnumSurface.SOLID ) ) {
			if ( forward && positiveAcceleration != 0 ) {
				currentSpeed = Math.min ( currentSpeed + positiveAcceleration , maxPositiveAcceleration );
			} else if ( backward && negativeAcceleration != 0 ) {
				currentSpeed = Math.max ( currentSpeed - negativeAcceleration , -maxNegativeAcceleration );
			}
		}
		
		// increasing/decreasing height
		if ( increasingHeight || decreasingHeight ) {
			if ( increasingHeight ) {
				vehicle.addMomentumY ( increaseHeightSpeed );
			} else {
				vehicle.addMomentumY ( -decreaseHeightSpeed );
			}
			
			// fuel consumption
			consumeFuel = true;
		}
		
		// rotating
		tickRotation ( );
		
		// applying speed
		if ( currentSpeed != 0.0D && vehicle.isInTheAir ( ) ) {
			float facing = vehicle.getRotation ( );
			
			double x = -FastMath.sin ( FastMath.toRadians ( facing ) );
			double z = FastMath.cos ( FastMath.toRadians ( facing ) );
			
			vehicle.addMomentumX ( currentSpeed * x );
			vehicle.addMomentumZ ( currentSpeed * z );
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
		
		double maxAcceleration = currentSpeed > 0 ? maxPositiveAcceleration : maxNegativeAcceleration;
		double relative        = FastMath.abs ( currentSpeed ) / FastMath.abs ( maxAcceleration );
		
		float consumption = minFuelConsumption + FastMath.round (
				( maxFuelConsumption - minFuelConsumption ) * relative
		);
		
		if ( consumption > 0.0F ) {
			vehicle.consumeFuel ( consumption );
		}
	}
	
	private void tickRotation ( ) {
		if ( rotatingLeft || rotatingRight ) {
			float facing = vehicle.getRotation ( );
			
			if ( currentSpeed >= 0.0D ) { // forward
				if ( rotatingLeft ) { // rotating to the left
					facing -= rotationAngle;
				} else { // rotating to the right
					facing += rotationAngle;
				}
			} else { // backwards
				if ( rotatingLeft ) { // rotating to the left
					facing += rotationAngle;
				} else { // rotating to the right
					facing -= rotationAngle;
				}
			}
			
			vehicle.setRotation ( facing );
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
		if ( increasingHeight || decreasingHeight ) {
			VehicleState state = increasingHeight
					? VehicleState.INCREASING_HEIGHT
					: VehicleState.DECREASING_HEIGHT;
			
			if ( rotatingLeft ) {
				state = increasingHeight
						? VehicleState.INCREASING_HEIGHT_TURNING_LEFT
						: VehicleState.DECREASING_HEIGHT_TURNING_LEFT;
			} else if ( rotatingRight ) {
				state = increasingHeight
						? VehicleState.INCREASING_HEIGHT_TURNING_RIGHT
						: VehicleState.DECREASING_HEIGHT_TURNING_RIGHT;
			}
			
			vehicle.setState ( state );
		} else if ( vehicle.getMomentumX ( ) != 0.0D
				|| vehicle.getMomentumZ ( ) != 0.0D ) {
			boolean      backwards = currentSpeed < 0;
			VehicleState state     = backwards ? VehicleState.MOVING_BACKWARDS : VehicleState.MOVING;
			
			if ( rotatingLeft ) {
				state = backwards
						? VehicleState.MOVING_BACKWARDS_TURNING_LEFT
						: VehicleState.MOVING_TURNING_LEFT;
			} else if ( rotatingRight ) {
				state = backwards
						? VehicleState.MOVING_BACKWARDS_TURNING_RIGHT
						: VehicleState.MOVING_TURNING_RIGHT;
			}
			
			vehicle.setState ( state );
		} else {
			if ( rotatingLeft ) {
				vehicle.setState ( VehicleState.TURNING_LEFT );
			} else if ( rotatingRight ) {
				vehicle.setState ( VehicleState.TURNING_RIGHT );
			} else {
				vehicle.setState ( VehicleState.IDLE );
			}
		}
	}
	
	@Override
	public void process ( @NotNull PlayerSteerInput input ) {
		forward  = input.forward > 0;
		backward = input.forward < 0;
		
		increasingHeight = input.jump;
		decreasingHeight = input.unmount;
		
		rotatingLeft  = input.sideways > 0;
		rotatingRight = input.sideways < 0;
	}
	
	@Override
	public void standby ( ) {
		forward  = false;
		backward = false;
		
		increasingHeight = false;
		decreasingHeight = false;
		
		rotatingLeft  = false;
		rotatingRight = false;
	}
}