package es.outlook.adriansrj.cv.api.vehicle.controller;

import es.outlook.adriansrj.cv.api.interfaces.IDeyed;
import es.outlook.adriansrj.cv.api.registry.types.VehicleControllerFactoryRegistry;
import es.outlook.adriansrj.cv.api.vehicle.Vehicle;
import es.outlook.adriansrj.cv.api.vehicle.configuration.VehicleControllersConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.input.PlayerSteerInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for vehicle controllers, defining core behavior and properties.
 * <br>
 * Vehicle controllers define the behavior of vehicles, handling tasks such as:
 * <ul>
 * <li>Processing player input (steering, acceleration, braking)</li>
 * <li>Updating vehicle movement and physics each tick</li>
 * <li>Managing vehicle state transitions (e.g., idle, moving, turning)</li>
 * <li>Enforcing vehicle constraints and limitations</li>
 * </ul>
 * <br>
 * Concrete implementations of this class should provide specific
 * logic for different vehicle types and behaviors.
 * <br>
 * @author AdrianSR / 28/12/2023 / 12:18 p. m.
 */
public abstract class VehicleController {
	
	/**
	 * Works in conjunction with the
	 * {@link VehicleControllerFactoryRegistry};
	 * <br>
	 * This is the class to be used by developers for creating
	 * custom controllers. You would just create and register
	 * an instance of this that creates the instance of your
	 * custom controller.
	 * <br>
	 * @author AdrianSR / 28/12/2023 / 1:50 p. m.
	 */
	public static abstract class Factory implements IDeyed {
		
		/**
		 * Gets the id of the controller associated with this factory.
		 * <br>
		 * The {@link VehicleControllersConfiguration} class will find this
		 * factory in the registry using this id.
		 * <br>
		 * @return id of the controller associated with this factory.
		 */
		public abstract @NotNull String getControllerId ( );
		
		/**
		 * Creates a new instance of the specific vehicle controller this factory represents.
		 * <br>
		 * @param vehicle    The vehicle that the controller will manage.
		 * @param properties Optional properties to customize the controller's behavior.
		 *                   If null, default properties will be used.
		 * @return A new instance of the vehicle controller, fully configured for the given vehicle and properties.
		 */
		public abstract @NotNull VehicleController createInstance (
				@NotNull Vehicle vehicle ,
				@Nullable VehicleControllerProperties properties );
		
		@Override
		public final @NotNull String getId ( ) {
			return IDeyed.idCheck ( getControllerId ( ) );
		}
	}
	
	protected final @NotNull Vehicle                     vehicle;
	protected final @NotNull VehicleControllerProperties properties;
	
	/**
	 * Constructs a new VehicleController instance.
	 * <br>
	 * @param vehicle    the associated vehicle instance.
	 * @param properties the properties of the vehicle controller (<b>null to keep defaults</b>)
	 */
	public VehicleController ( @NotNull Vehicle vehicle ,
			@Nullable VehicleControllerProperties properties ) {
		this.vehicle    = vehicle;
		this.properties = properties != null ? properties : new VehicleControllerProperties ( );
		
		this.loadProperties ( );
	}
	
	/**
	 * Called from constructor to load properties.
	 */
	protected abstract void loadProperties ( );
	
	/**
	 * Gets the associated vehicle instance.
	 * <br>
	 * @return the associated vehicle
	 */
	public @NotNull Vehicle getVehicle ( ) {
		return vehicle;
	}
	
	/**
	 * Gets the properties for this controller.
	 * <br>
	 * @return the vehicle controller properties
	 */
	public @NotNull VehicleControllerProperties getProperties ( ) {
		return properties;
	}
	
	/**
	 * Performs a tick update for the vehicle controller.
	 * This method is called periodically to update the vehicle's behavior.
	 */
	public abstract void tick ( );
	
	/**
	 * Processes player steering input and adjusts the vehicle's behavior accordingly.
	 * <br>
	 * @param input The player's steering input.
	 */
	public abstract void process ( @NotNull PlayerSteerInput input );
	
	/**
	 * Called whenever the player input ceases. Typically
	 * used to handle behaviors like gradual deceleration.
	 */
	public abstract void standby ( );
}