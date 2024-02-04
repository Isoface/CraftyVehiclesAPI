package es.outlook.adriansrj.cv.api.handler;

import es.outlook.adriansrj.cv.api.vehicle.Vehicle;
import es.outlook.adriansrj.cv.api.vehicle.configuration.VehicleConfiguration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * @author AdrianSR / 24/11/2023 / 4:19 p. m.
 */
public interface VehicleHandler extends PluginHandler {
	
	@NotNull Set < Vehicle > getRegisteredVehicles ( );
	
	@Nullable Vehicle getVehicleByOperator ( @NotNull Entity operator );
	
	/**
	 * Spawns a vehicle at the specified word and coordinates.
	 * <br>
	 * Note that it is guaranteed that the returned vehicle by this method
	 * is already <b>registered in this handler</b>.
	 * <br>
	 * @param configuration the configuration for the vehicle to spawn.
	 * @param world         the world to spawn the vehicle at.
	 * @param x             coordinate component.
	 * @param y             coordinate component.
	 * @param z             coordinate component.
	 * @return the spawned and registered vehicle instance.
	 */
	@NotNull Vehicle spawnVehicle ( @NotNull VehicleConfiguration configuration ,
			@NotNull World world , double x , double y , double z );
	
	default @NotNull Vehicle spawnVehicle ( @NotNull VehicleConfiguration configuration ,
			@NotNull Location location ) {
		World world = Objects.requireNonNull (
				location.getWorld ( ) ,
				"the provided location returned a null world"
		);
		
		return spawnVehicle (
				configuration , world ,
				location.getX ( ) , location.getY ( ) , location.getZ ( )
		);
	}
	
	/**
	 * Destroys/removes and unregisters from this handler the specified vehicle.
	 * <br>
	 * @param vehicle the vehicle to destroy/remove.
	 */
	void destroyVehicle ( @NotNull Vehicle vehicle );
	
	void register ( @NotNull Vehicle vehicle );
	
	void unregister ( @NotNull Vehicle vehicle );
}