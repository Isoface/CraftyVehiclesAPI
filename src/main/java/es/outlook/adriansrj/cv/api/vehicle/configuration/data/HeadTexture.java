package es.outlook.adriansrj.cv.api.vehicle.configuration.data;

import es.outlook.adriansrj.cv.api.CraftyVehicles;
import es.outlook.adriansrj.cv.api.util.inventory.ItemStackUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AdrianSR / 28/1/2024 / 12:36 a. m.
 */
public class HeadTexture {
	
	private final @Getter String value;
	
	public HeadTexture ( String value ) {
		this.value = value;
	}
	
	public ItemStack getItemStack ( @Nullable Integer customModelData ) {
		ItemStack item = ItemStackUtil.buildCustomItem (
				Material.PLAYER_HEAD , customModelData );
		applyTexture ( item );
		
		return item;
	}
	
	public ItemStack getItemStack ( ) {
		return getItemStack ( null );
	}
	
	public void applyTexture ( @NotNull ItemStack item ) {
		CraftyVehicles.getTexturedHeadService ( ).applyTexture ( item , value );
	}
}