package es.outlook.adriansrj.cv.api.event;

import es.outlook.adriansrj.cv.api.vehicle.Vehicle;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 1/2/2024 / 10:39 a. m.
 */
public class VehicleClickedEvent extends VehicleEvent implements Cancellable {
	
	/**
	 * @author AdrianSR / 1/2/2024 / 10:40 a. m.
	 */
	public enum ClickType {
		
		LEFT_CLICK,
		LEFT_CLICK_CROUCHING,
		
		RIGHT_CLICK,
		RIGHT_CLICK_CROUCHING;
		
		public boolean isLeftClick ( ) {
			return this == LEFT_CLICK || this == LEFT_CLICK_CROUCHING;
		}
		
		public boolean isRightClick ( ) {
			return this == RIGHT_CLICK || this == RIGHT_CLICK_CROUCHING;
		}
	}
	
	private static final HandlerList HANDLERS = new HandlerList ( );
	
	@NotNull
	public static HandlerList getHandlerList ( ) {
		return HANDLERS;
	}
	
	private final @Getter @NotNull Player    player;
	private final @Getter @NotNull ClickType clickType;
	
	private boolean cancelled;
	
	public VehicleClickedEvent ( @NotNull Vehicle vehicle ,
			@NotNull Player player , @NotNull ClickType clickType ) {
		super ( vehicle );
		this.player    = player;
		this.clickType = clickType;
	}
	
	@Override
	public boolean isCancelled ( ) {
		return cancelled;
	}
	
	@Override
	public void setCancelled ( boolean cancel ) {
		this.cancelled = cancel;
	}
	
	@NotNull
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLERS;
	}
}