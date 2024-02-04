package es.outlook.adriansrj.cv.api.event;

import es.outlook.adriansrj.cv.api.vehicle.Vehicle;
import es.outlook.adriansrj.cv.api.vehicle.VehicleState;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 30/11/2023 / 2:16 p. m.
 */
public class VehicleStateChangeEvent extends VehicleEvent {
	
	private static final HandlerList HANDLERS = new HandlerList ( );
	
	@NotNull
	public static HandlerList getHandlerList ( ) {
		return HANDLERS;
	}
	
	private final @NotNull @Getter VehicleState previousState;
	private final @NotNull @Getter VehicleState newState;
	
	public VehicleStateChangeEvent ( @NotNull Vehicle vehicle ,
			@NotNull VehicleState previousState ,
			@NotNull VehicleState newState ) {
		super ( vehicle , false );
		
		this.previousState = previousState;
		this.newState      = newState;
	}
	
	@NotNull
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLERS;
	}
}
