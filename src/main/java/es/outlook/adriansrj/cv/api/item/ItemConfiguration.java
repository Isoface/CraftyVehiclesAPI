package es.outlook.adriansrj.cv.api.item;

import es.outlook.adriansrj.cv.api.CraftyVehicles;
import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.interfaces.IDeyed;
import es.outlook.adriansrj.cv.api.registry.Registries;
import es.outlook.adriansrj.cv.api.util.ConfigurationUtil;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.util.LangFormatter;
import es.outlook.adriansrj.cv.api.util.inventory.ItemStackUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AdrianSR / 31/1/2024 / 2:02 p. m.
 */
@Getter
@Builder
@AllArgsConstructor
public class ItemConfiguration implements IDeyed, ConfigurationSectionWritable {
	
	private static final String SPAWN_ACTION_TYPE = "spawn";
	private static final String FUEL_ACTION_TYPE  = "fuel";
	
	public static @NotNull ItemStack buildItemStack ( @NotNull ItemConfiguration configuration ) {
		ItemStack item = ItemStackUtil.buildCustomItem (
				configuration.material ,
				configuration.customModelData ,
				configuration.displayName ,
				configuration.description
		);
		
		if ( ItemStackUtil.isHead ( configuration.material )
				&& StringUtils.isNotBlank ( configuration.headTexture ) ) {
			CraftyVehicles.getTexturedHeadService ( )
					.applyTexture ( item , configuration.headTexture );
		}
		
		// storing vehicle id
		ItemStackUtil.setPersistentData (
				item , Constants.NamespacedKeys.ITEM_ID ,
				PersistentDataType.STRING , configuration.id
		);
		
		return item;
	}
	
	public static @NotNull ItemStack buildFuelItemStack (
			@NotNull ItemConfiguration configuration , float remainingFuel ) {
		ItemStack item = buildItemStack ( configuration );
		
		// display fuel left on display name and lore
		if ( configuration.displayName != null ) {
			ItemStackUtil.setDisplayName ( item , formatFuel (
					configuration.displayName , remainingFuel
			) );
		}
		
		if ( configuration.description != null ) {
			List < String > lore = new ArrayList <> ( );
			configuration.description.forEach ( line -> lore.add (
					formatFuel ( line , remainingFuel )
			) );
			
			ItemStackUtil.setLore ( item , lore );
		}
		
		return item;
	}
	
	private static String formatFuel ( @NotNull String context , float fuel ) {
		return LangFormatter
				.single ( context )
				.arg ( 'f' , String.format ( "%.2f" , fuel ) )
				.format ( );
	}
	
	/**
	 * @author AdrianSR / 1/2/2024 / 1:57 a. m.
	 */
	public static abstract class Action implements ConfigurationSectionWritable {
		
		public static Action load ( @NotNull ConfigurationSection section ) {
			String type = section.getString ( Constants.Key.TYPE );
			
			if ( type != null ) {
				switch ( type.toLowerCase ( ).trim ( ) ) {
					case SPAWN_ACTION_TYPE:
						return new SpawnAction ( section );
					case FUEL_ACTION_TYPE:
						return new FuelAction ( section );
				}
			}
			
			return null;
		}
		
		abstract @NotNull String getTypeIdentifier ( );
		
		@Override
		public void write ( @NotNull ConfigurationSection section ) {
			section.set ( Constants.Key.TYPE , getTypeIdentifier ( ).trim ( ).toLowerCase ( ) );
		}
	}
	
	/**
	 * @author AdrianSR / 1/2/2024 / 1:57 a. m.
	 */
	@Getter
	@AllArgsConstructor
	public static class SpawnAction extends Action {
		
		private final @NotNull String vehicleId;
		
		public SpawnAction ( @NotNull ConfigurationSection section ) {
			this.vehicleId = section.getString ( Constants.Key.VEHICLE_ID , "" );
		}
		
		@Override
		@NotNull String getTypeIdentifier ( ) {
			return SPAWN_ACTION_TYPE;
		}
		
		@Override
		public void write ( @NotNull ConfigurationSection section ) {
			super.write ( section );
			
			section.set ( Constants.Key.VEHICLE_ID , vehicleId );
		}
	}
	
	/**
	 * @author AdrianSR / 1/2/2024 / 2:05 a. m.
	 */
	@Getter
	@AllArgsConstructor
	public static class FuelAction extends Action {
		
		private final float fuelAmount;
		
		public FuelAction ( @NotNull ConfigurationSection section ) {
			this.fuelAmount = ( float ) section.getDouble ( Constants.Key.FUEL_AMOUNT );
		}
		
		@Override
		@NotNull String getTypeIdentifier ( ) {
			return FUEL_ACTION_TYPE;
		}
		
		@Override
		public void write ( @NotNull ConfigurationSection section ) {
			super.write ( section );
			
			section.set ( Constants.Key.FUEL_AMOUNT , fuelAmount );
		}
	}
	
	public static @Nullable String getItemId ( @NotNull ItemStack vehicleItem ) {
		return ItemStackUtil.getPersistentData (
				vehicleItem ,
				Constants.NamespacedKeys.ITEM_ID ,
				PersistentDataType.STRING
		);
	}
	
	public static @Nullable ItemConfiguration matchItemConfiguration ( @NotNull ItemStack vehicleItem ) {
		String itemId = getItemId ( vehicleItem );
		
		if ( StringUtils.isNotBlank ( itemId ) ) {
			return Registries.getRegistry ( ItemConfiguration.class ).get ( itemId );
		} else {
			return null;
		}
	}
	
	public static ItemConfiguration load ( @NotNull ConfigurationSection section )
			throws InvalidConfigurationException {
		Material material = ConfigurationUtil.loadEnum (
				Material.class , section , Constants.Key.MATERIAL );
		
		if ( material == null ) {
			throw new InvalidConfigurationException (
					"invalid material: " + section.getString ( Constants.Key.MATERIAL ) );
		}
		
		String          displayName = section.getString ( Constants.Key.DISPLAY_NAME );
		List < String > description = section.getStringList ( Constants.Key.DESCRIPTION );
		String          headTexture = section.getString ( Constants.Key.HEAD_TEXTURE );
		
		// custom model data
		Object customModelDataRaw = section.get ( Constants.Key.CUSTOM_MODEL_DATA );
		Integer customModelData = customModelDataRaw instanceof Number
				? ( ( Number ) customModelDataRaw ).intValue ( ) : null;
		
		// action
		ConfigurationSection actionSection = section
				.getConfigurationSection ( Constants.Key.ACTION );
		Action action = actionSection != null ? Action.load ( actionSection ) : null;
		
		return new ItemConfiguration (
				IDeyed.loadId ( section ) , material ,
				displayName , description , customModelData ,
				headTexture , action
		);
	}
	
	private final @NotNull  String          id;
	private final @NotNull  Material        material;
	private final @Nullable String          displayName;
	private final @Nullable List < String > description;
	private final @Nullable Integer         customModelData;
	private final @Nullable String          headTexture;
	
	private final @Nullable Action action;
	
	@Override
	public @NotNull String getId ( ) {
		return id;
	}
	
	public @NotNull ItemStack getItemStack ( ) {
		if ( action instanceof FuelAction ) {
			return buildFuelItemStack ( this , ( ( FuelAction ) action ).fuelAmount );
		} else {
			return buildItemStack ( this );
		}
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		IDeyed.writeId ( this , section );
		ConfigurationUtil.writeEnum ( material , section , Constants.Key.MATERIAL );
		
		section.set ( Constants.Key.DISPLAY_NAME , displayName );
		section.set ( Constants.Key.DESCRIPTION , description != null
				&& description.size ( ) > 0 ? description : null );
		section.set ( Constants.Key.CUSTOM_MODEL_DATA , customModelData );
		section.set ( Constants.Key.HEAD_TEXTURE , headTexture );
		
		if ( action != null ) {
			action.write ( section.createSection ( Constants.Key.ACTION ) );
		}
	}
}