package es.outlook.adriansrj.cv.api.vehicle.configuration;

import es.outlook.adriansrj.cv.api.enums.EnumExitShortcut;
import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.interfaces.IDeyed;
import es.outlook.adriansrj.cv.api.interfaces.Named;
import es.outlook.adriansrj.cv.api.item.ItemConfiguration;
import es.outlook.adriansrj.cv.api.registry.Registries;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.vehicle.configuration.model.VehicleModelConfiguration;
import lombok.Builder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AdrianSR / 23/11/2023 / 12:02 p. m.
 */
public class VehicleConfiguration implements IDeyed, Named, ConfigurationSectionWritable {
	
	public static VehicleConfiguration load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		String id   = IDeyed.loadId ( section );
		String name = Named.loadName ( section , id );
		
		// exit shortcut.
		// meanwhile the exit shortcut for the operator must be set,
		// the exit shortcut for the passenger is not required here;
		// and if it is not set, the operator exit shortcut will
		// be used instead
		EnumExitShortcut operatorExitShortcut = ConfigurationUtil.loadEnum (
				EnumExitShortcut.class , section , Constants.Key.OPERATOR_EXIT_SHORTCUT );
		EnumExitShortcut passengerExitShortcut = ConfigurationUtil.loadEnum (
				EnumExitShortcut.class , section , Constants.Key.PASSENGER_EXIT_SHORTCUT );
		
		if ( operatorExitShortcut == null ) {
			throw new InvalidConfigurationException ( "invalid exit shortcut" );
		}
		
		if ( passengerExitShortcut == null ) {
			passengerExitShortcut = operatorExitShortcut;
		}
		
		// finding model
		String modelId = section.getString ( Constants.Key.MODEL );
		
		if ( StringUtils.isBlank ( modelId ) ) {
			throw new InvalidConfigurationException ( "invalid model id" );
		}
		
		VehicleModelConfiguration model = Registries
				.getRegistry ( VehicleModelConfiguration.class )
				.get ( IDeyed.idCheck ( modelId.toLowerCase ( ) ) );
		
		if ( model == null ) {
			throw new InvalidConfigurationException ( "the model '" + modelId + "' is unknown" );
		}
		
		// pickup-item
		String pickupItemId = section.getString ( Constants.Key.PICKUP_ITEM );
		ItemConfiguration pickupItem = pickupItemId != null ? Registries
				.getRegistry ( ItemConfiguration.class )
				.get ( pickupItemId ) : null;
		
		// physics
		ConfigurationSection physicsSection = section
				.getConfigurationSection ( Constants.Key.PHYSICS );
		VehiclePhysicsConfiguration physics;
		
		if ( physicsSection != null ) {
			physics = VehiclePhysicsConfiguration.load ( physicsSection );
		} else {
			physics = VehiclePhysicsConfiguration.DEFAULTS;
		}
		
		// fuel configuration
		ConfigurationSection fuelSection = section
				.getConfigurationSection ( Constants.Key.FUEL );
		VehicleFuelConfiguration fuel;
		
		if ( fuelSection != null ) {
			fuel = VehicleFuelConfiguration.load ( fuelSection );
		} else {
			fuel = VehicleFuelConfiguration.DEFAULTS;
		}
		
		// controllers
		ConfigurationSection controllersSection = section
				.getConfigurationSection ( Constants.Key.CONTROLLERS );
		VehicleControllersConfiguration controllers;
		
		if ( controllersSection != null ) {
			controllers = VehicleControllersConfiguration.load ( controllersSection );
		} else {
			controllers = VehicleControllersConfiguration.EMPTY;
		}
		
		return new VehicleConfiguration (
				id , name , operatorExitShortcut , passengerExitShortcut ,
				model , pickupItem , physics , fuel , controllers
		);
	}
	
	private final @NotNull  String                    id;
	private final @NotNull  String                    name;
	private final @NotNull  EnumExitShortcut          operatorExitShortcut;
	private final @NotNull  EnumExitShortcut          passengerExitShortcut;
	private final @NotNull  VehicleModelConfiguration model;
	private final @Nullable ItemConfiguration         pickupItem;
	
	private final @NotNull VehiclePhysicsConfiguration     physics;
	private final @NotNull VehicleFuelConfiguration        fuel;
	private final @NotNull VehicleControllersConfiguration controllers;
	
	@Builder
	public VehicleConfiguration ( @NotNull String id , @NotNull String name ,
			@NotNull EnumExitShortcut operatorExitShortcut ,
			@NotNull EnumExitShortcut passengerExitShortcut ,
			@NotNull VehicleModelConfiguration model ,
			@Nullable ItemConfiguration pickupItem ,
			@Nullable VehiclePhysicsConfiguration physics ,
			@Nullable VehicleFuelConfiguration fuel ,
			@Nullable VehicleControllersConfiguration controllers ) {
		this.id                    = IDeyed.idCheck ( id.toLowerCase ( ) );
		this.name                  = Named.nameCheck ( name );
		this.operatorExitShortcut  = operatorExitShortcut;
		this.passengerExitShortcut = passengerExitShortcut;
		this.model                 = model;
		this.pickupItem            = pickupItem;
		
		this.physics     = physics != null ? physics : VehiclePhysicsConfiguration.DEFAULTS;
		this.fuel        = fuel != null ? fuel : VehicleFuelConfiguration.DEFAULTS;
		this.controllers = controllers != null ? controllers : VehicleControllersConfiguration.EMPTY;
	}
	
	@Override
	public @NotNull String getId ( ) {
		return id;
	}
	
	@Override
	public @NotNull String getName ( ) {
		return name;
	}
	
	public @NotNull EnumExitShortcut getOperatorExitShortcut ( ) {
		return operatorExitShortcut;
	}
	
	public @NotNull EnumExitShortcut getPassengerExitShortcut ( ) {
		return passengerExitShortcut;
	}
	
	public @NotNull VehicleModelConfiguration getModel ( ) {
		return model;
	}
	
	public @Nullable ItemConfiguration getPickupItem ( ) {
		return pickupItem;
	}
	
	public @NotNull VehiclePhysicsConfiguration getPhysics ( ) {
		return physics;
	}
	
	public @NotNull VehicleFuelConfiguration getFuel ( ) {
		return fuel;
	}
	
	public @NotNull VehicleControllersConfiguration getControllers ( ) {
		return controllers;
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		IDeyed.writeId ( this , section );
		Named.writeName ( this , section );
		
		ConfigurationUtil.writeEnum (
				operatorExitShortcut ,
				section , Constants.Key.OPERATOR_EXIT_SHORTCUT );
		
		ConfigurationUtil.writeEnum (
				passengerExitShortcut ,
				section , Constants.Key.PASSENGER_EXIT_SHORTCUT );
		
		section.set ( Constants.Key.MODEL , model.getId ( ) );
		
		if ( pickupItem != null ) {
			section.set ( Constants.Key.PICKUP_ITEM , pickupItem.getId ( ) );
		}
		
		physics.write ( section.createSection ( Constants.Key.PHYSICS ) );
		fuel.write ( section.createSection ( Constants.Key.FUEL ) );
		controllers.write ( section.createSection ( Constants.Key.CONTROLLERS ) );
	}
}