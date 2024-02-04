package es.outlook.adriansrj.cv.api.item;

import es.outlook.adriansrj.cv.api.CraftyVehicles;
import es.outlook.adriansrj.cv.api.interfaces.ConfigurationSectionWritable;
import es.outlook.adriansrj.cv.api.registry.Registries;
import es.outlook.adriansrj.cv.api.util.Constants;
import es.outlook.adriansrj.cv.api.util.reflection.EnumReflection;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author AdrianSR / 31/1/2024 / 2:04 p. m.
 */
public class RecipeConfiguration implements ConfigurationSectionWritable {
	
	/**
	 * @author AdrianSR / 31/1/2024 / 2:45 p. m.
	 */
	public static abstract class Shape {
		
		public abstract void write ( @NotNull ConfigurationSection section , @NotNull String key );
	}
	
	/**
	 * @author AdrianSR / 31/1/2024 / 2:17 p. m.
	 */
	public static class Shaped extends Shape {
		
		public static Shaped parse ( @NotNull ConfigurationSection section , @NotNull String key )
				throws InvalidConfigurationException {
			List < String > serialized = section.getStringList ( key );
			String[]        value      = new String[ 9 ];
			boolean         invalid    = true;
			
			for ( int row = 0 ; row < serialized.size ( ) && row < 3 ; row++ ) {
				String rowString = serialized.get ( row );
				
				if ( StringUtils.isBlank ( rowString ) ) {
					continue;
				}
				
				int count = 0;
				
				while ( true ) {
					int startIndex = rowString.indexOf ( '[' );
					int endIndex   = rowString.indexOf ( ']' );
					
					if ( startIndex == -1 || endIndex == -1 ) {
						break; // malformed
					} else if ( startIndex > endIndex ) {
						break; // malformed
					}
					
					String ingredientId = rowString.substring ( startIndex + 1 , endIndex ).trim ( )
							.replace ( "[" , "" )
							.replace ( "]" , "" );
					
					if ( StringUtils.isNotBlank ( ingredientId ) ) {
						invalid                      = false;
						value[ ( row * 3 ) + count ] = ingredientId;
					}
					
					rowString = rowString.length ( ) > endIndex + 1
							? rowString.substring ( endIndex + 1 ) : "";
					
					if ( count + 1 < 3 ) {
						count++;
					} else {
						break;
					}
				}
			}
			
			if ( invalid ) {
				throw new InvalidConfigurationException ( "at least one ingredient must be set" );
			}
			
			return new Shaped ( value );
		}
		
		private final @NotNull String[] value = new String[ 9 ];
		
		public Shaped ( @NotNull String[] value ) throws InvalidConfigurationException {
			if ( value.length != this.value.length ) {
				throw new InvalidConfigurationException ( "invalid value" );
			}
			
			System.arraycopy ( value , 0 , this.value , 0 , this.value.length );
		}
		
		public void write ( @NotNull ConfigurationSection section , @NotNull String key ) {
			List < String > serialized = new ArrayList <> ( );
			
			for ( int row = 0 ; row < 3 ; row++ ) {
				StringBuilder builder = new StringBuilder ( );
				
				for ( int i = 0 ; i < 3 ; i++ ) {
					builder.append ( '[' );
					
					String ingredientId = value[ ( row * 3 ) + i ];
					
					if ( StringUtils.isNotBlank ( ingredientId ) ) {
						builder.append ( ingredientId );
					}
					
					builder.append ( ']' );
				}
				
				serialized.add ( builder.toString ( ) );
			}
			
			section.set ( key , serialized );
		}
	}
	
	/**
	 * @author AdrianSR / 31/1/2024 / 2:17 p. m.
	 */
	public static class Shapeless extends Shape {
		
		public static Shapeless parse ( @NotNull ConfigurationSection section , @NotNull String key )
				throws InvalidConfigurationException {
			List < String > value = new ArrayList <> ( section.getStringList ( key ) );
			
			if ( value.size ( ) == 0 ) {
				throw new InvalidConfigurationException ( "at least one ingredient must be set" );
			}
			
			for ( int i = 0 ; i < value.size ( ) ; i++ ) {
				String ingredient = value.get ( i );
				
				if ( StringUtils.isNotBlank ( ingredient ) ) {
					value.set ( i , ingredient.trim ( ) );
				}
			}
			
			return new Shapeless ( value );
		}
		
		private final @NotNull Set < String > value = new LinkedHashSet <> ( );
		
		public Shapeless ( @NotNull Collection < String > value ) throws InvalidConfigurationException {
			if ( value.size ( ) == 0 ) {
				throw new InvalidConfigurationException ( "invalid value" );
			}
			
			this.value.addAll ( value );
		}
		
		public void write ( @NotNull ConfigurationSection section , @NotNull String key ) {
			section.set ( key , new ArrayList <> ( value ) );
		}
	}
	
	public static RecipeConfiguration load ( ConfigurationSection section )
			throws InvalidConfigurationException {
		String result = section.getString ( Constants.Key.RESULT );
		
		if ( StringUtils.isBlank ( result ) ) {
			throw new InvalidConfigurationException ( "invalid result" );
		}
		
		Shaped    shaped    = null;
		Shapeless shapeless = null;
		
		if ( section.contains ( Constants.Key.SHAPED ) ) {
			shaped = Shaped.parse ( section , Constants.Key.SHAPED );
		} else if ( section.contains ( Constants.Key.SHAPELESS ) ) {
			shapeless = Shapeless.parse ( section , Constants.Key.SHAPELESS );
		}
		
		if ( shaped != null ) {
			return new RecipeConfiguration ( result , shaped );
		} else if ( shapeless != null ) {
			return new RecipeConfiguration ( result , shapeless );
		} else {
			throw new InvalidConfigurationException ( "a valid shape must be set" );
		}
	}
	
	private final @NotNull String result;
	private final @NotNull Shape  shape;
	
	public RecipeConfiguration ( @NotNull String result , @NotNull Shape shape ) {
		this.result = result;
		this.shape  = shape;
	}
	
	public @Nullable Recipe getRecipe ( ) {
		NamespacedKey key = new NamespacedKey (
				CraftyVehicles.getPlugin ( ) ,
				UUID.randomUUID ( ).toString ( )
		);
		
		ItemConfiguration result = Registries
				.getRegistry ( ItemConfiguration.class )
				.get ( this.result );
		
		if ( result == null ) {
			return null;
		}
		
		if ( shape instanceof Shaped ) {
			Shaped       shaped = ( Shaped ) shape;
			ShapedRecipe recipe = new ShapedRecipe ( key , result.getItemStack ( ) );
			
			recipe.shape ( "012" , "345" , "678" );
			
			for ( int i = 0 ; i < shaped.value.length ; i++ ) {
				String       id     = shaped.value[ i ];
				RecipeChoice choice = createChoice ( id );
				
				if ( choice != null ) {
					recipe.setIngredient ( String.valueOf ( i ).charAt ( 0 ) , choice );
				}
			}
			
			return recipe;
		} else if ( shape instanceof Shapeless ) {
			ShapelessRecipe recipe = new ShapelessRecipe ( key , result.getItemStack ( ) );
			
			for ( String id : ( ( Shapeless ) shape ).value ) {
				RecipeChoice choice = createChoice ( id );
				
				if ( choice != null ) {
					recipe.addIngredient ( choice );
				}
			}
			
			return recipe;
		}
		
		return null;
	}
	
	private RecipeChoice createChoice ( String ingredientId ) {
		if ( StringUtils.isBlank ( ingredientId ) ) {
			return null;
		}
		
		ItemConfiguration pluginItem = Registries
				.getRegistry ( ItemConfiguration.class )
				.get ( ingredientId );
		
		if ( pluginItem != null ) {
			ItemStack item = pluginItem.getItemStack ( );
			item.setAmount ( 1 );
			
			return new RecipeChoice.ExactChoice ( item );
		} else {
			Material material = EnumReflection.getEnumConstant (
					Material.class , ingredientId.trim ( ).toUpperCase ( ) );
			
			if ( material != null ) {
				return new RecipeChoice.MaterialChoice ( material );
			}
		}
		
		return null;
	}
	
	@Override
	public void write ( @NotNull ConfigurationSection section ) {
		section.set ( Constants.Key.RESULT , result );
		
		shape.write ( section , shape instanceof Shaped ? Constants.Key.SHAPED
				: Constants.Key.SHAPELESS );
	}
}