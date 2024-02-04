package es.outlook.adriansrj.cv.api.util.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AdrianSR / 23/11/2023 / 1:43 p. m.
 */
public final class ItemStackUtil {
	
	public static boolean isBanner ( @NotNull Material material ) {
		if ( material.isBlock ( ) && material.name ( ).endsWith ( "_BANNER" ) ) {
			return true;
		} else {
			return Bukkit.getItemFactory ( ).getItemMeta ( material ) instanceof BannerMeta;
		}
	}
	
	public static boolean isHead ( @NotNull Material material ) {
		if ( material.isBlock ( ) && material.name ( ).endsWith ( "_HEAD" ) ) {
			return true;
		} else {
			return Bukkit.getItemFactory ( ).getItemMeta ( material ) instanceof SkullMeta;
		}

//		return material == Material.PLAYER_HEAD
//				|| material == Material.PLAYER_WALL_HEAD
//				|| material == Material.CREEPER_HEAD
//				|| material == Material.CREEPER_WALL_HEAD
//				|| material == Material.DRAGON_HEAD
//				|| material == Material.DRAGON_WALL_HEAD
//				|| material == Material.ZOMBIE_HEAD
//				|| material == Material.ZOMBIE_WALL_HEAD;
	}
	
	public static @NotNull ItemStack setDisplayName (
			@NotNull ItemStack item , @NotNull String displayName ) {
		ItemMeta meta = item.getItemMeta ( );
		
		if ( meta != null ) {
			meta.setDisplayName ( colorize ( displayName ) );
			item.setItemMeta ( meta );
		}
		
		return item;
	}
	
	public static @NotNull ItemStack setLore (
			@NotNull ItemStack item , @NotNull List < String > lore ) {
		ItemMeta meta = item.getItemMeta ( );
		
		if ( meta != null ) {
			meta.setLore ( colorize ( lore ) );
			item.setItemMeta ( meta );
		}
		
		return item;
	}
	
	public static @NotNull ItemStack buildCustomItem (
			@NotNull Material material , @Nullable Integer customModelData ) {
		ItemStack result = new ItemStack ( material );
		
		if ( customModelData != null ) {
			ItemMeta meta = result.getItemMeta ( );
			
			if ( meta == null ) {
				return result; // not "metadatable"
			}
			
			try {
				Method method = ItemMeta.class.getMethod (
						"setCustomModelData" , Integer.class );
				
				try {
					method.setAccessible ( true );
					method.invoke ( meta , customModelData );
				} catch ( IllegalAccessException | InvocationTargetException e ) {
					e.printStackTrace ( );
				}
			} catch ( NoSuchMethodException ex ) { // pre 1.14
				result.setDurability ( ( short ) customModelData.intValue ( ) );
			}
			
			result.setItemMeta ( meta );
		}
		
		return result;
	}
	
	public static @NotNull ItemStack buildCustomItem (
			@NotNull Material material , @Nullable Integer customModelData ,
			@Nullable String displayName , @Nullable List < String > description ) {
		ItemStack result = buildCustomItem ( material , customModelData );
		ItemMeta  meta   = result.getItemMeta ( );
		
		if ( meta != null ) {
			meta.setDisplayName ( colorize ( displayName ) );
			meta.setLore ( colorize ( description ) );
			
			result.setItemMeta ( meta );
		}
		
		return result;
	}
	
	public static @Nullable < T, Z > Z getPersistentData ( @NotNull ItemStack source ,
			@NotNull NamespacedKey key , @NotNull PersistentDataType < T, Z > type ) {
		ItemMeta                meta      = source.getItemMeta ( );
		PersistentDataContainer container = meta != null ? meta.getPersistentDataContainer ( ) : null;
		
		return container != null ? container.get ( key , type ) : null;
	}
	
	public static < T, Z > @NotNull ItemStack setPersistentData ( @NotNull ItemStack itemStack ,
			@NotNull NamespacedKey key , @NotNull PersistentDataType < T, Z > type , @NotNull Z value ) {
		ItemMeta meta = itemStack.getItemMeta ( );
		
		if ( meta != null || ( ( meta = Bukkit.getItemFactory ( )
				.getItemMeta ( itemStack.getType ( ) ) ) != null ) ) {
			meta.getPersistentDataContainer ( ).set ( key , type , value );
			
			itemStack.setItemMeta ( meta );
		}
		
		return itemStack;
	}
	
	// -- internal
	
	private static @Nullable String colorize ( @Nullable String string ) {
		if ( string != null ) {
			return ChatColor.translateAlternateColorCodes ( '&' , string );
		} else {
			return null;
		}
	}
	
	private static @Nullable List < String > colorize ( @Nullable List < String > stringList ) {
		if ( stringList == null ) {
			return null;
		}
		
		List < String > result = new ArrayList <> ( );
		
		for ( String string : stringList ) {
			result.add ( colorize ( string ) );
		}
		
		return result;
	}
}