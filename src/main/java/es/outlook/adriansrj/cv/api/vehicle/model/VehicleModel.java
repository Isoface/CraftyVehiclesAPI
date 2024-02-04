package es.outlook.adriansrj.cv.api.vehicle.model;

import es.outlook.adriansrj.cv.api.interfaces.Tickable;
import es.outlook.adriansrj.cv.api.vehicle.VehicleState;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleModelConfiguration;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * @author AdrianSR / 23/12/2023 / 2:27 p. m.
 */
public interface VehicleModel < C extends VehicleModelConfiguration > extends Tickable {
	
	@NotNull C getConfiguration ( );
	
	// -- spawning
	
	boolean isSpawned ( );
	
	void spawn ( );
	
	void destroy ( );
	
	// -- state
	
	void setState ( @Nullable VehicleState state );
	
	// -- location
	
	@NotNull Location getLocation ( );
	
	@NotNull World getWorld ( );
	
	double getX ( );
	
	double getY ( );
	
	double getZ ( );
	
	float getRotation ( );
	
	void setLocationAndRotation ( double x , double y , double z , float rotation );
	
	void setLocation ( double x , double y , double z );
	
	void setRotation ( float rotation );
}