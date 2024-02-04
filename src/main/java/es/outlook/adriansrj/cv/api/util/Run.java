package es.outlook.adriansrj.cv.api.util;

import es.outlook.adriansrj.cv.api.CraftyVehicles;
import org.bukkit.Bukkit;

/**
 * @author AdrianSR / 8/12/2023 / 9:22 p. m.
 */
public final class Run {
	
	public static void sync ( Runnable runnable ) {
		if ( Bukkit.isPrimaryThread ( ) ) {
			runnable.run ( );
		} else {
			Bukkit.getScheduler ( ).runTask ( CraftyVehicles.getPlugin ( ) , runnable );
		}
	}
	
	public static void syncDelayed ( Runnable runnable , long delay ) {
		Bukkit.getScheduler ( ).scheduleSyncDelayedTask (
				CraftyVehicles.getPlugin ( ) , runnable , delay
		);
	}
	
	public static void syncDelayed ( Runnable runnable ) {
		Bukkit.getScheduler ( ).scheduleSyncDelayedTask (
				CraftyVehicles.getPlugin ( ) , runnable
		);
	}
}
