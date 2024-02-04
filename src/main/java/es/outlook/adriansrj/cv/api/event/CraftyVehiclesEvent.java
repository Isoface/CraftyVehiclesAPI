package es.outlook.adriansrj.cv.api.event;

import es.outlook.adriansrj.cv.api.util.Run;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author AdrianSR / 25/11/2023 / 12:34 p. m.
 */
public abstract class CraftyVehiclesEvent extends Event {
	
	public CraftyVehiclesEvent ( ) {
		super ( );
	}
	
	public CraftyVehiclesEvent ( boolean isAsync ) {
		super ( isAsync );
	}
	
	public boolean callEvent ( ) {
		org.bukkit.Bukkit.getPluginManager ( ).callEvent ( this );
		
		if ( this instanceof Cancellable ) {
			return !( ( Cancellable ) this ).isCancelled ( );
		} else {
			return true;
		}
	}
	
	public void callEventSynchronously ( @Nullable Consumer < Boolean > resultConsumer ) {
		if ( isAsynchronous ( ) ) {
			throw new IllegalStateException (
					getEventName ( ) + " cannot be triggered asynchronously" +
							" from primary server thread." );
		}
		
		if ( Bukkit.isPrimaryThread ( ) ) {
			boolean result = callEvent ( );
			
			if ( resultConsumer != null ) {
				resultConsumer.accept ( result );
			}
		} else {
			Run.sync ( ( ) -> {
				boolean result = callEvent ( );
				
				if ( resultConsumer != null ) {
					resultConsumer.accept ( result );
				}
			} );
		}
	}
}