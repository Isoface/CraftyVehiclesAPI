package es.outlook.adriansrj.cv.api.vehicle;

import es.outlook.adriansrj.cv.api.enums.EnumSurface;
import es.outlook.adriansrj.cv.api.interfaces.Tickable;
import es.outlook.adriansrj.cv.api.vehicle.configuration.VehicleConfiguration;
import es.outlook.adriansrj.cv.api.vehicle.controller.VehicleController;
import es.outlook.adriansrj.cv.api.vehicle.input.PlayerSteerInput;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author AdrianSR / 22/11/2023 / 10:27 p. m.
 */
public interface Vehicle extends Tickable {
	
	@NotNull UUID getUniqueId ( );
	
	@NotNull VehicleConfiguration getConfiguration ( );
	
	@NotNull VehicleHitBox getCurrentHitBox ( );
	
	@NotNull World getWorld ( );
	
	@NotNull VehicleState getCurrentState ( );
	
	boolean isDestroyed ( );
	
	// -- fuel
	
	default float getFuelCapacity ( ) {
		return getConfiguration ( ).getFuel ( ).getCapacity ( );
	}
	
//	/**
//	 * Gets the fuel level of this vehicle in a value that
//	 * ranges from <b>0.0</b> to <b>1.0</b>;
//	 * where <b>1.0</b> <b>is 100%</b>.
//	 * <br>
//	 * @return the fuel level of this vehicle.
//	 */
	float getFuelLevel ( );
	
//	/**
//	 * Sets the fuel level for this vehicle.
//	 * <br>
//	 * The specified <code>fuelLevel</code> is expected
//	 * to be a value between <b>0.0</b> and <b>1.0</b>;
//	 * Where <b>1.0 is 100%</b>.
//	 * <br>
//	 * @param fuelLevel the fuel level (between 0.0 and 1.0)
//	 */
	void setFuelLevel ( float fuelLevel );
	
	default void addFuel ( float amount ) {
		float current = getFuelLevel ( );
		
		setFuelLevel ( current + amount );
	}
	
	/**
	 * Consumes the specified amount of fuel.
	 * <br>
	 * This is the equivalent of:
	 * <br>
	 * <pre><code>
	 *     float currentFuelLevel = vehicle.getFuelLevel();
	 *     float consumption = ...
	 *
	 *     vehicle.setFuel(currentFuelLevel - consumption);
	 * </code></pre>
	 * @param consumption how much to consume.
	 */
	default void consumeFuel ( float consumption ) {
		float current = getFuelLevel ( );
		
		setFuelLevel ( current - consumption );
	}
	
	// -- owner
	
	@Nullable UUID getOwnerUniqueId ( );
	
	default @Nullable Player getOwner ( ) {
		UUID uniqueId = getOwnerUniqueId ( );
		
		return uniqueId != null ? Bukkit.getPlayer ( uniqueId ) : null;
	}
	
	default boolean hasOwner ( ) {
		return getOwnerUniqueId ( ) != null;
	}
	
	void setOwner ( @Nullable UUID ownerUniqueId );
	
	default void setOwner ( @Nullable Player owner ) {
		setOwner ( owner != null ? owner.getUniqueId ( ) : null );
	}
	
	// -- persistence
	
	boolean isPersistent ( );
	
	void setPersistent ( boolean persistent );
	
	// -- surface
	
	@NotNull Map < EnumSurface, Double > getCurrentSurface ( @Nullable Collection < EnumSurface > ignore );
	
	default @NotNull Map < EnumSurface, Double > getCurrentSurface ( ) {
		return getCurrentSurface ( null );
	}
	
	boolean isOnGround ( );
	
	boolean isOnSurfaces ( @NotNull EnumSurface... surfaces );
	
	boolean isOnAnySurface ( @NotNull EnumSurface... surfaces );
	
	boolean isOnSurface ( @NotNull EnumSurface surface );
	
	boolean isOnSolidSurface ( );
	
	boolean isOnWaterSurface ( );
	
	boolean isOnLavaSurface ( );
	
	boolean isOnLiquidSurface ( );
	
	boolean isInTheAir ( );
	
	boolean containedWithin ( @NotNull EnumSurface... surfaces );
	
	boolean containsAnyWithin ( @NotNull EnumSurface... surfaces );
	
	// -- seat
	
	@NotNull Set < ? extends VehicleSeat > getSeats ( );
	
	@NotNull VehicleSeat getMainSeat ( );
	
	@Nullable Entity getOperator ( );
	
	void setOperator ( @Nullable Entity operator );
	
	// -- controllers
	
	boolean hasController ( @NotNull VehicleController controller );
	
	void addController ( @NotNull VehicleController controller );
	
	void removeController ( @NotNull VehicleController controller );
	
	// -- state
	
	void setState ( @NotNull VehicleState state );
	
	// -- location & rotation
	
	@NotNull Location getLocation ( );
	
	double getX ( );
	
	double getY ( );
	
	double getZ ( );
	
	float getRotation ( );
	
	void setLocation ( double x , double y , double z );
	
	void setRotation ( float rotation );
	
	void setLocationAndRotation ( double x , double y , double z , float rotation );
	
	// -- world
	
	void moveToWorld ( @NotNull World world , double x , double y , double z );
	
	// -- momentum
	
	double getMomentumX ( );
	
	double getMomentumY ( );
	
	double getMomentumZ ( );
	
	void setMomentumX ( double momentumX );
	
	void setMomentumY ( double momentumY );
	
	void setMomentumZ ( double momentumZ );
	
	void setMomentum ( double x , double y , double z );
	
	void addMomentumX ( double momentumX );
	
	void addMomentumY ( double momentumY );
	
	void addMomentumZ ( double momentumZ );
	
	void addMomentum ( double x , double y , double z );
	
	// -- input
	
	void input ( @NotNull PlayerSteerInput input );
}