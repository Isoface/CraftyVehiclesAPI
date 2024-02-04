package es.outlook.adriansrj.cv.api.vehicle.configuration.data;

import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author AdrianSR / 28/1/2024 / 12:56 a. m.
 */
public class BannerStyle {
	
	private final List < Pattern > patterns = new ArrayList <> ( );
	
	public BannerStyle ( @NotNull List < Pattern > patterns ) {
		this.patterns.addAll ( patterns );
	}
	
	public boolean isEmpty ( ) {
		return patterns.isEmpty ( );
	}
	
	public @NotNull List < Pattern > getPatterns ( ) {
		return patterns;
	}
	
	public void applyStyle ( @NotNull ItemStack item ) {
		ItemMeta meta = item.getItemMeta ( );
		
		if ( meta instanceof BannerMeta ) {
			BannerMeta bannerMeta = ( BannerMeta ) meta;
			bannerMeta.setPatterns ( patterns );
			
			item.setItemMeta ( bannerMeta );
		}
	}
}