package es.outlook.adriansrj.cv.api.event;

import es.outlook.adriansrj.cv.api.vehicle.Vehicle;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * Fired whenever the operator of a vehicle changes.
 * <br>
 * @author AdrianSR / 25/11/2023 / 12:15 p. m.
 */
public class VehicleOperatorSetEvent extends VehicleEvent {
	
	private static final HandlerList HANDLERS = new HandlerList ( );
	
	@NotNull
	public static HandlerList getHandlerList ( ) {
		return HANDLERS;
	}
	
	private final @Nullable @Getter Entity previousOperator;
	private final @Nullable @Getter Entity newOperator;
	
	public VehicleOperatorSetEvent ( @NotNull Vehicle vehicle ,
			@Nullable Entity previousOperator ,
			@Nullable Entity newOperator ) {
		super ( vehicle , false );
		
		this.previousOperator = previousOperator;
		this.newOperator      = newOperator;
	}
	
	@NotNull
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLERS;
	}
}