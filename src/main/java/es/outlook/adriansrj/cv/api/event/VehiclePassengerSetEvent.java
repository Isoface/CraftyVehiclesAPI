package es.outlook.adriansrj.cv.api.event;

import es.outlook.adriansrj.cv.api.vehicle.Vehicle;
import es.outlook.adriansrj.cv.api.vehicle.VehicleSeat;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired whenever the passenger of a vehicle seat changes.
 * <br>
 * @author AdrianSR / 25/11/2023 / 12:15 p. m.
 */
public class VehiclePassengerSetEvent extends VehicleEvent {
	
	private static final HandlerList HANDLERS = new HandlerList ( );
	
	@NotNull
	public static HandlerList getHandlerList ( ) {
		return HANDLERS;
	}
	
	private final @NotNull          VehicleSeat vehicleSeat;
	private final @Nullable @Getter Entity      previousOperator;
	private final @Nullable @Getter Entity      newOperator;
	
	public VehiclePassengerSetEvent (
			@NotNull Vehicle vehicle ,
			@NotNull VehicleSeat vehicleSeat ,
			@Nullable Entity previousPassenger ,
			@Nullable Entity newPassenger ) {
		super ( vehicle , false );
		
		this.vehicleSeat      = vehicleSeat;
		this.previousOperator = previousPassenger;
		this.newOperator      = newPassenger;
	}
	
	public @NotNull VehicleSeat getSeat ( ) {
		return vehicleSeat;
	}
	
	@NotNull
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLERS;
	}
}