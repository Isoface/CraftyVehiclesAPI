package es.outlook.adriansrj.cv.api.event;

import es.outlook.adriansrj.cv.api.vehicle.Vehicle;
import org.jetbrains.annotations.NotNull;

/**
 * A bukkit event involving a {@link Vehicle}.
 * <br>
 * @author AdrianSR / 25/11/2023 / 12:15 p. m.
 */
public abstract class VehicleEvent extends CraftyVehiclesEvent {
	
	protected final @NotNull Vehicle vehicle;
	
	public VehicleEvent ( @NotNull Vehicle vehicle , boolean isAsync ) {
		super ( isAsync );
		
		this.vehicle = vehicle;
	}
	
	public VehicleEvent ( @NotNull Vehicle vehicle ) {
		this ( vehicle , false );
	}
	
	public @NotNull Vehicle getVehicle ( ) {
		return vehicle;
	}
}