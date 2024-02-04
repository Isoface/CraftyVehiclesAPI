package es.outlook.adriansrj.cv.api.vehicle;

import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleSeatConfiguration;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 24/11/2023 / 11:47 a. m.
 */
public interface VehicleSeat {
	
	@NotNull VehicleSeatConfiguration getConfiguration ( );
	
	@NotNull Vehicle getVehicle ( );
	
	/**
	 * Gets whether this seat is the "main seat",
	 * meaning that is the seat of the operator.
	 * <br>
	 * @return whether this seat is the main seat.
	 */
	boolean isMain ( );
	
	// -- spawning
	
	boolean isSpawned ( );
	
	boolean isDestroyed ( );
	
	void spawn ( );
	
	void destroy ( );
	
	// -- location
	
	double getX ( );
	
	double getY ( );
	
	double getZ ( );
	
	float getRotation ( );
	
	// -- passenger
	
	@Nullable Entity getPassenger ( );
	
	/**
	 * Sets the passenger for this seat.
	 * <br>
	 * <b>Note that <code>null</code> will be passed if the passenger
	 * is a <code>Player</code> and exits the seat</b>
	 * <br>
	 * <b>Note that this method is always supposed to
	 * be invoked from server thread</b>.
	 * <br>
	 * @param passenger the passenger for this sat.
	 */
	void setPassenger ( @Nullable Entity passenger );
	
	void moveToWorld ( @NotNull World world );
}